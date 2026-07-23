package com.smithingtracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("smithingtracker")
public interface SmithingTrackerConfig extends Config
{
	@ConfigSection(name="General", description="General settings", position=0, closedByDefault=false)
	String generalSection = "general";

	@ConfigItem(keyName="barType", name="Bar Type", description="Select bar type to track", position=0, section=generalSection)
	default BarType barType() { return BarType.RUNITE; }

	@ConfigItem(keyName="customBarCost", name="Bar Buy Price", description="What you paid per bar. 0 = live GE.", position=1, section=generalSection)
	default int customBarCost() { return 0; }

	@ConfigItem(keyName="showOverlay", name="Show Overlay", description="Show the main smithing overlay", position=2, section=generalSection)
	default boolean showOverlay() { return true; }

	@ConfigItem(keyName="showItemBox", name="Show Item Box", description="Show the item box overlay", position=3, section=generalSection)
	default boolean showItemBox() { return true; }

	@ConfigSection(name="Bronze", description="Bronze - Use ALL to bulk select/deselect all bronze items", position=10, closedByDefault=true)
	String bronzeSection = "bronze";

	@ConfigSection(name="Iron", description="Iron - Use ALL to bulk select/deselect all iron items", position=11, closedByDefault=true)
	String ironSection = "iron";

	@ConfigSection(name="Steel", description="Steel - Use ALL to bulk select/deselect all steel items", position=12, closedByDefault=true)
	String steelSection = "steel";

	@ConfigSection(name="Mithril", description="Mithril - Use ALL to bulk select/deselect all mithril items", position=13, closedByDefault=true)
	String mithrilSection = "mithril";

	@ConfigSection(name="Adamant", description="Adamant - Use ALL to bulk select/deselect all adamant items", position=14, closedByDefault=true)
	String adamantSection = "adamant";

	@ConfigSection(name="Rune", description="Rune - Use ALL to bulk select/deselect all rune items", position=15, closedByDefault=true)
	String runeSection = "rune";

	@ConfigSection(name="Ammo", description="Ammo mould products - cannonballs etc", position=16, closedByDefault=true)
	String ammoSection = "ammo";

	@ConfigItem(keyName="showBronzeAll", name="ALL - Select/Deselect All", description="Check/uncheck all bronze items", section=bronzeSection, position=0)
	default boolean showBronzeAll() { return true; }

	@ConfigItem(keyName="showBronzeDagger", name="Dagger", description="Show Bronze dagger", section=bronzeSection, position=1)
	default boolean showBronzeDagger() { return true; }

	@ConfigItem(keyName="showBronzeAxe", name="Axe", description="Show Bronze axe", section=bronzeSection, position=2)
	default boolean showBronzeAxe() { return true; }

	@ConfigItem(keyName="showBronzeMace", name="Mace", description="Show Bronze mace", section=bronzeSection, position=3)
	default boolean showBronzeMace() { return true; }

	@ConfigItem(keyName="showBronzeMedHelm", name="Med helm", description="Show Bronze med helm", section=bronzeSection, position=4)
	default boolean showBronzeMedHelm() { return true; }

	@ConfigItem(keyName="showBronzeBolts", name="Bolts (unf)", description="Show Bronze bolts (unf)", section=bronzeSection, position=5)
	default boolean showBronzeBolts() { return true; }

	@ConfigItem(keyName="showBronzeSword", name="Sword", description="Show Bronze sword", section=bronzeSection, position=6)
	default boolean showBronzeSword() { return true; }

	@ConfigItem(keyName="showBronzeNails", name="Nails", description="Show Bronze nails", section=bronzeSection, position=7)
	default boolean showBronzeNails() { return true; }

	@ConfigItem(keyName="showBronzeDartTips", name="Dart tips", description="Show Bronze dart tips", section=bronzeSection, position=8)
	default boolean showBronzeDartTips() { return true; }

	@ConfigItem(keyName="showBronzeScimitar", name="Scimitar", description="Show Bronze scimitar", section=bronzeSection, position=9)
	default boolean showBronzeScimitar() { return true; }

	@ConfigItem(keyName="showBronzeSpear", name="Spear", description="Show Bronze spear", section=bronzeSection, position=10)
	default boolean showBronzeSpear() { return true; }

