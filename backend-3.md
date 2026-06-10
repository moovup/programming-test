# Programming Test: Leaky Bucket Rate Limiter API

## Goal

Build a small REST API that uses a **single global in-memory leaky bucket rate limiter**.

The main focus is:

- Correct leaky bucket logic
- Clean code
- Unit tests for core functionality
- Clear separation between core logic and REST API layer

Keep the scope minimal. Do not over-engineer the solution.

---

## Problem Description

Implement a per-user rate limiter using the **leaky bucket algorithm**.

Rules:

- There is one global rate limiter instance for the application.
- Each user has an independent bucket inside that limiter.
- Each request adds `1` unit to the user's bucket.
- Each bucket has a fixed `capacity`.
- Each bucket leaks at a fixed `leak_rate` per second.
- If a request would overflow the user's bucket, reject the request.
- Bucket level should never go below `0`.

---

## Core Requirements
- Functional Approach: Minimize or eliminate mutation where possible
- Time-based Leaking: Buckets should leak based on elapsed time
- Per-user Buckets: Each user_id gets their own independent bucket

---

## Rate Limiter Initialization

The application should create one global limiter when the server starts.

Use this function, or equivalent method in your chosen language:

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

## Required Core Interface

Implement these functions, or equivalent methods in your chosen language:

```text
create_rate_limiter(capacity, leak_rate)
```

Creates the global rate limiter state.

Example initial state:

```json
{
  "capacity": 5,
  "leak_rate": 1.0,
  "buckets": {}
}
```

---

```text
allow_request(limiter, user_id, timestamp)
```

Checks whether a user's request is allowed.

Returns:

```text
[allowed, new_limiter_state]
```

Example usage:

```text
[allowed, limiter] = allow_request(limiter, "user1", 1710000000.0)
```

---

```text
get_bucket_state(limiter, user_id)
```

Returns bucket information for the user, or `null` if the user does not exist.

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

## How the REST API Should Use the Core Logic

The REST API should call the core functions instead of implementing the algorithm inside the handler.

Example flow:

```text
// Application startup
limiter = create_rate_limiter(5, 1.0)

// POST /allow
handle request:
    user_id = request.body.user_id
    timestamp = request.body.timestamp

    [allowed, updated_limiter] = allow_request(limiter, user_id, timestamp)

    limiter = updated_limiter

    bucket = get_bucket_state(limiter, user_id)

    if allowed:
        return 200 OK
    else:
        return 429 Too Many Requests
```

---

## REST API Requirements

Use any programming language and REST framework.

### POST /allow

Checks whether a request is allowed.

Request:

```json
{
  "user_id": "user1",
  "timestamp": 1710000000.0
}
```

Response when allowed:

```http
200 OK
```

```json
{
  "allowed": true,
  "user_id": "user1",
  "bucket": {
    "level": 1.0,
    "capacity": 5,
    "leak_rate": 1.0,
    "last_updated_at": 1710000000.0
  }
}
```

Response when rejected:

```http
429 Too Many Requests
```

```json
{
  "allowed": false,
  "user_id": "user1",
  "reason": "bucket_capacity_exceeded",
  "bucket": {
    "level": 5.0,
    "capacity": 5,
    "leak_rate": 1.0,
    "last_updated_at": 1710000000.0
  }
}
```

Invalid input should return:

```http
400 Bad Request
```

---

### GET /users/{user_id}/bucket

Returns the bucket state for a user.

Response when found:

```http
200 OK
```

```json
{
  "user_id": "user1",
  "bucket": {
    "level": 1.0,
    "capacity": 5,
    "leak_rate": 1.0,
    "last_updated_at": 1710000000.0
  }
}
```

Response when not found:

```http
404 Not Found
```

---

## Testing Requirements

Focus mainly on **unit tests for the core rate limiter logic**.

At minimum, test:

1. First request from a new user is allowed.
2. Requests fill the bucket.
3. Request is rejected when capacity would be exceeded.
4. Bucket leaks based on elapsed time.
5. Large time gap drains the bucket.
6. Multiple users have independent buckets.
7. Backward timestamp does not create invalid bucket state.
8. `get_bucket_state` returns `null` for unknown user.

REST API tests are optional, but one or two basic endpoint tests are welcome.

---

## Constraints

- Use in-memory data structures.
- Assume single-threaded execution.
- Timestamps are Unix epoch seconds and may be floating-point values.
- User IDs are strings.
- No database is required.
- No Redis is required.
- No authentication is required.
- No distributed/multi-node support is required.

---

## Deliverables

Please provide:

1. Core rate limiter implementation
2. Minimal REST API implementation
3. Unit tests for core functionality
4. Brief README with:
   - How to run the app
   - How to run tests
   - Design decisions
   - Assumptions and trade-offs
5. Source code in a Git repository: GitHub, GitLab, or Bitbucket
