# Concurrency Model (Java 21)

## Virtual Threads

* Use for ALL I/O operations
* Especially LLM + Vector DB calls

---

## Structured Concurrency

Use StructuredTaskScope:

* Parallel retrieval
* Multi-model querying
* Aggregation workflows

---

## Scoped Values

* Replace ThreadLocal
* Used for user context propagation

---

## Best Practices

* Avoid blocking operations
* Design async-first flows
* Combine with streaming (SSE)