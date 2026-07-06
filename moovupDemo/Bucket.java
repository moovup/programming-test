package com.moovupDemo;

public class Bucket {
    final double level;
    final long lastUpdated;

    Bucket(double level, long lastUpdated) {
        this.level = level;
        this.lastUpdated = lastUpdated;
    }

   Bucket leak(long now, double leakRatePerMillis, int capacity) {
        if (now < lastUpdated) return this; // Ignored to prevent negative leaks
        long elapsed = now - lastUpdated;
        double leaked = elapsed * leakRatePerMillis;
        double newLevel = Math.max(0.0, level - leaked);
        return new Bucket(Math.min(newLevel, capacity), now);
    }

     Bucket addRequest() {
        return new Bucket(level + 1, lastUpdated);
    }

    @Override
    public String toString() {
        return "Bucket{level=" + level + ", lastUpdated=" + lastUpdated + "}";
    }
}
