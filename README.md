# mini-redis

A lightweight Redis-compatible in-memory data store implemented in Java.

## Features

- RESP (Redis Serialization Protocol) parsing
- Core Redis commands (GET, SET, DEL, EXISTS, EXPIRE, TTL, ...)
- Key expiration with TTL support
- RDB-style persistence (dump to disk)
- Single-threaded event loop server

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/mini-redis.jar
```

Or use the provided script:

```bash
bash scripts/run-client.sh
```

### Benchmark

```bash
bash scripts/benchmark.sh
```

## Project Structure

```
src/main/java/com/razan/miniredis/
├── server/       # TCP server and client connection handling
├── protocol/     # RESP protocol parser and writer
├── commands/     # Command dispatch and implementations
├── store/        # In-memory key-value store
├── persistence/  # Dump/restore to disk (dump.dat)
└── expiration/   # TTL tracking and key expiration
```

## Documentation

- [Architecture](docs/architecture.md)
- [Protocol](docs/protocol.md)
- [Design Decisions](docs/design-decisions.md)

## License

MIT
