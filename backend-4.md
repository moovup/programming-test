# Programming Test: Leaky Bucket Rate Limiter with REST API

## Goal

Build a leaky bucket rate limiter with a minimal REST API on top.

The main focus is:

- Correct leaky bucket logic
- Clean code and clear separation between core logic and the REST API layer
- Unit tests for core functionality

Keep the scope minimal. Do not over-engineer the solution.

---

## Problem Description

You are tasked with implementing a rate limiter using a **leaky bucket algorithm** where:

- There is one **global rate limiter instance** for the application
- Each user has their own independent bucket inside that limiter
- Requests fill the bucket (add 1 unit)
- The bucket "leaks" at a constant rate over time
- If a request would overflow the bucket, it should be rejected
- Bucket level should never go below `0`

---

## Required Core Interface

Implement these functions, or equivalent methods in your chosen language:

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

Example bucket state:

```json
{
  "level": 1.0,
  "capacity": 5,
  "leak_rate": 1.0,
  "last_updated_at": 1710000000.0
}
```

---

## Core Requirements

1. **Functional Approach**: Minimize or eliminate mutation where possible
2. **Time-based Leaking**: Buckets should leak based on elapsed time
3. **Per-user Buckets**: Each user_id gets their own independent bucket

---

## Global Rate Limiter

The application should create **one global limiter** when the server starts:

```pseudocode
// Application startup
limiter = create_rate_limiter(capacity=5, leak_rate=1.0)
```

You may configure `capacity` and `leak_rate` using environment variables, a config file, or documented default values. Default values are acceptable:

```text
capacity = 5
leak_rate = 1.0
```

The REST API does **not** need an endpoint to create limiters.

---

## REST API Requirements

Use any programming language and REST framework.

You decide the route naming, HTTP verbs, and exact response shapes. The API should expose two behaviors:

### 1. Check whether a request is allowed

Accept a `user_id` and `timestamp`, call `allow_request`, and update the global limiter state with the returned state.

When the request is **allowed**, respond with a success status and include enough information for the caller to understand the outcome (e.g. that the request was allowed and the user's current bucket state).

When the request is **rejected** because the bucket would overflow, respond with an appropriate rate-limit status (e.g. `429 Too Many Requests`) and include the user's bucket state so the caller can see why it was rejected.

Invalid input (e.g. missing or malformed fields) should return a client error (e.g. `400 Bad Request`).

The REST handler should call the core functions instead of reimplementing the algorithm inline.

Example flow:

```pseudocode
// Application startup
limiter = create_rate_limiter(5, 1.0)

// Incoming HTTP request
handle request:
    user_id = request.body.user_id
    timestamp = request.body.timestamp

    [allowed, updated_limiter] = allow_request(limiter, user_id, timestamp)
    limiter = updated_limiter
    bucket = get_bucket_state(limiter, user_id)

    if allowed:
        return success response with bucket state
    else:
        return rate-limit response with bucket state
```

### 2. Get bucket state for a user

Accept a `user_id`, call `get_bucket_state`, and return the bucket information.

When the user exists, respond with success and the bucket state.

When the user has no bucket yet, respond with an appropriate not-found status (e.g. `404 Not Found`).

---

## Implementation Tasks

### Part 1: Core Implementation

Implement the three core functions above.

### Part 2: REST API

Expose the two behaviors described above using your chosen framework and conventions.

### Part 3: Edge Cases

Handle these scenarios in the core logic:

- First request from a new user
- Timestamps that go backwards
- Very large time gaps between requests
- Bucket overflow scenarios

---

## Test Scenarios

Write unit tests for the core rate limiter logic. At minimum, cover:

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

// Unknown user
// get_bucket_state returns null for a user that has never made a request
```

REST API tests are optional, but one or two basic endpoint tests are welcome.

---

## Constraints

- Use in-memory data structures
- Assume single-threaded execution
- Timestamps are Unix epoch seconds (can be floating-point)
- User IDs are strings
- No database, Redis, authentication, or distributed/multi-node support is required
- Focus on correctness first, then optimization

---

## Deliverables

1. Core implementation of the three required functions
2. Minimal REST API implementation
3. Unit tests demonstrating correctness across key scenarios
4. Brief README with:
   - How to run the app
   - How to run tests
   - Design decisions and trade-offs
5. Source code must be stored in a git repository (GitHub / GitLab / Bitbucket)
