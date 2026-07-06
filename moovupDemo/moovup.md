# Programming Test

## Problem Description

You are tasked with implementing a rate limiter using a **leaky bucket algorithm** where:

- Each user has their own bucket with a fixed capacity
- Requests fill the bucket (add 1 unit)
- The bucket "leaks" at a constant rate over time
- If a request would overflow the bucket, it should be rejected

## Required Interface

```pseudocode
function create_rate_limiter(capacity, leak_rate):
    // Creates a new rate limiter
    // capacity: maximum bucket size
    // leak_rate: units leaked per second

function allow_request(limiter, user_id, timestamp):
    // Determines if a request should be allowed
    // Returns: [boolean_allowed, new_limiter_state]

function get_bucket_state(limiter, user_id):
    // Returns current bucket information for debugging
    // Returns: bucket_info or null if user doesn't exist
```

## Core Requirements

1. **Functional Approach**: Minimize or eliminate mutation where possible
2. **Time-based Leaking**: Buckets should leak based on elapsed time
3. **Per-user Buckets**: Each user_id gets their own independent bucket

## Implementation Tasks

### Part 1: Basic Implementation
Implement the three core functions above.

### Part 2: Edge Cases
Handle these scenarios:
- First request from a new user
- Timestamps that go backwards
- Very large time gaps between requests
- Bucket overflow scenarios

## Test Scenarios

Write tests that demonstrate your solution works for:

```pseudocode
// Basic functionality
limiter = create_rate_limiter(capacity=5, leak_rate=1.0)
[allowed1, limiter] = allow_request(limiter, "user1", timestamp=0)
[allowed2, limiter] = allow_request(limiter, "user1", timestamp=1)

// Burst handling
// Multiple rapid requests that should be rejected

// Time-based leaking
// Requests separated by time should be allowed after leaking

// Multiple users
// Independent bucket behavior
```

## Constraints

- Assume single-threaded execution
- Timestamps are Unix epoch seconds (can be floating-point)
- User IDs are strings
- Focus on correctness first, then optimization

## Deliverables

1. Core implementation of the three required functions
2. Test cases demonstrating correctness across key scenarios
3. Brief explanation of your design decisions and trade-offs
4. Source code must be stored in a git repository (github /gitlab / bitbucket)


## Technologies Used
- Java
- JUnit for testing