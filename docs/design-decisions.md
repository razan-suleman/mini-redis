# Design Decisions

## Single-threaded event loop

**Decision:** Process all commands on a single thread.

**Rationale:** Redis itself is single-threaded for command processing, and for good reason — it eliminates lock contention on the data store and keeps the code simple. For an educational implementation, correctness and clarity outweigh raw throughput. A single thread also makes the expiration and persistence layers easier to reason about.

**Trade-off:** Throughput is limited to what one core can deliver. Long-running commands (e.g. `KEYS *` on a huge dataset) block all other clients.

---

## RESP2 over RESP3

**Decision:** Implement RESP2, not RESP3.

**Rationale:** RESP3 adds typed maps, sets, and push messages, which is valuable for a production client but adds significant parsing complexity. RESP2 is supported by every Redis client library, making it easy to test with `redis-cli` without any flags.

---

## HashMap-backed store with String keys

**Decision:** Use `java.util.HashMap<String, Object>` as the primary storage.

**Rationale:** Simple, fast, and well-understood. The value type is `Object` to allow storing different Redis value types (String, List, Hash, Set) in the same map. Type checks are performed at the command layer.

**Alternative considered:** A typed per-structure map (`Map<String, String>`, `Map<String, List<String>>`, etc.) — rejected because it complicates command dispatch and makes generic operations like `DEL` or `TTL` harder.

---

## Lazy + periodic expiration

**Decision:** Expire keys both lazily (on access) and actively (background sweep every second).

**Rationale:** Lazy-only expiration leaks memory for keys that are never read again. Active-only is expensive if many keys are set with short TTLs. The combination matches Redis's own strategy and keeps memory bounded without burning CPU.

---

## Binary RDB-style persistence

**Decision:** Serialize the store to a binary file (`data/dump.dat`) using Java's `ObjectOutputStream` as a starting point.

**Rationale:** Text formats (JSON, CSV) are easy to read but slow to parse at load time and are not well-suited to binary value types. A binary format matches what Redis does and is a natural stepping stone to implementing a proper RDB parser.

**Future work:** Replace `ObjectOutputStream` with a custom binary format to gain more control over versioning and forward compatibility.

---

## No AOF (Append-Only File)

**Decision:** Only RDB persistence is implemented; AOF is out of scope.

**Rationale:** AOF requires fsync management, log compaction (BGREWRITEAOF), and careful handling of partial writes — all significant complexity. For the goals of this project, point-in-time snapshots are sufficient.

---

## Maven over Gradle

**Decision:** Use Maven as the build tool.

**Rationale:** Maven's conventions are explicit and widely understood. For a project of this size, Maven's verbosity is not a burden, and it avoids the need to learn Gradle's DSL.