	@ConfigItem(keyName="showBronzeHasta", name="Hasta", description="Show Bronze hasta", section=bronzeSection, position=11)
	default boolean showBronzeHasta() { return true; }

	@ConfigItem(keyName="showBronzeArrowTips", name="Arrowtips", description="Show Bronze arrowtips", section=bronzeSection, position=12)
	default boolean showBronzeArrowTips() { return true; }

	@ConfigItem(keyName="showBronzeLimbs", name="Limbs", description="Show Bronze limbs", section=bronzeSection, position=13)
	default boolean showBronzeLimbs() { return true; }

	@ConfigItem(keyName="showBronzeLongsword", name="Longsword", description="Show Bronze longsword", section=bronzeSection, position=14)
	default boolean showBronzeLongsword() { return true; }

	@ConfigItem(keyName="showBronzeJavelinTips", name="Javelin tips", description="Show Bronze javelin tips", section=bronzeSection, position=15)
	default boolean showBronzeJavelinTips() { return true; }

	@ConfigItem(keyName="showBronzeFullHelm", name="Full helm", description="Show Bronze full helm", section=bronzeSection, position=16)
	default boolean showBronzeFullHelm() { return true; }

	@ConfigItem(keyName="showBronzeKnives", name="Knives", description="Show Bronze knives", section=bronzeSection, position=17)
	default boolean showBronzeKnives() { return true; }

	@ConfigItem(keyName="showBronzeSqShield", name="Sq shield", description="Show Bronze sq shield", section=bronzeSection, position=18)
	default boolean showBronzeSqShield() { return true; }

	@ConfigItem(keyName="showBronzeWarhammer", name="Warhammer", description="Show Bronze warhammer", section=bronzeSection, position=19)
	default boolean showBronzeWarhammer() { return true; }

	@ConfigItem(keyName="showBronzeBattleaxe", name="Battleaxe", description="Show Bronze battleaxe", section=bronzeSection, position=20)
	default boolean showBronzeBattleaxe() { return true; }

	@ConfigItem(keyName="showBronzeChainbody", name="Chainbody", description="Show Bronze chainbody", section=bronzeSection, position=21)
	default boolean showBronzeChainbody() { return true; }

	@ConfigItem(keyName="showBronzeKiteshield", name="Kiteshield", description="Show Bronze kiteshield", section=bronzeSection, position=22)
	default boolean showBronzeKiteshield() { return true; }

	@ConfigItem(keyName="showBronzeClaws", name="Claws", description="Show Bronze claws", section=bronzeSection, position=23)
	default boolean showBronzeClaws() { return true; }

	@ConfigItem(keyName="showBronzeTwoHandedSword", name="2h sword", description="Show Bronze 2h sword", section=bronzeSection, position=24)
	default boolean showBronzeTwoHandedSword() { return true; }

	@ConfigItem(keyName="showBronzePlatelegs", name="Platelegs", description="Show Bronze platelegs", section=bronzeSection, position=25)
	default boolean showBronzePlatelegs() { return true; }

	@ConfigItem(keyName="showBronzePlateskirt", name="Plateskirt", description="Show Bronze plateskirt", section=bronzeSection, position=26)
	default boolean showBronzePlateskirt() { return true; }

	@ConfigItem(keyName="showBronzePlatebody", name="Platebody", description="Show Bronze platebody", section=bronzeSection, position=27)
	default boolean showBronzePlatebody() { return true; }


	@ConfigItem(keyName="showIronAll", name="ALL - Select/Deselect All", description="Check/uncheck all iron items", section=ironSection, position=0)
	default boolean showIronAll() { return true; }

	@ConfigItem(keyName="showIronDagger", name="Dagger", description="Show Iron dagger", section=ironSection, position=1)
	default boolean showIronDagger() { return true; }

	@ConfigItem(keyName="showIronAxe", name="Axe", description="Show Iron axe", section=ironSection, position=2)
	default boolean showIronAxe() { return true; }

	@ConfigItem(keyName="showIronMace", name="Mace", description="Show Iron mace", section=ironSection, position=3)
	default boolean showIronMace() { return true; }

	@ConfigItem(keyName="showIronMedHelm", name="Med helm", description="Show Iron med helm", section=ironSection, position=4)
	default boolean showIronMedHelm() { return true; }

