# Observability & Metrics Guide (Spring Boot + Micrometer + Prometheus + RabbitMQ)

## 0) Why this guide

This document helps you:

* pick the **right metrics**,
* understand **why they matter**,
* know **what to troubleshoot** when a signal goes bad,
* and **implement** them efficiently in Spring Boot with minimal resource waste.

> Scope: HTTP (server & client), RabbitMQ (producer/consumer + exporter), JVM/GC, DB (HikariCP), container/K8s, business KPIs, **unified load** (HTTP + MQ), SLOs/SLIs/SLAs, alerting, and cost control.

---

## 1) How metrics work (quick primer)

* **Counter**: monotonically increasing (e.g., requests, errors). Use with `rate()` in PromQL.
* **Gauge**: point-in-time value (e.g., heap used, queue depth).
* **Histogram/Timer**: buckets of durations/sizes, used to compute **percentiles** (p50/p95/p99) via `histogram_quantile`.

> Percentiles reveal the **tail** (slowest requests). Enable histograms **only** where they matter to control time series cost.

---

## 2) SLO / SLI / SLA & ROI (optimize what matters)

* **SLI**: the thing you measure (e.g., p95 latency, error rate).
* **SLO**: the target for that SLI over a window (e.g., p95 < 250 ms over 30 days).
* **SLA**: contractual/business agreement built on SLOs.
* **Error Budget**: 1 − SLO (how much “badness” you can spend).
* **ROI**: invest engineering effort where (impact on SLO × traffic volume × cost of failure) ÷ cost to fix is highest.

---

## 3) Metrics Catalog

### A) HTTP – Server (Spring MVC/WebFlux)

**What it is**
How fast and how reliably your service responds to incoming HTTP requests.

**Why it matters**
Directly reflects user experience and SLO conformance.

**Troubleshooting with it**

* **RPS up** + latency stable → healthy headroom.
* **RPS up** + latency up → CPU-bound, I/O wait, or dependency slow.
* **5xx up** → exceptions, timeouts, thread-pool exhaustion, DB errors.
* High **p99** only → tail latency from GC, locks, cold cache, rare dependency slowdowns.

**Key metrics (Prometheus names)**

* Throughput: `http_server_requests_seconds_count` (use `rate()` for RPS)
* Latency: `http_server_requests_seconds_bucket|sum|max` (for p50/p95/p99)
* Errors: filter by `status` (e.g., 5xx), `outcome`

**Implementation (Spring Boot)**

* Add Actuator + Micrometer Prometheus registry.
* Enable histograms/percentiles **only** for server HTTP:

  ```
  management.metrics.distribution.percentiles-histogram.http.server.requests=true
  management.metrics.distribution.percentiles.http.server.requests=0.5,0.95,0.99
  ```
* Expose `/actuator/prometheus`.

**Useful PromQL**

* RPS: `sum(rate(http_server_requests_seconds_count[5m]))`
* Error %: `sum(rate(...{status=~"5.."}[5m])) / sum(rate(...[5m]))`
* p95: `histogram_quantile(0.95, sum by (le, uri)(rate(http_server_requests_seconds_bucket[5m])))`

---

### B) HTTP – Client (WebClient/RestTemplate)

**What it is**
Performance & reliability of outbound calls to dependencies.

**Why it matters**
Dependencies are often the bottleneck and root cause of tail latency.

**Troubleshooting**

* Latency spikes to specific host → add caching, backoff, circuit breakers.
* Error rates up → partner/API instability, auth/quotas, network/DNS/SSL issues.

**Key metrics**

* `http_client_requests_seconds_count|bucket|sum`
* Labels: `uri`, `status`, `target` (if present)

**Implementation**

* Client metrics come from Micrometer automatically when using Spring instrumentation.
* Enable histogram for client timers **only if needed**:

  ```
  management.metrics.distribution.percentiles-histogram.http.client.requests=true
  ```

---

### C) RabbitMQ – Producer/Consumer (App-level)

**What it is**
Time to publish (producer) and time to process (consumer) messages, plus ack/nack counters.

**Why it matters**
Asynchronous work is part of user-visible flow (eventually). Slow consumers create backlogs; slow producers indicate broker/app issues.

**Troubleshooting**

* Consumer processing time ↑ + queue depth ↑ → not enough consumers/CPU, slow handler, downstream DB/cache issues.
* Nacks/requeues ↑ → flaky logic, transient errors.

**Key metrics (custom timers/counters in your app)**

* Producer publish latency: `rabbitmq_producer_publish_seconds_{count,sum,bucket}`
* Consumer processing time: `rabbitmq_consumer_process_seconds_{count,sum,bucket}`
* Acknowledgements: `rabbitmq_consumer_ack_total`, `..._nack_total`

**Implementation**

