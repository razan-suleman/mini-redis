# Architecture

## Overview

mini-redis is a single-process, single-threaded in-memory key-value store that speaks a subset of the Redis protocol over TCP.

```
┌─────────────────────────────────────────────────────────┐
│                        Client                           │
│              (redis-cli / custom client)                │
└────────────────────────┬────────────────────────────────┘
                         │ TCP (port 6379)
                         ▼
┌─────────────────────────────────────────────────────────┐
│                       Server                            │
│  Accepts connections, reads raw bytes, dispatches work  │
└────────────┬──────────────────────────┬─────────────────┘
             │                          │
             ▼                          ▼
┌────────────────────┐      ┌────────────────────────────┐
│  Protocol (RESP)   │      │     Command Handler         │
│  Parser / Writer   │◄────►│  Looks up & executes cmds  │
└────────────────────┘      └──────────┬─────────────────┘
                                        │
                   ┌────────────────────┼──────────────────┐
                   ▼                    ▼                   ▼
        ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
        │    DataStore     │  │ ExpirationManager│  │PersistenceManager│
        │ (in-memory map)  │  │  (TTL tracking)  │  │  (dump / restore)│
        └──────────────────┘  └──────────────────┘  └──────────────────┘
```

## Components

### Server
Listens on TCP port 6379 (configurable). Accepts client connections and manages the read/write lifecycle for each socket. Delegates byte streams to the protocol layer.

### Protocol
Implements the [Redis Serialization Protocol (RESP)](protocol.md). Parses incoming RESP frames into command arrays and serialises responses back to RESP.

### Commands
A command registry maps command names (e.g. `SET`, `GET`) to handler implementations. Each handler validates its arguments, interacts with the store, and returns a response value.

### Store
A thread-safe `HashMap`-backed data store. All reads and writes go through this layer. Supports String, List, Hash, and Set value types (extensible).

### Expiration
A background task periodically scans keys with a TTL and evicts expired ones. Lazy expiration is also applied on every read.

### Persistence
Supports saving the current dataset to `data/dump.dat` in a binary format (inspired by RDB). On startup the file is loaded to restore state.

## Threading Model

The server uses a single-threaded event loop (similar to Redis). All command processing happens on one thread, eliminating the need for locks on the data store in the hot path. The expiration background task runs on a dedicated daemon thread but only calls into the store through a synchronized interface.