	@ConfigItem(keyName="showIronBolts", name="Bolts (unf)", description="Show Iron bolts (unf)", section=ironSection, position=5)
	default boolean showIronBolts() { return true; }

	@ConfigItem(keyName="showIronSword", name="Sword", description="Show Iron sword", section=ironSection, position=6)
	default boolean showIronSword() { return true; }

	@ConfigItem(keyName="showIronNails", name="Nails", description="Show Iron nails", section=ironSection, position=7)
	default boolean showIronNails() { return true; }

	@ConfigItem(keyName="showIronDartTips", name="Dart tips", description="Show Iron dart tips", section=ironSection, position=8)
	default boolean showIronDartTips() { return true; }

	@ConfigItem(keyName="showIronScimitar", name="Scimitar", description="Show Iron scimitar", section=ironSection, position=9)
	default boolean showIronScimitar() { return true; }

	@ConfigItem(keyName="showIronSpear", name="Spear", description="Show Iron spear", section=ironSection, position=10)
	default boolean showIronSpear() { return true; }

	@ConfigItem(keyName="showIronHasta", name="Hasta", description="Show Iron hasta", section=ironSection, position=11)
	default boolean showIronHasta() { return true; }

	@ConfigItem(keyName="showIronArrowTips", name="Arrowtips", description="Show Iron arrowtips", section=ironSection, position=12)
	default boolean showIronArrowTips() { return true; }

	@ConfigItem(keyName="showIronLimbs", name="Limbs", description="Show Iron limbs", section=ironSection, position=13)
	default boolean showIronLimbs() { return true; }

	@ConfigItem(keyName="showIronLongsword", name="Longsword", description="Show Iron longsword", section=ironSection, position=14)
	default boolean showIronLongsword() { return true; }

	@ConfigItem(keyName="showIronJavelinTips", name="Javelin tips", description="Show Iron javelin tips", section=ironSection, position=15)
	default boolean showIronJavelinTips() { return true; }

	@ConfigItem(keyName="showIronFullHelm", name="Full helm", description="Show Iron full helm", section=ironSection, position=16)
	default boolean showIronFullHelm() { return true; }

	@ConfigItem(keyName="showIronKnives", name="Knives", description="Show Iron knives", section=ironSection, position=17)
	default boolean showIronKnives() { return true; }

	@ConfigItem(keyName="showIronSqShield", name="Sq shield", description="Show Iron sq shield", section=ironSection, position=18)
	default boolean showIronSqShield() { return true; }

	@ConfigItem(keyName="showIronWarhammer", name="Warhammer", description="Show Iron warhammer", section=ironSection, position=19)
	default boolean showIronWarhammer() { return true; }

	@ConfigItem(keyName="showIronBattleaxe", name="Battleaxe", description="Show Iron battleaxe", section=ironSection, position=20)
	default boolean showIronBattleaxe() { return true; }

	@ConfigItem(keyName="showIronChainbody", name="Chainbody", description="Show Iron chainbody", section=ironSection, position=21)
	default boolean showIronChainbody() { return true; }

	@ConfigItem(keyName="showIronKiteshield", name="Kiteshield", description="Show Iron kiteshield", section=ironSection, position=22)
	default boolean showIronKiteshield() { return true; }

	@ConfigItem(keyName="showIronClaws", name="Claws", description="Show Iron claws", section=ironSection, position=23)
	default boolean showIronClaws() { return true; }

	@ConfigItem(keyName="showIronTwoHandedSword", name="2h sword", description="Show Iron 2h sword", section=ironSection, position=24)
	default boolean showIronTwoHandedSword() { return true; }

	@ConfigItem(keyName="showIronPlatelegs", name="Platelegs", description="Show Iron platelegs", section=ironSection, position=25)
	default boolean showIronPlatelegs() { return true; }

	@ConfigItem(keyName="showIronPlateskirt", name="Plateskirt", description="Show Iron plateskirt", section=ironSection, position=26)
	default boolean showIronPlateskirt() { return true; }

	@ConfigItem(keyName="showIronPlatebody", name="Platebody", description="Show Iron platebody", section=ironSection, position=27)
	default boolean showIronPlatebody() { return true; }


	@ConfigItem(keyName="showSteelAll", name="ALL - Select/Deselect All", description="Check/uncheck all steel items", section=steelSection, position=0)
	default boolean showSteelAll() { return true; }

