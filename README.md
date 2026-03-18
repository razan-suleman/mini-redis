#  Mini Redis (Java)

A tiny in-memory database I built to understand how real systems actually work (and suffer a little in the process).
his semester I finished my Software 1 course in TLV university, which started shifting from just writing code to thinking more about system design (which is amazing :))
So I wanted to build a system end-to-end and understand how the pieces fit together in practice.

## What is this?

This is a **mini version of Redis**, built from scratch in Java.

It’s a simple server that:

* stores key-value pairs in memory
* accepts commands over TCP
* supports expiration (TTL)
* can persist data to disk

Basically:

> You send it commands → it responds → it remembers things 


## Features

Current version supports:

* `PING` → check if server is alive
* `SET key value` → store a value
* `GET key` → retrieve value
* `DEL key` → delete key
* `EXPIRE key seconds` → set time-to-live

### Example

```text
SET name razan
GET name
→ razan

EXPIRE name 5
(wait 5 seconds...)

GET name
→ (nil)
```

## How it works 

The system is built in layers:

```text
Client
  ↓
TCP Server
  ↓
Command Parser
  ↓
Command Executor
  ↓
In-memory Store
  ↓
Persistence
```

### Key idea

* **Server** → handles connections
* **Parser** → understands commands
* **Executor** → decides what to do
* **Store** → holds the data

---

## Expiration (TTL)

Each key can have an expiration time.

Internally:

```java
expireAt = currentTime + seconds * 1000
```

On every access:

* if expired → remove key
* otherwise → return value

Lazy deletion because:

> I’m not trying to reinvent the entire Redis scheduler (yet)

---

##  Persistence

Data can be saved to disk using a simple format:

```text
key|value|expireAt
```
Example:

```text
name|razan|null
session|abc|1710000000000
```

On startup:

* file is loaded
* expired keys are ignored

## 🌐 Networking

The server uses:

* `ServerSocket` → listens for connections
* `Socket` → handles client communication
* `BufferedReader` → reads commands
* `PrintWriter` → sends responses

Each client connection is handled independently.

---

## Concurrency

Supports multiple clients using threads.

Basic approach:

* one thread per client
* shared store with synchronization

Yes, it’s simple. Yes, it works.

---

## How to run

1. Start the server:

```bash
java Main
```

2. Connect using:

```bash
telnet localhost 6379
```

3. Try commands:

```text
PING
SET x 10
GET x
DEL x
```

This project forced me to understand:

* how client-server systems actually work
* how data is stored and accessed efficiently
* why parsing and protocol design matter
* Debugging sockets at 2AM builds character.

