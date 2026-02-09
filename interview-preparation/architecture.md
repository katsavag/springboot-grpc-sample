# Master-Slave Database Architecture

Master-slave database architecture is a model where one database server acts as the primary (master) server and one or more database servers act as secondary (slave) servers. Let me explain how it works, when to use it, and its benefits and drawbacks.

## How It Works

1. The master database is the authoritative source where all write operations (INSERT, UPDATE, DELETE) are performed
2. The slave databases replicate data from the master database, typically in near real-time
3. Read operations can be directed to slave databases, while write operations must go to the master
4. Changes made to the master are propagated to the slaves through a replication process

## When to Use Master-Slave Architecture

This architecture is particularly useful in scenarios such as:

- **High read-to-write ratio applications**: When your application has more read operations than write operations
- **Reporting and analytics**: Running resource-intensive reports on slave databases without affecting the master
- **Geographical distribution**: Placing slave databases closer to users in different regions to reduce latency
- **Backup strategy**: Using slaves as hot standby databases for disaster recovery
- **Scaling read operations**: Distributing read load across multiple slave servers

## Benefits

1. **Improved read scalability**: By distributing read operations across multiple slaves
2. **Load balancing**: Ability to direct read queries to different servers to optimize performance
3. **High availability**: If the master fails, a slave can be promoted to master (though this requires additional configuration)
4. **Geographic distribution**: Placing slaves closer to users reduces read latency
5. **Data protection**: Additional copies of data provide redundancy
6. **Maintenance windows**: Maintenance can be performed on slaves one at a time while the system remains operational

## Problems and Limitations

1. **Replication lag**: Slaves may fall behind the master, causing data inconsistency for a period
2. **Write scalability limitations**: All writes must go to the master, creating a potential bottleneck
3. **Failover complexity**: Automatic promotion of a slave to master requires additional infrastructure
4. **Consistency model**: By default, provides eventual consistency rather than strong consistency
5. **Configuration overhead**: Setting up and maintaining replication adds operational complexity
6. **Single point of failure**: If the master fails and automated failover is not configured, write operations become unavailable
7. **Terminology concerns**: The master-slave terminology has been criticized for its historical connotations, with many systems now using terms like "primary-replica" or "leader-follower"

## Modern Alternatives

Many database systems now offer more sophisticated architectures such as:

- Multi-master replication
- Sharded architectures
- Distributed consensus-based systems (like those based on Raft or Paxos algorithms)
- Cloud-native database services with built-in high availability

These alternatives may address some of the limitations of the traditional master-slave model, particularly around write scalability and automatic failover.

# Scale Classifications in Application Traffic

When discussing application scale in terms of traffic, there are no universally standardized definitions, but I can provide general guidelines that are commonly used in the industry. These classifications typically consider metrics like daily active users (DAU), requests per second (RPS), data volume, and concurrent users.

## Small Scale Applications

**Typical characteristics:**
- Up to ~10,000 daily active users
- Up to ~10 requests per second
- Monthly traffic: Up to ~25 GB
- Concurrent users: Up to a few hundred

**Examples:**
- Small business websites
- Internal company tools
- Personal blogs or portfolios
- Early-stage startups

**Infrastructure considerations:**
- Often a single server setup or small cloud instance
- Single database (no need for complex scaling)
- Simple caching if any
- Minimal to no CDN requirements

## Medium Scale Applications

**Typical characteristics:**
- ~10,000 to ~1 million daily active users
- ~10 to ~1,000 requests per second
- Monthly traffic: ~25 GB to ~5 TB
- Concurrent users: Hundreds to tens of thousands

**Examples:**
- Growing B2B SaaS applications
- Popular regional e-commerce sites
- Medium-sized community forums
- Department-level enterprise applications

**Infrastructure considerations:**
- Multiple application servers
- Database with read replicas (master-slave setup becomes valuable)
- More sophisticated caching strategies
- CDN for static assets
- Basic load balancing

## Large Scale Applications

**Typical characteristics:**
- 1 million+ daily active users
- 1,000+ requests per second (often many thousands or even millions)
- Monthly traffic: 5+ TB (often petabytes)
- Concurrent users: Tens of thousands to millions