	@ConfigItem(keyName="showSteelDagger", name="Dagger", description="Show Steel dagger", section=steelSection, position=1)
	default boolean showSteelDagger() { return true; }

	@ConfigItem(keyName="showSteelAxe", name="Axe", description="Show Steel axe", section=steelSection, position=2)
	default boolean showSteelAxe() { return true; }

	@ConfigItem(keyName="showSteelMace", name="Mace", description="Show Steel mace", section=steelSection, position=3)
	default boolean showSteelMace() { return true; }

	@ConfigItem(keyName="showSteelMedHelm", name="Med helm", description="Show Steel med helm", section=steelSection, position=4)
	default boolean showSteelMedHelm() { return true; }

	@ConfigItem(keyName="showSteelBolts", name="Bolts (unf)", description="Show Steel bolts (unf)", section=steelSection, position=5)
	default boolean showSteelBolts() { return true; }

	@ConfigItem(keyName="showSteelSword", name="Sword", description="Show Steel sword", section=steelSection, position=6)
	default boolean showSteelSword() { return true; }

	@ConfigItem(keyName="showSteelNails", name="Nails", description="Show Steel nails", section=steelSection, position=7)
	default boolean showSteelNails() { return true; }

	@ConfigItem(keyName="showSteelDartTips", name="Dart tips", description="Show Steel dart tips", section=steelSection, position=8)
	default boolean showSteelDartTips() { return true; }

	@ConfigItem(keyName="showSteelScimitar", name="Scimitar", description="Show Steel scimitar", section=steelSection, position=9)
	default boolean showSteelScimitar() { return true; }

	@ConfigItem(keyName="showSteelSpear", name="Spear", description="Show Steel spear", section=steelSection, position=10)
	default boolean showSteelSpear() { return true; }

	@ConfigItem(keyName="showSteelHasta", name="Hasta", description="Show Steel hasta", section=steelSection, position=11)
	default boolean showSteelHasta() { return true; }

	@ConfigItem(keyName="showSteelArrowTips", name="Arrowtips", description="Show Steel arrowtips", section=steelSection, position=12)
	default boolean showSteelArrowTips() { return true; }

	@ConfigItem(keyName="showSteelLimbs", name="Limbs", description="Show Steel limbs", section=steelSection, position=13)
	default boolean showSteelLimbs() { return true; }

	@ConfigItem(keyName="showSteelLongsword", name="Longsword", description="Show Steel longsword", section=steelSection, position=14)
	default boolean showSteelLongsword() { return true; }

	@ConfigItem(keyName="showSteelJavelinTips", name="Javelin tips", description="Show Steel javelin tips", section=steelSection, position=15)
	default boolean showSteelJavelinTips() { return true; }

	@ConfigItem(keyName="showSteelFullHelm", name="Full helm", description="Show Steel full helm", section=steelSection, position=16)
	default boolean showSteelFullHelm() { return true; }

	@ConfigItem(keyName="showSteelKnives", name="Knives", description="Show Steel knives", section=steelSection, position=17)
	default boolean showSteelKnives() { return true; }

	@ConfigItem(keyName="showSteelSqShield", name="Sq shield", description="Show Steel sq shield", section=steelSection, position=18)
	default boolean showSteelSqShield() { return true; }

	@ConfigItem(keyName="showSteelWarhammer", name="Warhammer", description="Show Steel warhammer", section=steelSection, position=19)
	default boolean showSteelWarhammer() { return true; }

	@ConfigItem(keyName="showSteelBattleaxe", name="Battleaxe", description="Show Steel battleaxe", section=steelSection, position=20)
	default boolean showSteelBattleaxe() { return true; }

	@ConfigItem(keyName="showSteelChainbody", name="Chainbody", description="Show Steel chainbody", section=steelSection, position=21)
	default boolean showSteelChainbody() { return true; }

	@ConfigItem(keyName="showSteelKiteshield", name="Kiteshield", description="Show Steel kiteshield", section=steelSection, position=22)
	default boolean showSteelKiteshield() { return true; }

	@ConfigItem(keyName="showSteelClaws", name="Claws", description="Show Steel claws", section=steelSection, position=23)
	default boolean showSteelClaws() { return true; }

	@ConfigItem(keyName="showSteelTwoHandedSword", name="2h sword", description="Show Steel 2h sword", section=steelSection, position=24)
	default boolean showSteelTwoHandedSword() { return true; }

