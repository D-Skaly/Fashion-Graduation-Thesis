# DO & DON'T

## ✅ DO

* Use ReentrantLock instead of synchronized
* Write Domain unit tests first
* Use StructuredTaskScope.Joiner
* Keep modules isolated

---

## ❌ DON'T

* Put AI logic in Controllers
* Use JPA Entities in Domain
* Call OpenAI SDK directly
* Break module boundaries

---

## Engineering Mindset

* Design for scale from day-1
* Optimize for AI-agent readability
* Keep code deterministic & testable