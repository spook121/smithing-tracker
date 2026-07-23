package com.smithingtracker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@PluginDescriptor(name = "Smithing Tracker")
public class SmithingTrackerPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private OverlayManager overlayManager;
	@Inject private SmithingTrackerOverlay overlay;
	@Inject private ItemBoxOverlay itemBoxOverlay;
	@Inject private ItemManager itemManager;
	@Inject private SmithingTrackerConfig config;
	@Inject private OkHttpClient okHttpClient;
	@Inject private ScheduledExecutorService executor;

	private SmithingSession session = new SmithingSession();
	private int lastSmithingXp = -1;
	private int trackedItemId = -1;
	private int trackedItemPrice = 0;
	private String trackedItemName = "None";
	private volatile String pendingItemName = null;
	private Instant lastSmithingTime = null;
	private Instant loginTime = null;
	private boolean hasStartedSmithing = false;
	private WorldPoint lastLocation = null;
	private boolean wasMovingLastTick = false;

	private final Map<Integer, Integer> barsUsed = new HashMap<>();
	private final Map<Integer, Integer> barsInBank = new HashMap<>();
	private final Map<Integer, Integer> barsInInventory = new HashMap<>();
	private final Map<Integer, Integer> itemsSmithed = new LinkedHashMap<>();
	private final Map<Integer, Integer> cachedItemTotals = new HashMap<>();

	// Live wiki prices cache - refreshed every 60s from https://prices.runescape.wiki
	private final Map<Integer, Integer> livePrices = new ConcurrentHashMap<>();
	private volatile Instant lastPriceFetch = Instant.EPOCH;

	public static final int BRONZE_BAR = 2349, IRON_BAR = 2351, STEEL_BAR = 2353, MITHRIL_BAR = 2359, ADAMANTITE_BAR = 2361, RUNITE_BAR = 2363;
	private static final int SMITHING_ANIMATION = 898;
	private static final int MAX_REAL_SMITH_XP = 400;
	private static final String WIKI_PRICE_URL = "https://prices.runescape.wiki/api/v1/osrs/latest";

	@Provides SmithingTrackerConfig provideConfig(ConfigManager c) { return c.getConfig(SmithingTrackerConfig.class); }

	@Override protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(itemBoxOverlay);
		resetAll();
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastSmithingXp = client.getSkillExperience(Skill.SMITHING);
			loginTime = Instant.now();
			clientThread.invoke(this::scanForAllSmithableItems);
		}
		// initial fetch + schedule every 60s
		executor.submit(this::fetchLivePrices);
		executor.scheduleWithFixedDelay(this::fetchLivePrices, 60, 60, TimeUnit.SECONDS);
	}
	@Override protected void shutDown() { overlayManager.remove(overlay); overlayManager.remove(itemBoxOverlay); resetAll(); livePrices.clear(); }

	private void resetAll()
	{
		session.reset(); barsUsed.clear(); barsInBank.clear(); barsInInventory.clear(); itemsSmithed.clear(); cachedItemTotals.clear();
		trackedItemName = "None"; trackedItemId = -1; trackedItemPrice = 0; lastSmithingXp = -1; pendingItemName = null; lastSmithingTime = null; loginTime = null; hasStartedSmithing = false; lastLocation = null; wasMovingLastTick = false;
	}

	// Live price fetcher - uses wiki API every 60s
	private void fetchLivePrices()
	{
		try
		{
			Request request = new Request.Builder().url(WIKI_PRICE_URL).header("User-Agent", "SmithingTracker - RuneLite plugin").build();
			try (Response response = okHttpClient.newCall(request).execute())
			{
				if (!response.isSuccessful() || response.body() == null) return;
				String body = response.body().string();
				JsonObject root = new JsonParser().parse(body).getAsJsonObject();
				JsonObject data = root.getAsJsonObject("data");
				if (data == null) return;
				for (Map.Entry<String, JsonElement> entry : data.entrySet())
				{
					try
					{
						int itemId = Integer.parseInt(entry.getKey());
						JsonObject priceObj = entry.getValue().getAsJsonObject();
						int high = priceObj.has("high") && !priceObj.get("high").isJsonNull() ? priceObj.get("high").getAsInt() : 0;
						int low = priceObj.has("low") && !priceObj.get("low").isJsonNull() ? priceObj.get("low").getAsInt() : 0;
						int price = high > 0 ? high : low;
						if (price > 0) livePrices.put(itemId, price);
					}
					catch (Exception ignored) {}
				}
				lastPriceFetch = Instant.now();
				log.debug("Fetched {} live prices from wiki", livePrices.size());
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to fetch wiki prices", e);
		}
	}

	public int getLivePrice(int itemId)
	{
		Integer live = livePrices.get(itemId);
		if (live != null && live > 0) return live;
		try { return itemManager.getItemPrice(itemId); } catch (Exception e) { return 0; }
	}

	public int getEffectiveBarPrice(int barId)
	{
		// If user set a custom buy price for the currently selected bar type, use it for accurate profit
		if (config.customBarCost() > 0 && barId == config.barType().getItemId()) return config.customBarCost();
		// Otherwise use live GE price from wiki (refreshed every 60s)
		return getLivePrice(barId);
	}

	@Subscribe public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOGGED_IN) { lastSmithingXp = client.getSkillExperience(Skill.SMITHING); loginTime = Instant.now(); clientThread.invoke(this::scanForAllSmithableItems); }
		else if (e.getGameState() == GameState.HOPPING || e.getGameState() == GameState.LOGIN_SCREEN) resetAll();
	}

	@Subscribe public void onConfigChanged(ConfigChanged e)
	{
		if (!e.getGroup().equals("smithingtracker")) return;
		if (e.getKey().equals("barType"))
		{
			session.reset();
			trackedItemName = "None";
			trackedItemId = -1;
			trackedItemPrice = 0;
			pendingItemName = null;
			hasStartedSmithing = false;
			barsUsed.clear();
			lastSmithingTime = null;
		}
	}

	@Subscribe public void onChatMessage(ChatMessage e)
	{
		String msg = e.getMessage().toLowerCase();
		if (msg.contains("you hammer") || msg.contains("you make"))
		{
			String clean = msg.replaceAll("<[^>]*>", ""); String[] split = clean.split("make ");
			if (split.length > 1) { String itemPart = split[1].replace("a ", "").replace("an ", "").replace("some ", "").replace(".", "").replace("!", "").trim(); pendingItemName = mapItemName(itemPart); clientThread.invoke(this::resolvePendingItem); }
		}
	}

	private void resolvePendingItem()
	{
		if (pendingItemName == null) return; String s = pendingItemName.toLowerCase().trim();
		for (int i = 0; i < 40000; i++) { try { String n = itemManager.getItemComposition(i).getName(); if (n!= null && n.equalsIgnoreCase(s)) { trackedItemId = i; trackedItemPrice = getLivePrice(i); trackedItemName = n; pendingItemName = null; return; } } catch (Exception ignored) {} }
		pendingItemName = null;
	}

	@Subscribe public void onItemContainerChanged(ItemContainerChanged e)
	{
		if (e.getContainerId() == InventoryID.BANK.getId())
		{
			ItemContainer bank = e.getItemContainer(); barsInBank.clear();
			barsInBank.put(BRONZE_BAR, bank.count(BRONZE_BAR)); barsInBank.put(IRON_BAR, bank.count(IRON_BAR)); barsInBank.put(STEEL_BAR, bank.count(STEEL_BAR)); barsInBank.put(MITHRIL_BAR, bank.count(MITHRIL_BAR)); barsInBank.put(ADAMANTITE_BAR, bank.count(ADAMANTITE_BAR)); barsInBank.put(RUNITE_BAR, bank.count(RUNITE_BAR));
			scanForAllSmithableItems();
		}
		else if (e.getContainerId() == InventoryID.INVENTORY.getId())
		{
			ItemContainer inv = e.getItemContainer();
			barsInInventory.clear();
			barsInInventory.put(BRONZE_BAR, inv.count(BRONZE_BAR)); barsInInventory.put(IRON_BAR, inv.count(IRON_BAR)); barsInInventory.put(STEEL_BAR, inv.count(STEEL_BAR)); barsInInventory.put(MITHRIL_BAR, inv.count(MITHRIL_BAR)); barsInInventory.put(ADAMANTITE_BAR, inv.count(ADAMANTITE_BAR)); barsInInventory.put(RUNITE_BAR, inv.count(RUNITE_BAR));
			updateCachedTotalsFromInventory();
		}
	}

	public int getRemainingBars(int barId)
	{
		// FIXED: remaining = bank + inv (current bars you own) - do NOT subtract used, because bank+inv already decreased when you smith
		// Old logic: bank + inv - used caused total qty to drop by 1 each smith (double subtract)
		int bank = barsInBank.getOrDefault(barId, 0);
		int inv = barsInInventory.getOrDefault(barId, 0);
		return Math.max(0, bank + inv);
	}

	public int getTotalBarsAvailable(int barId)
	{
		return barsInBank.getOrDefault(barId, 0) + barsInInventory.getOrDefault(barId, 0);
	}

	@Subscribe public void onGameTick(GameTick e) { if (client.getLocalPlayer() == null) { wasMovingLastTick = false; return; } WorldPoint cur = client.getLocalPlayer().getWorldLocation(); wasMovingLastTick = lastLocation!= null &&!lastLocation.equals(cur); lastLocation = cur; }
	@Subscribe public void onAnimationChanged(AnimationChanged e) { if (e.getActor()!= client.getLocalPlayer()) return; if (client.getLocalPlayer().getAnimation() == SMITHING_ANIMATION) lastSmithingTime = Instant.now(); }

	private void updateCachedTotalsFromInventory()
	{
		ItemContainer bank = client.getItemContainer(InventoryID.BANK); ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
		for (Integer id : itemsSmithed.keySet()) { int invCount = inv!= null? inv.count(id) : 0; if (bank!= null) cachedItemTotals.put(id, bank.count(id) + invCount); else { int old = cachedItemTotals.getOrDefault(id, 0); int estBank = Math.max(0, old - (id == trackedItemId? invCount : 0)); cachedItemTotals.put(id, estBank + invCount); } }
	}

	private void scanForAllSmithableItems()
	{
		ItemContainer bank = client.getItemContainer(InventoryID.BANK); ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY); if (bank == null && inv == null) return;
		Set<Integer> found = new HashSet<>(); String[] pre = {"bronze", "iron", "steel", "mithril", "adamant", "rune"};
		for (int i = 0; i < 40000; i++) { try { String n = itemManager.getItemComposition(i).getName(); if (n == null) continue; String l = n.toLowerCase(); boolean ok = false; for (String p : pre) if (l.startsWith(p + " ")) { ok = true; break; } if (!ok) continue; if (isSmithableType(l)) { int tot = (bank!= null? bank.count(i) : 0) + (inv!= null? inv.count(i) : 0); if (tot > 0) { found.add(i); cachedItemTotals.put(i, tot); } } } catch (Exception ignored) {} }
		itemsSmithed.clear(); for (Integer id : found) itemsSmithed.put(id, 1);
	}

	private boolean isSmithableType(String n) { return n.contains("2h sword") || n.contains("platebody") || n.contains("platelegs") || n.contains("plateskirt") || n.contains("full helm") || n.contains("med helm") || n.contains("chainbody") || n.contains("sq shield") || n.contains("kiteshield") || n.contains("scimitar") || n.contains("longsword") || n.contains("sword") || n.contains("dagger") || n.contains("axe") || n.contains("mace") || n.contains("warhammer") || n.contains("battleaxe") || n.contains("claws") || n.contains("spear") || n.contains("halberd") || n.contains("pickaxe") || n.contains("hasta") || n.contains("javelin heads") || n.contains("dart tips") || n.contains("arrowtips") || n.contains("knives") || n.contains("bolts") || n.contains("limbs") || n.contains("nails"); }
	private String mapItemName(String raw) { String l = raw.toLowerCase().trim(); String p = getBarPrefix(); if (l.equals("two-handed sword") || l.equals("2h sword")) return p + " 2h sword"; return p + " " + raw; }
	private String getBarPrefix() { switch (config.barType()) { case BRONZE: return "bronze"; case IRON: return "iron"; case STEEL: return "steel"; case MITHRIL: return "mithril"; case ADAMANTITE: return "adamant"; case RUNITE: return "rune"; default: return "rune"; } }

	@Subscribe public void onStatChanged(StatChanged event)
	{
		if (event.getSkill()!= Skill.SMITHING) return; int currentXp = event.getXp(); if (lastSmithingXp == -1) { lastSmithingXp = currentXp; return; } int delta = currentXp - lastSmithingXp; if (delta <= 0) { lastSmithingXp = currentXp; return; }
		if (!hasStartedSmithing && delta > MAX_REAL_SMITH_XP) { lastSmithingXp = currentXp; return; }
		if (!hasStartedSmithing && loginTime!= null && Duration.between(loginTime, Instant.now()).getSeconds() < 5) { lastSmithingXp = currentXp; return; }
		if (!hasStartedSmithing) { hasStartedSmithing = true; session.reset(); barsUsed.clear(); }
		lastSmithingTime = Instant.now(); session.incrementItems(); if (pendingItemName!= null) resolvePendingItem();
		if (trackedItemId!= -1) { itemsSmithed.put(trackedItemId, 1); cachedItemTotals.put(trackedItemId, cachedItemTotals.getOrDefault(trackedItemId, 0) + 1); }
		int barId = config.barType().getItemId(); barsUsed.put(barId, barsUsed.getOrDefault(barId, 0) + getBarsPerItem(trackedItemName)); lastSmithingXp = currentXp;
	}

	public int getBarsPerItem(String name) { String l = name.toLowerCase(); if (l.contains("platebody")) return 5; if (l.contains("2h sword") || l.contains("platelegs") || l.contains("plateskirt")) return 3; if (l.contains("full helm") || l.contains("chainbody") || l.contains("kiteshield") || l.contains("battleaxe") || l.contains("scimitar") || l.contains("longsword") || l.contains("claws")) return 2; return 1; }
	public int getBarIdForItem(String itemName) { String l = itemName.toLowerCase(); if (l.startsWith("bronze ")) return BRONZE_BAR; if (l.startsWith("iron ")) return IRON_BAR; if (l.startsWith("steel ")) return STEEL_BAR; if (l.startsWith("mithril ")) return MITHRIL_BAR; if (l.startsWith("adamant ")) return ADAMANTITE_BAR; if (l.startsWith("rune ")) return RUNITE_BAR; return config.barType().getItemId(); }

	public int getItemsSmithed() { return hasStartedSmithing? session.getItemsSmithed() : 0; }
	public double getItemsPerHour() { return hasStartedSmithing? session.getPerHour() : 0; }
	public double getGpPerHour() { return!hasStartedSmithing || trackedItemId == -1? 0 : getItemsPerHour() * getLivePrice(trackedItemId); }
	public int getBarCostPerHour() { return!hasStartedSmithing || trackedItemId == -1? 0 : (int)(getItemsPerHour() * getBarsPerItem(trackedItemName) * getEffectiveBarPrice(config.barType().getItemId())); }
	public int getProfitPerHour() { return (int)getGpPerHour() - getBarCostPerHour(); }
	public String getTrackedItemName() { return trackedItemName; }
	public int getTrackedItemId() { return trackedItemId; }
	public int getItemCountTotal(int id) { return cachedItemTotals.getOrDefault(id, 0); }
	public Map<Integer, Integer> getItemsSmithedMap() { return itemsSmithed; }
	public Duration getTimeRunning() { return hasStartedSmithing? session.getRuntime() : Duration.ZERO; }
	public boolean hasStartedSmithing() { return hasStartedSmithing; }

	public String getStatus()
	{
		if (client.getLocalPlayer() == null) return "Idle";
		Widget bankWidget = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankWidget!= null &&!bankWidget.isHidden()) return "Banking";
		if (client.getLocalPlayer().getAnimation() == SMITHING_ANIMATION) return "Smithing";
		if (lastSmithingTime != null && Duration.between(lastSmithingTime, Instant.now()).toMillis() < 2500) return "Smithing";
		if (wasMovingLastTick) return "Running";
		return "Idle";
	}

	public Map<Integer, Integer> getBarsUsed() { return barsUsed; }
	public Map<Integer, Integer> getBarsInBank() { return barsInBank; }
	public int getCurrentBarId() { return config.barType().getItemId(); }
	public ItemManager getItemManager() { return itemManager; }
	public Instant getLastPriceFetch() { return lastPriceFetch; }
	public String getItemKeyFromName(String lower)
	{
		String l = lower.toLowerCase();
		if (l.contains("2h sword") || l.contains("two-handed sword") || l.contains("2-handed sword")) return "TwoHandedSword";
		if (l.contains("platebody")) return "Platebody";
		if (l.contains("platelegs")) return "Platelegs";
		if (l.contains("plateskirt")) return "Plateskirt";
		if (l.contains("kiteshield")) return "Kiteshield";
		if (l.contains("chainbody")) return "Chainbody";
		if (l.contains("battleaxe")) return "Battleaxe";
		if (l.contains("warhammer")) return "Warhammer";
		if (l.contains("sq shield") || l.contains("square shield")) return "SqShield";
		if (l.contains("full helm") || l.contains("full helmet")) return "FullHelm";
		if (l.contains("med helm") || l.contains("medium helm") || l.contains("med helmet") || l.contains("medium helmet")) return "MedHelm";
		if (l.contains("claws")) return "Claws";
		if (l.contains("longsword")) return "Longsword";
		if (l.contains("scimitar")) return "Scimitar";
		if (l.contains("crossbow limbs") || (l.contains("limbs") && !l.contains("cl")) ) return "Limbs";
		if (l.contains("javelin") && l.contains("tips")) return "JavelinTips";
		if (l.contains("dart") && l.contains("tip")) return "DartTips";
		if (l.contains("arrowtip")) return "ArrowTips";
		if (l.contains("bolts") || l.contains("bolt")) return "Bolts";
		if (l.contains("knives") || l.contains("knife")) return "Knives";
		if (l.contains("nails") || l.contains("nail")) return "Nails";
		if (l.contains("hasta")) return "Hasta";
		if (l.contains("spear") && !l.contains("spearhead")) return "Spear";
		if (l.contains("dagger")) return "Dagger";
		if (l.contains("axe") && !l.contains("battleaxe") && !l.contains("pickaxe")) return "Axe";
		if (l.contains("mace")) return "Mace";
		if (l.contains("sword") && !l.contains("longsword") && !l.contains("2h") && !l.contains("two-handed")) return "Sword";
		if (l.contains("bolts")) return "Bolts";
		return "Dagger";
	}
	public boolean shouldShowItem(int itemId)
	{
		try {
			String name = itemManager.getItemComposition(itemId).getName();
			if (name == null) return true;
			String lower = name.toLowerCase();
			String[] parts = lower.split(" ", 2);
			if (parts.length < 2) return true;
			String prefix = parts[0];
			String key = getItemKeyFromName(lower);
			switch (prefix) {
				case "bronze":
					switch (key) {
						case "Dagger": return config.showBronzeDagger();
						case "Axe": return config.showBronzeAxe();
						case "Mace": return config.showBronzeMace();
						case "MedHelm": return config.showBronzeMedHelm();
						case "Bolts": return config.showBronzeBolts();
						case "Sword": return config.showBronzeSword();
						case "Nails": return config.showBronzeNails();
						case "DartTips": return config.showBronzeDartTips();
						case "Scimitar": return config.showBronzeScimitar();
						case "Spear": return config.showBronzeSpear();
						case "Hasta": return config.showBronzeHasta();
						case "ArrowTips": return config.showBronzeArrowTips();
						case "Limbs": return config.showBronzeLimbs();
						case "Longsword": return config.showBronzeLongsword();
						case "JavelinTips": return config.showBronzeJavelinTips();
						case "FullHelm": return config.showBronzeFullHelm();
						case "Knives": return config.showBronzeKnives();
						case "SqShield": return config.showBronzeSqShield();
						case "Warhammer": return config.showBronzeWarhammer();
						case "Battleaxe": return config.showBronzeBattleaxe();
						case "Chainbody": return config.showBronzeChainbody();
						case "Kiteshield": return config.showBronzeKiteshield();
						case "Claws": return config.showBronzeClaws();
						case "TwoHandedSword": return config.showBronzeTwoHandedSword();
						case "Platelegs": return config.showBronzePlatelegs();
						case "Plateskirt": return config.showBronzePlateskirt();
						case "Platebody": return config.showBronzePlatebody();
						default: return true;
					}
				case "iron":
					switch (key) {
						case "Dagger": return config.showIronDagger();
						case "Axe": return config.showIronAxe();
						case "Mace": return config.showIronMace();
						case "MedHelm": return config.showIronMedHelm();
						case "Bolts": return config.showIronBolts();
						case "Sword": return config.showIronSword();
						case "Nails": return config.showIronNails();
						case "DartTips": return config.showIronDartTips();
						case "Scimitar": return config.showIronScimitar();
						case "Spear": return config.showIronSpear();
						case "Hasta": return config.showIronHasta();
						case "ArrowTips": return config.showIronArrowTips();
						case "Limbs": return config.showIronLimbs();
						case "Longsword": return config.showIronLongsword();
						case "JavelinTips": return config.showIronJavelinTips();
						case "FullHelm": return config.showIronFullHelm();
						case "Knives": return config.showIronKnives();
						case "SqShield": return config.showIronSqShield();
						case "Warhammer": return config.showIronWarhammer();
						case "Battleaxe": return config.showIronBattleaxe();
						case "Chainbody": return config.showIronChainbody();
						case "Kiteshield": return config.showIronKiteshield();
						case "Claws": return config.showIronClaws();
						case "TwoHandedSword": return config.showIronTwoHandedSword();
						case "Platelegs": return config.showIronPlatelegs();
						case "Plateskirt": return config.showIronPlateskirt();
						case "Platebody": return config.showIronPlatebody();
						default: return true;
					}
				case "steel":
					switch (key) {
						case "Dagger": return config.showSteelDagger();
						case "Axe": return config.showSteelAxe();
						case "Mace": return config.showSteelMace();
						case "MedHelm": return config.showSteelMedHelm();
						case "Bolts": return config.showSteelBolts();
						case "Sword": return config.showSteelSword();
						case "Nails": return config.showSteelNails();
						case "DartTips": return config.showSteelDartTips();
						case "Scimitar": return config.showSteelScimitar();
						case "Spear": return config.showSteelSpear();
						case "Hasta": return config.showSteelHasta();
						case "ArrowTips": return config.showSteelArrowTips();
						case "Limbs": return config.showSteelLimbs();
						case "Longsword": return config.showSteelLongsword();
						case "JavelinTips": return config.showSteelJavelinTips();
						case "FullHelm": return config.showSteelFullHelm();
						case "Knives": return config.showSteelKnives();
						case "SqShield": return config.showSteelSqShield();
						case "Warhammer": return config.showSteelWarhammer();
						case "Battleaxe": return config.showSteelBattleaxe();
						case "Chainbody": return config.showSteelChainbody();
						case "Kiteshield": return config.showSteelKiteshield();
						case "Claws": return config.showSteelClaws();
						case "TwoHandedSword": return config.showSteelTwoHandedSword();
						case "Platelegs": return config.showSteelPlatelegs();
						case "Plateskirt": return config.showSteelPlateskirt();
						case "Platebody": return config.showSteelPlatebody();
						default: return true;
					}
				case "mithril":
					switch (key) {
						case "Dagger": return config.showMithrilDagger();
						case "Axe": return config.showMithrilAxe();
						case "Mace": return config.showMithrilMace();
						case "MedHelm": return config.showMithrilMedHelm();
						case "Bolts": return config.showMithrilBolts();
						case "Sword": return config.showMithrilSword();
						case "Nails": return config.showMithrilNails();
						case "DartTips": return config.showMithrilDartTips();
						case "Scimitar": return config.showMithrilScimitar();
						case "Spear": return config.showMithrilSpear();
						case "Hasta": return config.showMithrilHasta();
						case "ArrowTips": return config.showMithrilArrowTips();
						case "Limbs": return config.showMithrilLimbs();
						case "Longsword": return config.showMithrilLongsword();
						case "JavelinTips": return config.showMithrilJavelinTips();
						case "FullHelm": return config.showMithrilFullHelm();
						case "Knives": return config.showMithrilKnives();
						case "SqShield": return config.showMithrilSqShield();
						case "Warhammer": return config.showMithrilWarhammer();
						case "Battleaxe": return config.showMithrilBattleaxe();
						case "Chainbody": return config.showMithrilChainbody();
						case "Kiteshield": return config.showMithrilKiteshield();
						case "Claws": return config.showMithrilClaws();
						case "TwoHandedSword": return config.showMithrilTwoHandedSword();
						case "Platelegs": return config.showMithrilPlatelegs();
						case "Plateskirt": return config.showMithrilPlateskirt();
						case "Platebody": return config.showMithrilPlatebody();
						default: return true;
					}
				case "adamant":
					switch (key) {
						case "Dagger": return config.showAdamantDagger();
						case "Axe": return config.showAdamantAxe();
						case "Mace": return config.showAdamantMace();
						case "MedHelm": return config.showAdamantMedHelm();
						case "Bolts": return config.showAdamantBolts();
						case "Sword": return config.showAdamantSword();
						case "Nails": return config.showAdamantNails();
						case "DartTips": return config.showAdamantDartTips();
						case "Scimitar": return config.showAdamantScimitar();
						case "Spear": return config.showAdamantSpear();
						case "Hasta": return config.showAdamantHasta();
						case "ArrowTips": return config.showAdamantArrowTips();
						case "Limbs": return config.showAdamantLimbs();
						case "Longsword": return config.showAdamantLongsword();
						case "JavelinTips": return config.showAdamantJavelinTips();
						case "FullHelm": return config.showAdamantFullHelm();
						case "Knives": return config.showAdamantKnives();
						case "SqShield": return config.showAdamantSqShield();
						case "Warhammer": return config.showAdamantWarhammer();
						case "Battleaxe": return config.showAdamantBattleaxe();
						case "Chainbody": return config.showAdamantChainbody();
						case "Kiteshield": return config.showAdamantKiteshield();
						case "Claws": return config.showAdamantClaws();
						case "TwoHandedSword": return config.showAdamantTwoHandedSword();
						case "Platelegs": return config.showAdamantPlatelegs();
						case "Plateskirt": return config.showAdamantPlateskirt();
						case "Platebody": return config.showAdamantPlatebody();
						default: return true;
					}
				case "rune":
					switch (key) {
						case "Dagger": return config.showRuneDagger();
						case "Axe": return config.showRuneAxe();
						case "Mace": return config.showRuneMace();
						case "MedHelm": return config.showRuneMedHelm();
						case "Bolts": return config.showRuneBolts();
						case "Sword": return config.showRuneSword();
						case "Nails": return config.showRuneNails();
						case "DartTips": return config.showRuneDartTips();
						case "Scimitar": return config.showRuneScimitar();
						case "Spear": return config.showRuneSpear();
						case "Hasta": return config.showRuneHasta();
						case "ArrowTips": return config.showRuneArrowTips();
						case "Limbs": return config.showRuneLimbs();
						case "Longsword": return config.showRuneLongsword();
						case "JavelinTips": return config.showRuneJavelinTips();
						case "FullHelm": return config.showRuneFullHelm();
						case "Knives": return config.showRuneKnives();
						case "SqShield": return config.showRuneSqShield();
						case "Warhammer": return config.showRuneWarhammer();
						case "Battleaxe": return config.showRuneBattleaxe();
						case "Chainbody": return config.showRuneChainbody();
						case "Kiteshield": return config.showRuneKiteshield();
						case "Claws": return config.showRuneClaws();
						case "TwoHandedSword": return config.showRuneTwoHandedSword();
						case "Platelegs": return config.showRunePlatelegs();
						case "Plateskirt": return config.showRunePlateskirt();
						case "Platebody": return config.showRunePlatebody();
						default: return true;
					}
				default: return true;
			}
		} catch (Exception ignored) {}
		return true;
	}
}