	@ConfigItem(keyName="showSteelPlatelegs", name="Platelegs", description="Show Steel platelegs", section=steelSection, position=25)
	default boolean showSteelPlatelegs() { return true; }

	@ConfigItem(keyName="showSteelPlateskirt", name="Plateskirt", description="Show Steel plateskirt", section=steelSection, position=26)
	default boolean showSteelPlateskirt() { return true; }

	@ConfigItem(keyName="showSteelPlatebody", name="Platebody", description="Show Steel platebody", section=steelSection, position=27)
	default boolean showSteelPlatebody() { return true; }

	@ConfigItem(keyName="showSteelCannonball", name="Cannonball", description="Show Steel cannonballs (from ammo mould)", section=steelSection, position=28)
	default boolean showSteelCannonball() { return true; }


	@ConfigItem(keyName="showMithrilAll", name="ALL - Select/Deselect All", description="Check/uncheck all mithril items", section=mithrilSection, position=0)
	default boolean showMithrilAll() { return true; }

	@ConfigItem(keyName="showMithrilDagger", name="Dagger", description="Show Mithril dagger", section=mithrilSection, position=1)
	default boolean showMithrilDagger() { return true; }

	@ConfigItem(keyName="showMithrilAxe", name="Axe", description="Show Mithril axe", section=mithrilSection, position=2)
	default boolean showMithrilAxe() { return true; }

	@ConfigItem(keyName="showMithrilMace", name="Mace", description="Show Mithril mace", section=mithrilSection, position=3)
	default boolean showMithrilMace() { return true; }

	@ConfigItem(keyName="showMithrilMedHelm", name="Med helm", description="Show Mithril med helm", section=mithrilSection, position=4)
	default boolean showMithrilMedHelm() { return true; }

	@ConfigItem(keyName="showMithrilBolts", name="Bolts (unf)", description="Show Mithril bolts (unf)", section=mithrilSection, position=5)
	default boolean showMithrilBolts() { return true; }

	@ConfigItem(keyName="showMithrilSword", name="Sword", description="Show Mithril sword", section=mithrilSection, position=6)
	default boolean showMithrilSword() { return true; }

	@ConfigItem(keyName="showMithrilNails", name="Nails", description="Show Mithril nails", section=mithrilSection, position=7)
	default boolean showMithrilNails() { return true; }

	@ConfigItem(keyName="showMithrilDartTips", name="Dart tips", description="Show Mithril dart tips", section=mithrilSection, position=8)
	default boolean showMithrilDartTips() { return true; }

	@ConfigItem(keyName="showMithrilScimitar", name="Scimitar", description="Show Mithril scimitar", section=mithrilSection, position=9)
	default boolean showMithrilScimitar() { return true; }

	@ConfigItem(keyName="showMithrilSpear", name="Spear", description="Show Mithril spear", section=mithrilSection, position=10)
	default boolean showMithrilSpear() { return true; }

	@ConfigItem(keyName="showMithrilHasta", name="Hasta", description="Show Mithril hasta", section=mithrilSection, position=11)
	default boolean showMithrilHasta() { return true; }

	@ConfigItem(keyName="showMithrilArrowTips", name="Arrowtips", description="Show Mithril arrowtips", section=mithrilSection, position=12)
	default boolean showMithrilArrowTips() { return true; }

	@ConfigItem(keyName="showMithrilLimbs", name="Limbs", description="Show Mithril limbs", section=mithrilSection, position=13)
	default boolean showMithrilLimbs() { return true; }

	@ConfigItem(keyName="showMithrilLongsword", name="Longsword", description="Show Mithril longsword", section=mithrilSection, position=14)
	default boolean showMithrilLongsword() { return true; }

	@ConfigItem(keyName="showMithrilJavelinTips", name="Javelin tips", description="Show Mithril javelin tips", section=mithrilSection, position=15)
	default boolean showMithrilJavelinTips() { return true; }

	@ConfigItem(keyName="showMithrilFullHelm", name="Full helm", description="Show Mithril full helm", section=mithrilSection, position=16)
	default boolean showMithrilFullHelm() { return true; }

	@ConfigItem(keyName="showMithrilKnives", name="Knives", description="Show Mithril knives", section=mithrilSection, position=17)
	default boolean showMithrilKnives() { return true; }