**Examples:**
- Major social networks
- Global e-commerce platforms
- Streaming services
- Popular mobile applications
- Financial trading platforms

**Infrastructure considerations:**
- Distributed systems architecture
- Multi-region deployments
- Database sharding and complex replication strategies
- Advanced caching (multiple layers)
- Microservices architecture
- Comprehensive CDN usage
- Sophisticated load balancing and auto-scaling
- Dedicated reliability engineering teams

## Important Considerations

1. **Industry-specific variations**: What's considered "large scale" in one industry might be "medium scale" in another.

2. **Traffic patterns matter**: An application with consistent traffic has different scaling needs than one with extreme peaks and valleys.

3. **Complexity factor**: Some applications generate less traffic but are computationally intensive or have complex data requirements.

4. **Growth trajectory**: Applications on a rapid growth curve may need to implement large-scale solutions earlier.

5. **Geographic distribution**: Global applications often need different scaling considerations than regionally-focused ones.

The transition points between these categories are not rigid, and many applications exist in the boundary areas with hybrid characteristics. The key is to design your infrastructure to handle your current needs while being adaptable to your anticipated growth.

# Back-of-the-Envelope (BoE) Estimation
## Systems & Software Engineering — Interview Cheatsheet

---

## 1. What Interviewers Expect

Back-of-the-envelope estimation evaluates:
- Engineering intuition
- Order-of-magnitude reasoning
- System decomposition skills
- Ability to sanity-check designs

Accuracy targets:
- ✅ ±1 order of magnitude → good
- ⚠️ ±2 → questionable
- ❌ >2 → design flaw

---

## 2. Core Mental Model

### Order of Magnitude
x ≈ 10^n

### Decomposition
Q = ∏ q_i  
Q = Σ q_i

### Throughput Pattern
Load = (users) × (actions/user) × (frequency)

---

## 3. Universal Engineering Assumptions

### Users & Traffic

| Parameter | Rule of Thumb |
|---------|---------------|
| Active users | 10–30% |
| Concurrent users | 1–5% |
| Peak vs average | ×3–×10 |
| Requests per user | 1–10 / min |

---

## 4. Time & Latency Constants

| Component | Typical Value |
|---------|---------------|
| CPU cycle (3 GHz) | ~0.3 ns |
| L1 cache | ~1 ns |
| L2 cache | ~5 ns |
| RAM access | ~100 ns |
| SSD read | ~100 µs |
| HDD seek | ~10 ms |
| LAN RTT | ~1 ms |
| WAN RTT | ~100 ms |
| Human perception | ~100 ms |

---

## 5. Compute & Storage

### Compute

| Resource | Approximation |
|--------|----------------|
| 1 CPU core | ~10⁹ ops/s |
| 1 server | ~10–100 cores |
| Context switch | ~1–5 µs |

### Storage

| Item | Size |
|----|------|
| Integer | 4 bytes |
| UUID | 16 bytes |
| Timestamp | 8 bytes |
| Cache line | 64 bytes |
| JSON overhead | ×2–×5 raw data |

---

## 6. Network & Data Rates

| Metric | Rule |
|------|------|
| 1 Gbps | ~125 MB/s |
| Ethernet utilization | ~70–80% |
| TCP overhead | ~5–10% |
| Typical API payload | 0.5–5 KB |
| Video streaming | ~1–5 Mbps |

---

## 7. Memory Estimation Pattern

Memory = (objects) × (object size) × (replication factor)

Example:
10M users × 1 KB/user × 3 replicas ≈ 30 GB

---

## 8. Throughput Estimation Pattern

QPS = (active users) × (actions/user/sec)

Example:
1M users  
10% active → 100k  
1 req / 10 s → 0.1 rps  
QPS ≈ 10k

---

## 9. Reliability & Capacity

### Availability
Availability = MTBF / (MTBF + MTTR)

Rules:
- “Five nines” ≈ 5 min downtime/year
- Redundancy improves availability multiplicatively

---

## 10. Scaling Heuristics

