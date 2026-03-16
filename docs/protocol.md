# Protocol

mini-redis uses the **Redis Serialization Protocol (RESP)**, specifically RESP2.

## RESP2 Types

| First byte | Type            | Example wire format              |
|-----------|-----------------|----------------------------------|
| `+`       | Simple String   | `+OK\r\n`                        |
| `-`       | Error           | `-ERR unknown command\r\n`       |
| `:`       | Integer         | `:1000\r\n`                      |
| `$`       | Bulk String     | `$5\r\nhello\r\n`                |
| `*`       | Array           | `*2\r\n$3\r\nGET\r\n$3\r\nfoo\r\n` |

Null bulk string: `$-1\r\n`  
Null array: `*-1\r\n`

## Request Format

Clients always send commands as a RESP Array of Bulk Strings:

```
*3\r\n
$3\r\n
SET\r\n
$3\r\n
foo\r\n
$3\r\n
bar\r\n
```

## Response Format

The server replies with the appropriate RESP type depending on the command:

| Command        | Response type   |
|----------------|-----------------|
| SET            | Simple String (`+OK`) |
| GET (found)    | Bulk String     |
| GET (missing)  | Null Bulk String |
| DEL            | Integer (count deleted) |
| EXISTS         | Integer (0 or 1) |
| TTL            | Integer (seconds, -1 = no TTL, -2 = not found) |
| Error cases    | Error           |

## Inline Commands (optional)

As a convenience for raw TCP clients (e.g. `telnet`), single-line inline commands (space-separated, terminated with `\r\n` or `\n`) are also supported.

## Supported Commands

| Command | Syntax | Description |
|---------|--------|-------------|
| PING    | `PING [message]` | Returns PONG or echo |
| SET     | `SET key value [EX seconds]` | Set a key |
| GET     | `GET key` | Get a key |
| DEL     | `DEL key [key ...]` | Delete one or more keys |
| EXISTS  | `EXISTS key` | Check if a key exists |
| EXPIRE  | `EXPIRE key seconds` | Set TTL on a key |
| TTL     | `TTL key` | Get remaining TTL |
| KEYS    | `KEYS pattern` | List matching keys |
| FLUSHDB | `FLUSHDB` | Delete all keys |
| SAVE    | `SAVE` | Persist dataset to disk |
