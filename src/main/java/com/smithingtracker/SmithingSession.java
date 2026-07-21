package com.smithingtracker;
import java.time.Duration;
import java.time.Instant;
public class SmithingSession
{
    private Instant startTime = null;
    private int itemsSmithed = 0;
    public void reset() { startTime = null; itemsSmithed = 0; }
    public void incrementItems() { if (startTime == null) startTime = Instant.now(); itemsSmithed++; }
    public Duration getRuntime() { return startTime == null? Duration.ZERO : Duration.between(startTime, Instant.now()); }
    public double getPerHour() { if (startTime == null || itemsSmithed == 0) return 0; double hours = getRuntime().toMillis() / 3600000.0; return hours == 0? 0 : itemsSmithed / hours; }
    public int getItemsSmithed() { return itemsSmithed; }
}