| Pattern | Insight |
|-------|--------|
| Vertical scaling | Hits limits fast |
| Horizontal scaling | Adds coordination cost |
| Caching | Trades memory for latency |
| Sharding | Trades simplicity for scale |
| Async | Trades latency for throughput |

---

## 11. Power-Law Awareness

Many system behaviors follow:
P(x) ∝ x^(-α)

Implications:
- Heavy-tailed traffic
- Hot keys dominate load
- Averages mislead → think percentiles

---

## 12. Error Propagation

If:
Q = ∏ q_i

Then:
ΔQ / Q ≈ Σ (Δq_i / q_i)

Too many assumptions → exploding uncertainty

---

## 13. Interview Red Flags

- Ignoring peak load
- Confusing average vs worst case
- Forgetting replication/backups
- Ignoring network or serialization cost
- Over-precision too early

---

## 14. Interviewer-Friendly Language

Use phrases like:
- “Order-of-magnitude wise…”
- “Let me sanity-check this…”
- “I’ll assume a 10× peak factor…”
- “This gets us in the right ballpark…”

---

## 15. Golden Rule

> A fast, reasonable estimate now  
> beats a perfect calculation too late.

## Hash Functions Comparison Table

| Hash Function | Output Length | Speed | Collision Resistance | Security Status | Year Introduced |
| --- | --- | --- | --- | --- | --- |
| MD5 | 128 bits | Very Fast | Broken | Insecure | 1992 |
| SHA-1 | 160 bits | Fast | Broken | Deprecated | 1995 |
| SHA-256 (SHA-2) | 256 bits | Moderate | Strong | Secure | 2001 |
| SHA-512 (SHA-2) | 512 bits | Moderate | Strong | Secure | 2001 |
| SHA3-256 | 256 bits | Slower | Very Strong | Secure | 2015 |
| SHA3-512 | 512 bits | Slower | Very Strong | Secure | 2015 |
| BLAKE2b | 8-512 bits | Fast | Strong | Secure | 2012 |
| BLAKE3 | 256 bits (extendable) | Very Fast | Strong | Secure | 2020 |
| bcrypt | 184 bits | Deliberately Slow | Strong | Secure for passwords | 1999 |
| Argon2 | Configurable | Deliberately Slow | Strong | Secure for passwords | 2015 |
| CRC32 | 32 bits | Extremely Fast | Very Weak | Not cryptographically secure | 1975 |
| MurmurHash | 32/64/128 bits | Extremely Fast | Weak | Not cryptographically secure | 2008 |
## Detailed Breakdown
### MD5 (Message Digest Algorithm 5)
- **Length**: 128 bits (16 bytes)
- **Character Output**: 32 hexadecimal characters
- **Speed**: Very fast
- **Collision Risk**: Very high (practically broken)
- **Benefits**:
    - Fast computation
    - Still useful for non-security checksums

- **Negatives**:
    - Completely broken for security purposes
    - Demonstrated collision attacks
    - Should not be used for any security-related purpose

### SHA-1 (Secure Hash Algorithm 1)
- **Length**: 160 bits (20 bytes)
- **Character Output**: 40 hexadecimal characters
- **Speed**: Fast
- **Collision Risk**: High (broken)
- **Benefits**:
    - Faster than newer SHA variants
    - Better than MD5

- **Negatives**:
    - Collision attacks demonstrated in 2017
    - Deprecated for security use
    - Not suitable for digital signatures or certificates

### SHA-2 Family
#### SHA-256
- **Length**: 256 bits (32 bytes)
- **Character Output**: 64 hexadecimal characters
- **Speed**: Moderate
- **Collision Risk**: Low
- **Benefits**:
    - Widely supported
    - Hardware acceleration on modern CPUs
    - No practical attacks known

- **Negatives**:
    - Slower than SHA-1
    - Same fundamental design as SHA-1

#### SHA-512
- **Length**: 512 bits (64 bytes)
- **Character Output**: 128 hexadecimal characters
- **Speed**: Moderate (can be faster than SHA-256 on 64-bit systems)
- **Collision Risk**: Very low
- **Benefits**:
    - Very strong security margin
    - Better performance on 64-bit architectures

