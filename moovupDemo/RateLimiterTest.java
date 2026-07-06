package com.moovupDemo;
import org.junit.Test;

import static org.junit.Assert.*;

public class RateLimiterTest {
    @Test
    public void testFirstRequestFromNewUser() {
        Limiter limiter = RateLimiter.createRateLimiter(5, 1);
        long timestamp = 1000;
        Object[] result = RateLimiter.allowRequest(limiter, "alice", timestamp);
        assertTrue((Boolean) result[0]);
        Bucket bucket = RateLimiter.getBucketState(limiter, "alice");
        assertEquals(1.0, bucket.level, 0.01);
        assertEquals(timestamp, bucket.lastUpdated);
    }

    @Test
    public void testMultipleUsersIsolation() {
        Limiter limiter = RateLimiter.createRateLimiter(5, 1);
        long ts = 1000;

        assertTrue((Boolean) RateLimiter.allowRequest(limiter, "alice", ts)[0]);
        assertTrue((Boolean) RateLimiter.allowRequest(limiter, "bob", ts)[0]);

        Bucket aliceBucket = RateLimiter.getBucketState(limiter, "alice");
        Bucket bobBucket = RateLimiter.getBucketState(limiter, "bob");

        assertEquals(1.0, aliceBucket.level, 0.01);
        assertEquals(1.0, bobBucket.level, 0.01);
        assertNotSame(aliceBucket, bobBucket);
    }
    @Test
    public void testSteadyRequestsAtLeakRate() {
        Limiter limiter = RateLimiter.createRateLimiter(5, 1);
        String user = "bob";
        long start = 1000;

        for (int i = 0; i < 5; i++) {
            long ts = start + i * 1000; // 1 sec apart
            Object[] result = RateLimiter.allowRequest(limiter, user, ts);
            assertTrue((Boolean) result[0]);
            Bucket bucket = RateLimiter.getBucketState(limiter, user);
            assertEquals(1.0, bucket.level, 0.01);
        }
    }

    @Test
    public void testLargeTimeGapDrainsBucket() {
        Limiter limiter = RateLimiter.createRateLimiter(5, 1);
        String user = "dave";

        RateLimiter.allowRequest(limiter, user, 1000); // fill 1 unit
        Object[] result = RateLimiter.allowRequest(limiter, user, 10000); // 9 sec later
        assertTrue((Boolean) result[0]);
        Bucket bucket = RateLimiter.getBucketState(limiter, user);
        assertEquals(1.0, bucket.level, 0.01); // previous drained, new added
    }

}
