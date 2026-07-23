package com.smithingtracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class ItemBoxOverlay extends OverlayPanel
{
    private final SmithingTrackerPlugin plugin;
    private final SmithingTrackerConfig config;
    private final Client client;

    private static final Color BACKGROUND = new Color(10, 10, 15, 200);
    private static final Color ACCENT = new Color(255, 180, 50);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color TOOLTIP_BG = new Color(20, 20, 25, 240);

    private static final int ICON_SIZE = 32;
    private static final int ICON_PADDING = 4;
    private static final int MIN_COLS = 1;
    private static final int MIN_ROWS = 1;

    private int currentCols = 4;
    private int currentRows = 1;
    private final Map<Rectangle, ItemTooltipData> tooltipMap = new HashMap<>();

    @Inject
    private ItemBoxOverlay(SmithingTrackerPlugin plugin, SmithingTrackerConfig config, Client client)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.DETACHED);
        setPriority(OverlayPriority.HIGH);
        setMovable(true);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showOverlay()) return null;
        Map<Integer, Integer> itemsMap = plugin.getItemsSmithedMap();
        if (itemsMap.isEmpty()) return null;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int visibleCount = 0;
        for (Integer itemId : itemsMap.keySet()) if (plugin.shouldShowItem(itemId)) visibleCount++;
        if (visibleCount == 0) return null;
        Dimension preferred = getPreferredSize();
        if (preferred!= null) currentCols = Math.max(MIN_COLS, (preferred.width - ICON_PADDING) / (ICON_SIZE + ICON_PADDING));
        currentRows = Math.max(MIN_ROWS, (int) Math.ceil(visibleCount / (double) currentCols));
        int boxWidth = (currentCols * ICON_SIZE) + ((currentCols + 1) * ICON_PADDING);
        int boxHeight = (currentRows * ICON_SIZE) + ((currentRows + 1) * ICON_PADDING) + 20;
        graphics.setColor(BACKGROUND);
        graphics.fillRect(0, 0, boxWidth, boxHeight);
        graphics.setColor(ACCENT);
        graphics.drawRect(0, 0, boxWidth - 1, boxHeight - 1);
        graphics.setColor(ACCENT);
        graphics.setFont(new Font("RuneScape Small", Font.BOLD, 15));
        graphics.drawString("ITEMS", ICON_PADDING, 14);
        tooltipMap.clear();
        Point mousePos = null;
        Rectangle bounds = getBounds();
        if (bounds!= null && client.getMouseCanvasPosition()!= null)
        {
            mousePos = new Point((int) client.getMouseCanvasPosition().getX() - bounds.x, (int) client.getMouseCanvasPosition().getY() - bounds.y);
        }
        int xPos = ICON_PADDING; int yPos = 20; int col = 0;
        for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet())
        {
            int itemId = entry.getKey();
            if (!plugin.shouldShowItem(itemId)) continue;
            BufferedImage itemImg = plugin.getItemManager().getImage(itemId, 1, false);
            if (itemImg!= null)
            {
                Rectangle iconBounds = new Rectangle(xPos, yPos, ICON_SIZE, ICON_SIZE);
                graphics.drawImage(itemImg, xPos, yPos, ICON_SIZE, ICON_SIZE, null);
                int totalCount = plugin.getItemCountTotal(itemId);
                int gePrice = plugin.getItemManager().getItemPrice(itemId);
                long totalValue = (long) totalCount * gePrice;
                tooltipMap.put(iconBounds, new ItemTooltipData(plugin.getItemManager().getItemComposition(itemId).getName(), totalCount, gePrice, totalValue, itemId));
                graphics.setColor(GOLD);
                graphics.setFont(new Font("RuneScape Small", Font.PLAIN, 15));
                String countStr = String.valueOf(totalCount);
                int strWidth = graphics.getFontMetrics().stringWidth(countStr);
                graphics.drawString(countStr, xPos + (ICON_SIZE - strWidth) / 2, yPos + ICON_SIZE - 2);
                col++; if (col >= currentCols) { col = 0; xPos = ICON_PADDING; yPos += ICON_SIZE + ICON_PADDING; } else { xPos += ICON_SIZE + ICON_PADDING; }
            }
        }
        renderTooltip(graphics, mousePos);
        return new Dimension(boxWidth, boxHeight);
    }

    private void renderTooltip(Graphics2D graphics, Point mousePos)
    {
        if (mousePos == null) return;
        for (Map.Entry<Rectangle, ItemTooltipData> entry : tooltipMap.entrySet())
        {
            if (entry.getKey().contains(mousePos))
            {
                ItemTooltipData data = entry.getValue();
                NumberFormat nf = NumberFormat.getInstance();
                int barId = plugin.getBarIdForItem(data.name);
                int barsPerItem = plugin.getBarsPerItem(data.name);
                int barPrice = plugin.getEffectiveBarPrice(barId);
                int remainingBars = plugin.getRemainingBars(barId);
                int canMake = barsPerItem == 0? 0 : remainingBars / barsPerItem;
                int profitEach = data.price - (barsPerItem * barPrice);
                long totalProfit = (long) canMake * profitEach;
                int totalQty = data.count + canMake;
                long totalQtyValue = (long) totalQty * data.price;

                String[] lines = {
                        data.name,
                        "Quantity: " + nf.format(data.count),
                        "Quantity value: " + nf.format(data.totalValue) + " gp",
                        "Can make: " + nf.format(canMake) + " with " + nf.format(remainingBars) + " bars",
                        "Total qty value: " + nf.format(totalQtyValue) + " gp (" + nf.format(totalQty) + " total)",
                        "Total profit: " + nf.format(totalProfit) + " gp"
                };

                graphics.setFont(new Font("RuneScape Small", Font.PLAIN, 15));
                int maxWidth = 0;
                for (String line : lines) maxWidth = Math.max(maxWidth, graphics.getFontMetrics().stringWidth(line));
                int tooltipWidth = maxWidth + 12;
                int tooltipHeight = lines.length * 14 + 8;
                int tooltipX = mousePos.x + 15;
                int tooltipY = mousePos.y + 15;
                graphics.setColor(TOOLTIP_BG);
                graphics.fillRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
                graphics.setColor(ACCENT);
                graphics.drawRect(tooltipX, tooltipY, tooltipWidth - 1, tooltipHeight - 1);
                int y = tooltipY + 14;
                for (int i = 0; i < lines.length; i++)
                {
                    if (i == 0) graphics.setColor(GOLD);
                    else if (i == 5) graphics.setColor(totalProfit >= 0? new Color(50, 255, 100) : new Color(255, 100, 100));
                    else graphics.setColor(Color.WHITE);
                    graphics.drawString(lines[i], tooltipX + 6, y);
                    y += 14;
                }
                return;
            }
        }
    }

    private static class ItemTooltipData
    {
        final String name; final int count; final int price; final long totalValue; final int itemId;
        ItemTooltipData(String name, int count, int price, long totalValue, int itemId) { this.name = name; this.count = count; this.price = price; this.totalValue = totalValue; this.itemId = itemId; }
    }
}