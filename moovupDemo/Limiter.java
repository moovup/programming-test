package com.moovupDemo;

import java.util.HashMap;
import java.util.Map;

public class Limiter {
    final int capacity;
    final double leakRatePerMillis;
    final Map<String, Bucket> buckets;

    Limiter(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerMillis = leakRatePerSecond / 1000.0;
        this.buckets = new HashMap<>();
    }
}