	@ConfigItem(keyName="showMithrilSqShield", name="Sq shield", description="Show Mithril sq shield", section=mithrilSection, position=18)
	default boolean showMithrilSqShield() { return true; }

	@ConfigItem(keyName="showMithrilWarhammer", name="Warhammer", description="Show Mithril warhammer", section=mithrilSection, position=19)
	default boolean showMithrilWarhammer() { return true; }

	@ConfigItem(keyName="showMithrilBattleaxe", name="Battleaxe", description="Show Mithril battleaxe", section=mithrilSection, position=20)
	default boolean showMithrilBattleaxe() { return true; }

	@ConfigItem(keyName="showMithrilChainbody", name="Chainbody", description="Show Mithril chainbody", section=mithrilSection, position=21)
	default boolean showMithrilChainbody() { return true; }

	@ConfigItem(keyName="showMithrilKiteshield", name="Kiteshield", description="Show Mithril kiteshield", section=mithrilSection, position=22)
	default boolean showMithrilKiteshield() { return true; }

	@ConfigItem(keyName="showMithrilClaws", name="Claws", description="Show Mithril claws", section=mithrilSection, position=23)
	default boolean showMithrilClaws() { return true; }

	@ConfigItem(keyName="showMithrilTwoHandedSword", name="2h sword", description="Show Mithril 2h sword", section=mithrilSection, position=24)
	default boolean showMithrilTwoHandedSword() { return true; }

	@ConfigItem(keyName="showMithrilPlatelegs", name="Platelegs", description="Show Mithril platelegs", section=mithrilSection, position=25)
	default boolean showMithrilPlatelegs() { return true; }

	@ConfigItem(keyName="showMithrilPlateskirt", name="Plateskirt", description="Show Mithril plateskirt", section=mithrilSection, position=26)
	default boolean showMithrilPlateskirt() { return true; }

	@ConfigItem(keyName="showMithrilPlatebody", name="Platebody", description="Show Mithril platebody", section=mithrilSection, position=27)
	default boolean showMithrilPlatebody() { return true; }


	@ConfigItem(keyName="showAdamantAll", name="ALL - Select/Deselect All", description="Check/uncheck all adamant items", section=adamantSection, position=0)
	default boolean showAdamantAll() { return true; }

	@ConfigItem(keyName="showAdamantDagger", name="Dagger", description="Show Adamant dagger", section=adamantSection, position=1)
	default boolean showAdamantDagger() { return true; }

	@ConfigItem(keyName="showAdamantAxe", name="Axe", description="Show Adamant axe", section=adamantSection, position=2)
	default boolean showAdamantAxe() { return true; }

	@ConfigItem(keyName="showAdamantMace", name="Mace", description="Show Adamant mace", section=adamantSection, position=3)
	default boolean showAdamantMace() { return true; }

	@ConfigItem(keyName="showAdamantMedHelm", name="Med helm", description="Show Adamant med helm", section=adamantSection, position=4)
	default boolean showAdamantMedHelm() { return true; }

	@ConfigItem(keyName="showAdamantBolts", name="Bolts (unf)", description="Show Adamant bolts (unf)", section=adamantSection, position=5)
	default boolean showAdamantBolts() { return true; }

	@ConfigItem(keyName="showAdamantSword", name="Sword", description="Show Adamant sword", section=adamantSection, position=6)
	default boolean showAdamantSword() { return true; }

	@ConfigItem(keyName="showAdamantNails", name="Nails", description="Show Adamant nails", section=adamantSection, position=7)
	default boolean showAdamantNails() { return true; }

	@ConfigItem(keyName="showAdamantDartTips", name="Dart tips", description="Show Adamant dart tips", section=adamantSection, position=8)
	default boolean showAdamantDartTips() { return true; }

	@ConfigItem(keyName="showAdamantScimitar", name="Scimitar", description="Show Adamant scimitar", section=adamantSection, position=9)
	default boolean showAdamantScimitar() { return true; }

	@ConfigItem(keyName="showAdamantSpear", name="Spear", description="Show Adamant spear", section=adamantSection, position=10)
	default boolean showAdamantSpear() { return true; }

	@ConfigItem(keyName="showAdamantHasta", name="Hasta", description="Show Adamant hasta", section=adamantSection, position=11)
	default boolean showAdamantHasta() { return true; }

