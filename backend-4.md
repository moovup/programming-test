# Programming Test: Leaky Bucket Rate Limiter with REST API

## Goal

Build a small REST API that uses a **single global in-memory leaky bucket rate limiter**.
- Each user has their own bucket with a fixed capacity
- Requests fill the bucket (add 1 unit)
- The bucket "leaks" at a constant rate over time
- If a request would overflow the bucket, it should be rejected

---

## Required Core Interface

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

---

## Core Requirements

1. **Functional Approach**: Minimize or eliminate mutation where possible
2. **Time-based Leaking**: Buckets should leak based on elapsed time
3. **Per-user Buckets**: Each user_id gets their own independent bucket

---

## Rate Limiter Initialization

The application should create one global limiter when the server starts.

```text
create_rate_limiter(capacity, leak_rate)
```

Example startup behavior:

```text
// Application startup

limiter = create_rate_limiter(capacity = 5, leak_rate = 1.0)
```

The REST API does **not** need an endpoint to create limiters.

You may configure `capacity` and `leak_rate` using environment variables, a config file, or documented default values.

Default values are acceptable:

```text
capacity = 5
leak_rate = 1.0
```

---

## REST API Requirements

The REST API should call the core functions instead of implementing the algorithm inside the handler.

The API should expose two behaviors:

### 1. Check whether a request is allowed

Call `allow_request`, and update the global limiter state with the returned state.


### 2. Get bucket state for a user

Call `get_bucket_state`, and return the bucket information.


---

## Implementation Tasks

### Part 1: Core Implementation

Implement the three core functions above.

### Part 2: REST API

Expose the two behaviors described above using your chosen framework and conventions.

### Part 3: Edge Cases

Handle these scenarios in the core logic:

- First request from a new user
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