* Wrap publish and consume handlers with Micrometer `Timer` + `Counter`.
* Enable histograms for these timers only:

  ```
  management.metrics.distribution.percentiles-histogram.rabbitmq_producer_publish_seconds=true
  management.metrics.distribution.percentiles-histogram.rabbitmq_consumer_process_seconds=true
  ```

**Useful PromQL**

* Consumer p95:
  `histogram_quantile(0.95, sum by (le)(rate(rabbitmq_consumer_process_seconds_bucket[5m])))`

---

### D) RabbitMQ – Broker Exporter (Infra-level)

**What it is**
Broker-provided metrics about queues, consumers, backlog, throughput.

**Why it matters**
Shows whether the **system** can keep up, independently from your application code.

**Troubleshooting**

* `messages_ready` ↑ continuously → consumers can’t keep up (scale out or speed up).
* `unacknowledged` ↑ → consumers slow/crashing or network issues during processing.

**Key metrics**

* Backlog/lag: `rabbitmq_queue_messages_ready`, `rabbitmq_queue_messages_unacknowledged`
* Throughput: `rabbitmq_queue_messages_published_total`, `rabbitmq_queue_messages_delivered_total`
* Consumers: `rabbitmq_consumers`

**Implementation**

* Enable RabbitMQ management & Prometheus plugin; have Prometheus scrape broker `/metrics`.

---

### E) Database – HikariCP / JDBC

**What it is**
Connection pool saturation & timeouts; indirect signal for DB health and query performance.

**Why it matters**
If the pool is pegged or pending grows, your service will queue requests and degrade.

**Troubleshooting**

* `pending > 0` or timeouts → increase pool cautiously, optimize queries, add indexes, evaluate DB capacity.
* `active ≈ max` persistently → DB bottleneck or too much app parallelism.

**Key metrics**

* `hikaricp_connections{state="active|idle|pending"}`
* `hikaricp_connections_max`
* `hikaricp_connections_timeout_total`

**Implementation**

* Include Hikari (default for Spring Boot JDBC).
* No extra setup beyond Actuator/Micrometer; metrics are auto-bound.

---

### F) JVM & Process

**What it is**
Heap usage, GC pauses, thread counts, process CPU.

**Why it matters**
GC & memory pressure are classic root causes of tail latency and OOMs.

**Troubleshooting**

* GC pauses up + p99 latency up → reduce allocations, tune GC, review caches.
* Heap steadily rising → memory leak (retain cycle, unbounded caches).

**Key metrics**

* Memory: `jvm_memory_used_bytes{area="heap"}`, `jvm_memory_max_bytes`
* GC: `jvm_gc_pause_seconds_count|sum`
* Threads: `jvm_threads_live_threads`, `..._daemon_threads`
* CPU: `process_cpu_usage` (plus container CPU below)

**Implementation**

* Provided by Micrometer JVM binder automatically.

---

### G) Container / Kubernetes (if running in K8s)

**What it is**
Real container usage and limits; essential for autoscaling and capacity planning.

**Why it matters**
CPU throttling or memory pressure makes latency & errors spike even if app-level metrics look fine.

**Troubleshooting**

* Throttling ratio ↑ → raise CPU limits or optimize CPU-heavy paths.
* Working set ↑ → leaks or oversized caches.

**Key metrics (cAdvisor / kube-state-metrics)**

* CPU usage: `container_cpu_usage_seconds_total` (use `rate()`)
* Throttling: `container_cpu_cfs_throttled_seconds_total` / `..._periods_total`
* Memory: `container_memory_working_set_bytes`
* Limits/requests: `kube_pod_container_resource_limits{resource="cpu|memory"}`

---

### H) Business KPIs (custom)

**What it is**
Counters & timers that reflect **business** events (e.g., orders created).

**Why it matters**
Great for ROI-driven prioritization and alerting aligned with user impact.

**Examples**

* `orders_created_total`, `orders_api_received_total`
* Processing time per business flow (timer)

**Implementation**

* Use Micrometer `Counter`/`Timer` in the relevant application services.

---

### I) Unified Service Load (HTTP + MQ)

**What it is**
A combined signal representing total work processed per unit time.

**Why it matters**
Your service may be busy either with HTTP or with messages. Unified load drives better autoscaling and capacity planning.

**PromQL idea**

```
sum(rate(http_server_requests_seconds_count[1m]))
+
sum(rate(rabbitmq_queue_messages_delivered_total[1m]))
```

**Use cases**

* HPA custom metric (via Prometheus Adapter) so you scale on **real** workload.
* Performance testing: correlate unified load with CPU %, p95 latency to find **capacity per pod**.

---

## 4) Dashboards & Alerting (what to visualize/alert)

**Dashboards**

