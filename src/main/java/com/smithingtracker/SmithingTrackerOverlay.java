package com.smithingtracker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.Duration;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
public class SmithingTrackerOverlay extends OverlayPanel
{
    private final SmithingTrackerPlugin plugin;
    private final SmithingTrackerConfig config;
    private static final Color BACKGROUND = new Color(10, 10, 15, 200);
    private static final Color ACCENT = new Color(255, 180, 50);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color GREEN = new Color(50, 255, 100);
    private static final Color GREY = new Color(150, 150, 150);
    private static final Color CYAN = new Color(100, 200, 255);
    @Inject private SmithingTrackerOverlay(SmithingTrackerPlugin p, SmithingTrackerConfig c) { super(p); plugin = p; config = c; setPosition(OverlayPosition.TOP_LEFT); setPriority(OverlayPriority.HIGH); }
    @Override public Dimension render(Graphics2D g)
    {
        if (!config.showOverlay()) return null;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(BACKGROUND);
        panelComponent.getChildren().add(TitleComponent.builder().text("⚒ SMITHING TRACKER ⚒").color(ACCENT).build());
        Duration t = plugin.getTimeRunning();
        String timeStr = String.format("%dh %02dm %02ds", t.toHours(), t.toMinutesPart(), t.toSecondsPart());
        String status = plugin.getStatus();
        Color sc = GREY; String st = "● Idle";
        if (status.equals("Smithing")) { sc = GREEN; st = "● Smithing"; } else if (status.equals("Banking")) { sc = ACCENT; st = "● Banking"; } else if (status.equals("Running")) { sc = CYAN; st = "● Running"; }
        panelComponent.getChildren().add(LineComponent.builder().left("Status").leftColor(TEXT_COLOR).right(st).rightColor(sc).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Time").leftColor(TEXT_COLOR).right(timeStr).rightColor(ACCENT).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Smithed").leftColor(TEXT_COLOR).right(String.format("%,d", plugin.getItemsSmithed())).rightColor(new Color(100, 200, 255)).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Per Hour").leftColor(TEXT_COLOR).right(String.format("%,.0f", plugin.getItemsPerHour())).rightColor(new Color(100, 255, 200)).build());
        String itemName = plugin.getTrackedItemName();
        panelComponent.getChildren().add(LineComponent.builder().left("Item").leftColor(TEXT_COLOR).right(itemName.equals("None")? "Detecting..." : itemName).rightColor(itemName.equals("None")? GREY : new Color(200, 200, 200)).build());
        double gp = plugin.getGpPerHour();
        if (plugin.getCurrentBarId()!= -1 && gp > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder().left("Profit/hr").leftColor(GOLD).right(formatGp(plugin.getProfitPerHour())).rightColor(plugin.getProfitPerHour() >= 0? GREEN : new Color(255, 100, 100)).build());
        }
        int sel = config.barType().getItemId();
        Map<Integer, Integer> bank = plugin.getBarsInBank();
        Map<Integer, Integer> used = plugin.getBarsUsed();
        int bankCount = bank.getOrDefault(sel, 0);
        int usedCount = used.getOrDefault(sel, 0);
        if (bankCount > 0)
        {
            String barName = plugin.getItemManager().getItemComposition(sel).getName();
            int price = plugin.getItemManager().getItemPrice(sel);
            int rem = bankCount - usedCount;
            if (rem < 0) rem = 0;
            panelComponent.getChildren().add(LineComponent.builder().left(barName).leftColor(CYAN).right(String.format("%d (%s)", rem, formatGp(rem * price))).rightColor(TEXT_COLOR).build());
        }
        return super.render(g);
    }
    private String formatGp(double gp) { if (gp >= 1_000_000) return String.format("%.2fM", gp / 1_000_000); else if (gp >= 1_000) return String.format("%.1fK", gp / 1_000); return String.format("%,.0f", gp); }
}