	@ConfigItem(keyName="showAdamantArrowTips", name="Arrowtips", description="Show Adamant arrowtips", section=adamantSection, position=12)
	default boolean showAdamantArrowTips() { return true; }

	@ConfigItem(keyName="showAdamantLimbs", name="Limbs", description="Show Adamant limbs", section=adamantSection, position=13)
	default boolean showAdamantLimbs() { return true; }

	@ConfigItem(keyName="showAdamantLongsword", name="Longsword", description="Show Adamant longsword", section=adamantSection, position=14)
	default boolean showAdamantLongsword() { return true; }

	@ConfigItem(keyName="showAdamantJavelinTips", name="Javelin tips", description="Show Adamant javelin tips", section=adamantSection, position=15)
	default boolean showAdamantJavelinTips() { return true; }

	@ConfigItem(keyName="showAdamantFullHelm", name="Full helm", description="Show Adamant full helm", section=adamantSection, position=16)
	default boolean showAdamantFullHelm() { return true; }

	@ConfigItem(keyName="showAdamantKnives", name="Knives", description="Show Adamant knives", section=adamantSection, position=17)
	default boolean showAdamantKnives() { return true; }

	@ConfigItem(keyName="showAdamantSqShield", name="Sq shield", description="Show Adamant sq shield", section=adamantSection, position=18)
	default boolean showAdamantSqShield() { return true; }

	@ConfigItem(keyName="showAdamantWarhammer", name="Warhammer", description="Show Adamant warhammer", section=adamantSection, position=19)
	default boolean showAdamantWarhammer() { return true; }

	@ConfigItem(keyName="showAdamantBattleaxe", name="Battleaxe", description="Show Adamant battleaxe", section=adamantSection, position=20)
	default boolean showAdamantBattleaxe() { return true; }

	@ConfigItem(keyName="showAdamantChainbody", name="Chainbody", description="Show Adamant chainbody", section=adamantSection, position=21)
	default boolean showAdamantChainbody() { return true; }

	@ConfigItem(keyName="showAdamantKiteshield", name="Kiteshield", description="Show Adamant kiteshield", section=adamantSection, position=22)
	default boolean showAdamantKiteshield() { return true; }

	@ConfigItem(keyName="showAdamantClaws", name="Claws", description="Show Adamant claws", section=adamantSection, position=23)
	default boolean showAdamantClaws() { return true; }

	@ConfigItem(keyName="showAdamantTwoHandedSword", name="2h sword", description="Show Adamant 2h sword", section=adamantSection, position=24)
	default boolean showAdamantTwoHandedSword() { return true; }

	@ConfigItem(keyName="showAdamantPlatelegs", name="Platelegs", description="Show Adamant platelegs", section=adamantSection, position=25)
	default boolean showAdamantPlatelegs() { return true; }

	@ConfigItem(keyName="showAdamantPlateskirt", name="Plateskirt", description="Show Adamant plateskirt", section=adamantSection, position=26)
	default boolean showAdamantPlateskirt() { return true; }

	@ConfigItem(keyName="showAdamantPlatebody", name="Platebody", description="Show Adamant platebody", section=adamantSection, position=27)
	default boolean showAdamantPlatebody() { return true; }


	@ConfigItem(keyName="showRuneAll", name="ALL - Select/Deselect All", description="Check/uncheck all rune items", section=runeSection, position=0)
	default boolean showRuneAll() { return true; }

	@ConfigItem(keyName="showRuneDagger", name="Dagger", description="Show Rune dagger", section=runeSection, position=1)
	default boolean showRuneDagger() { return true; }

	@ConfigItem(keyName="showRuneAxe", name="Axe", description="Show Rune axe", section=runeSection, position=2)
	default boolean showRuneAxe() { return true; }

	@ConfigItem(keyName="showRuneMace", name="Mace", description="Show Rune mace", section=runeSection, position=3)
	default boolean showRuneMace() { return true; }

	@ConfigItem(keyName="showRuneMedHelm", name="Med helm", description="Show Rune med helm", section=runeSection, position=4)
	default boolean showRuneMedHelm() { return true; }

	@ConfigItem(keyName="showRuneBolts", name="Bolts (unf)", description="Show Rune bolts (unf)", section=runeSection, position=5)
	default boolean showRuneBolts() { return true; }