- **Negatives**:
    - Larger output size may be overkill for some applications

### SHA-3 Family
#### SHA3-256
- **Length**: 256 bits (32 bytes)
- **Character Output**: 64 hexadecimal characters
- **Speed**: Slower than SHA-2
- **Collision Risk**: Very low
- **Benefits**:
    - Different design than SHA-2 (sponge construction)
    - Resistance against length extension attacks
    - NIST standardized

- **Negatives**:
    - Generally slower than SHA-2
    - Less hardware acceleration support

#### SHA3-512
- **Length**: 512 bits (64 bytes)
- **Character Output**: 128 hexadecimal characters
- **Speed**: Slower than SHA-2
- **Collision Risk**: Extremely low
- **Benefits**:
    - Maximum security in SHA-3 family
    - Resistant to quantum attacks (as much as currently possible)

- **Negatives**:
    - Slower than SHA-2
    - Larger output size

### BLAKE2
- **Length**: Configurable (8-512 bits)
- **Speed**: Very fast (often faster than MD5)
- **Collision Risk**: Low
- **Benefits**:
    - High speed on modern CPUs
    - Can be faster than SHA-1 with better security
    - Resistant to length extension attacks

- **Negatives**:
    - Less widely adopted than SHA-2/SHA-3

### BLAKE3
- **Length**: 256 bits default, extendable to arbitrary length
- **Speed**: Extremely fast
- **Collision Risk**: Low
- **Benefits**:
    - One of the fastest cryptographic hash functions
    - Parallelizable (extremely fast on multi-core systems)
    - Built-in keyed hashing mode
    - Resistant to length extension attacks

- **Negatives**:
    - Relatively new (2020)
    - Less established than SHA-2/SHA-3

### Password-Specific Hash Functions
#### bcrypt
- **Length**: 184 bits (23 bytes)
- **Character Output**: ~60 characters (includes algorithm, cost, salt)
- **Speed**: Deliberately slow (configurable)
- **Collision Risk**: Low for password use case
- **Benefits**:
    - Designed for password hashing
    - Includes salt automatically
    - Configurable work factor

- **Negatives**:
    - Not suitable for general hashing purposes
    - Memory-efficient (can be vulnerable to GPU/ASIC attacks)

#### Argon2 (winner of Password Hashing Competition)
- **Length**: Configurable
- **Speed**: Deliberately slow and configurable
- **Collision Risk**: Low for password use case
- **Benefits**:
    - Modern password hashing algorithm
    - Configurable memory, time, and parallelism cost
    - Resistant to GPU/ASIC attacks

- **Negatives**:
    - More complex to implement correctly
    - Not suitable for general hashing purposes

### Non-Cryptographic Hash Functions
#### CRC32 (Cyclic Redundancy Check)
- **Length**: 32 bits (4 bytes)
- **Character Output**: 8 hexadecimal characters
- **Speed**: Extremely fast
- **Collision Risk**: Very high
- **Benefits**:
    - Very fast computation
    - Good for error detection

- **Negatives**:
    - Not secure against intentional collisions
    - Only useful for data integrity, not security

#### MurmurHash
- **Length**: 32, 64, or 128 bits
- **Speed**: Extremely fast
- **Collision Risk**: High
- **Benefits**:
    - Very fast non-cryptographic hash
    - Good distribution properties
    - Useful for hash tables and bloom filters

- **Negatives**:
    - Not secure against intentional collisions
    - Should not be used for security purposes

## Choosing the Right Hash Function
1. **For general security applications**: SHA-256 or SHA-512 (SHA-2)
2. **For maximum security**: SHA3-256 or SHA3-512
3. **For password storage**: Argon2 or bcrypt
4. **For performance-critical security applications**: BLAKE2 or BLAKE3
5. **For checksums/data integrity**: SHA-256 or BLAKE3
6. **For non-cryptographic use (hash tables, etc.)**: MurmurHash or xxHash

## Additional Considerations
- Always use the latest version of a hash function when possible
- For password hashing, always use specialized password hashing algorithms with salting
- Consider the specific security requirements of your application when choosing a hash function
- MD5 and SHA-1 should not be used for any new security-related applications