* **SLO panel**: error rate, p95/p99 latency, burn rate.
* **RED** (Rate, Errors, Duration): per service & per endpoint.
* **Saturation**: CPU%, throttling, memory working set, HikariCP active/pending.
* **RabbitMQ**: queue depth, delivered/sec, consumers, consumer processing p95.
* **Unified Load**: HTTP RPS + MQ msgs/sec.
* **Dependencies**: HTTP client p95 per target host.

**Alerting tips**

* **Error budget burn rate** (multi-window, e.g., 5m & 1h) to detect acute & chronic issues.
* **Queue depth growth** over time (sustained positive slope) and **consumer lag**.
* **p95 latency** breach for key endpoints/consumers.
* **Hikari pending** > 0 for > N minutes.
* **CPU throttling ratio** > X% sustained.

---

## 5) Implementation Checklist (Spring Boot)

**Dependencies**

* `spring-boot-starter-actuator`
* `micrometer-registry-prometheus`
* `spring-boot-starter-web` (and WebFlux/MVC)
* `spring-boot-starter-amqp` (RabbitMQ)
* `spring-boot-starter-data-jdbc` (Hikari) as needed

**Expose metrics**

* `management.endpoints.web.exposure.include=prometheus,health,info`
* `/actuator/prometheus` reachable by Prometheus

**Histograms & percentiles (selective!)**

```
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.distribution.percentiles.http.server.requests=0.5,0.95,0.99
# Add only where needed:
management.metrics.distribution.percentiles-histogram.rabbitmq_consumer_process_seconds=true
management.metrics.distribution.percentiles-histogram.rabbitmq_producer_publish_seconds=true
```

**Common tags (stable, low-cardinality)**

```
management.metrics.tags.application=${spring.application.name}
management.metrics.tags.environment=${ENV:local}
```

**Cardinality & cost control (strongly recommended)**

* Disable entire families you don’t need:

  ```
  management.metrics.enable.tomcat=false
  management.metrics.enable.logback=false
  management.metrics.enable.jvm=true
  ```
* Add a `MeterFilter` to **deny** noisy meters or **strip** labels that explode series (e.g., raw `uri`s).
* Avoid unbounded tags (`userId`, `query`, `exception` with stack elements, etc.).
* Enable percentiles/histograms **only** for the few meters you truly analyze.

**Prometheus**

* Scrape your apps’ `/actuator/prometheus` and RabbitMQ’s `/metrics`.
* (Optional) Server-side **relabel/drop** rules to discard unwanted series.

**Grafana**

* Import/create dashboards for RED, JVM, Hikari, RabbitMQ, Unified Load.

---

## 6) Capacity Planning & Autoscaling

**Find capacity per pod**

1. Generate load; record **unified load**, **CPU%**, and **p95 latency**.
2. Pick the sustainable point where p95 meets the SLO and CPU ≈ 60–70% (no throttling).
3. Capacity per pod = sustained work units/sec at that point.

**Size replicas**

```
replicas = ceil( peak_workload / capacity_per_pod * safety_factor )
# safety_factor typically 1.2–1.5
```

**HPA**

* Start with CPU target 60–70%.
* Better: **external metric** (RPS/pod or msgs/sec/pod, or unified load) via Prometheus Adapter.
* Respect downstream constraints (DB max connections, partner rate limits).

---

## 7) Troubleshooting Playbook (pattern → likely cause → action)

* **Latency ↑ & CPU low** → I/O wait (DB, external API) → tune timeouts, add caching, async I/O, fix slow queries.
* **Latency ↑ & CPU high** → CPU-bound or GC → optimize code/hot paths, reduce allocations, raise CPU limit/replicas.
* **p99 bad only** → tail causes (GC pauses, lock contention, cold cache) → tune GC, reduce locks, pre-warm cache.
* **5xx spike** → regressions/timeouts → check exceptions by endpoint, roll back/feature flag.
* **Hikari pending > 0** → pool saturated → increase pool cautiously, add indexes/optimize queries, scale DB.
* **Queue depth ↑** → consumers behind → scale consumers, speed handler, add DLQ/retry/backoff.
* **Throttling ↑** → CPU limit too low or code hot path too heavy → increase limit or optimize.

---

## 8) Appendix: Minimal “only what we need” strategy

1. **Keep**: `http.server.requests`, `http.client.requests` (only if you have dependencies), `hikaricp_*` (if DB), `jvm_*`, selected RabbitMQ app timers & exporter metrics, a handful of **business** counters.
2. **Disable**: families you don’t use (`tomcat`, `logback`, etc.).
3. **Histograms**: only on `http.server.requests` + `rabbitmq_consumer_process_seconds` (+ producer if needed).
4. **Tags**: `application`, `environment`, maybe `version`. Avoid anything unbounded.
5. **Prometheus**: optionally drop series you never graph.

---