	@ConfigItem(keyName="showRuneSword", name="Sword", description="Show Rune sword", section=runeSection, position=6)
	default boolean showRuneSword() { return true; }

	@ConfigItem(keyName="showRuneNails", name="Nails", description="Show Rune nails", section=runeSection, position=7)
	default boolean showRuneNails() { return true; }

	@ConfigItem(keyName="showRuneDartTips", name="Dart tips", description="Show Rune dart tips", section=runeSection, position=8)
	default boolean showRuneDartTips() { return true; }

	@ConfigItem(keyName="showRuneScimitar", name="Scimitar", description="Show Rune scimitar", section=runeSection, position=9)
	default boolean showRuneScimitar() { return true; }

	@ConfigItem(keyName="showRuneSpear", name="Spear", description="Show Rune spear", section=runeSection, position=10)
	default boolean showRuneSpear() { return true; }

	@ConfigItem(keyName="showRuneHasta", name="Hasta", description="Show Rune hasta", section=runeSection, position=11)
	default boolean showRuneHasta() { return true; }

	@ConfigItem(keyName="showRuneArrowTips", name="Arrowtips", description="Show Rune arrowtips", section=runeSection, position=12)
	default boolean showRuneArrowTips() { return true; }

	@ConfigItem(keyName="showRuneLimbs", name="Limbs", description="Show Rune limbs", section=runeSection, position=13)
	default boolean showRuneLimbs() { return true; }

	@ConfigItem(keyName="showRuneLongsword", name="Longsword", description="Show Rune longsword", section=runeSection, position=14)
	default boolean showRuneLongsword() { return true; }

	@ConfigItem(keyName="showRuneJavelinTips", name="Javelin tips", description="Show Rune javelin tips", section=runeSection, position=15)
	default boolean showRuneJavelinTips() { return true; }

	@ConfigItem(keyName="showRuneFullHelm", name="Full helm", description="Show Rune full helm", section=runeSection, position=16)
	default boolean showRuneFullHelm() { return true; }

	@ConfigItem(keyName="showRuneKnives", name="Knives", description="Show Rune knives", section=runeSection, position=17)
	default boolean showRuneKnives() { return true; }

	@ConfigItem(keyName="showRuneSqShield", name="Sq shield", description="Show Rune sq shield", section=runeSection, position=18)
	default boolean showRuneSqShield() { return true; }

	@ConfigItem(keyName="showRuneWarhammer", name="Warhammer", description="Show Rune warhammer", section=runeSection, position=19)
	default boolean showRuneWarhammer() { return true; }

	@ConfigItem(keyName="showRuneBattleaxe", name="Battleaxe", description="Show Rune battleaxe", section=runeSection, position=20)
	default boolean showRuneBattleaxe() { return true; }

	@ConfigItem(keyName="showRuneChainbody", name="Chainbody", description="Show Rune chainbody", section=runeSection, position=21)
	default boolean showRuneChainbody() { return true; }

	@ConfigItem(keyName="showRuneKiteshield", name="Kiteshield", description="Show Rune kiteshield", section=runeSection, position=22)
	default boolean showRuneKiteshield() { return true; }

	@ConfigItem(keyName="showRuneClaws", name="Claws", description="Show Rune claws", section=runeSection, position=23)
	default boolean showRuneClaws() { return true; }

	@ConfigItem(keyName="showRuneTwoHandedSword", name="2h sword", description="Show Rune 2h sword", section=runeSection, position=24)
	default boolean showRuneTwoHandedSword() { return true; }

	@ConfigItem(keyName="showRunePlatelegs", name="Platelegs", description="Show Rune platelegs", section=runeSection, position=25)
	default boolean showRunePlatelegs() { return true; }

	@ConfigItem(keyName="showRunePlateskirt", name="Plateskirt", description="Show Rune plateskirt", section=runeSection, position=26)
	default boolean showRunePlateskirt() { return true; }

	@ConfigItem(keyName="showCannonballs", name="Cannonballs", description="Show cannonballs (steel)", section=ammoSection, position=0)
	default boolean showCannonballs() { return true; }

	@ConfigItem(keyName="showRunePlatebody", name="Platebody", description="Show Rune platebody", section=runeSection, position=27)
	default boolean showRunePlatebody() { return true; }


}