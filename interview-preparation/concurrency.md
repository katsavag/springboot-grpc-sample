# Volatile in Java Concurrency

## How `volatile` works
### Memory Visibility
When a thread modifies a `volatile` variable:
1. The value is immediately written back to main memory
2. All other threads will see the most recent write
3. The JVM inserts necessary memory barriers

### Key characteristics
- **Visibility guarantee**: Changes to a `volatile` variable are always visible to other threads
- **Atomicity for single operations**: Reading/writing primitive types (except `long`/`double`) is atomic
- **No mutual exclusion**: Unlike `synchronized`, it doesn't provide locking
- **Prevents instruction reordering**: The JVM cannot reorder memory operations around volatile reads/writes

### When to use `volatile`
`volatile` is appropriate when:
- You need visibility but not mutual exclusion
- A single thread updates the value, and others only read it
- You're implementing a simple flag (like a `boolean` for stopping a thread)

## Limitations of `volatile`
- Doesn't guarantee atomicity for compound operations (like i++)
- Not a replacement for proper synchronization when you need atomic compound operations
- No locking/mutual exclusion
