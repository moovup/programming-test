package com.moovupDemo;


public class RateLimiter {

    // Creates a new rate limiter
    public static Limiter createRateLimiter(int capacity, double leakRatePerSecond) {
        return new Limiter(capacity, leakRatePerSecond);
    }

    // Determines if a request should be allowed
    public static Object[] allowRequest(Limiter limiter, String userId, long timestamp) {
        Bucket oldBucket = limiter.buckets.getOrDefault(userId, new Bucket(0.0, timestamp)); // Automatically initialize a new bucket
        Bucket updatedBucket = oldBucket.leak(timestamp, limiter.leakRatePerMillis, limiter.capacity); // updating timestamp

        if (updatedBucket.level < limiter.capacity) {
            Bucket newBucket = updatedBucket.addRequest(); // adding request if allowed
            limiter.buckets.put(userId, newBucket);
            return new Object[]{true, limiter};
        } else {
            limiter.buckets.put(userId, updatedBucket); // still update timestamp
            return new Object[]{false, limiter};
        }
    }

    // Returns current bucket information for debugging
    public static Bucket getBucketState(Limiter limiter, String userId) {
        return limiter.buckets.getOrDefault(userId, null);
    }

    // Example usage
    public static void main(String[] args) throws InterruptedException {
        Limiter limiter = createRateLimiter(5, 1.0); // 5 capacity, 1 unit/sec leak
        String user = "alice";
        String user2 = "dave";
        for (int i = 0; i < 10; i++) {
            long now = System.currentTimeMillis();
            Object[] result = allowRequest(limiter, user, now);
            Object[] result2 = allowRequest(limiter, user2, now);

            boolean allowed = (boolean) result[0];
            boolean allowed2 = (boolean) result2[0];

            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Rejected"));
            System.out.println("Bucket state: " + getBucketState(limiter, user));

            System.out.println("Request " + i + ": " + (allowed2 ? "Allowed" : "Rejected"));
            System.out.println("Bucket state: " + getBucketState(limiter, user2));
            Thread.sleep(300);
        }
    }
}
