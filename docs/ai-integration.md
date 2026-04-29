# AI Integration (Spring AI 2.0)

## ChatClient Pattern (MANDATORY)

DO NOT use ChatModel directly.

```java
return chatClientBuilder
  .defaultAdvisors(
        new MessageChatMemoryAdvisor(chatMemory),
        new RetrievalAugmentationAdvisor(retriever)
    )
  .build();
```

---

## RAG (Modular)

* Use RetrievalAugmentationAdvisor
* Plug multiple retrievers if needed

---

## MCP (Model Context Protocol)

* Define tools using @Tool
* Use spring-ai-starter-mcp-client

---

## AI Design Rules

* AI logic belongs in Application layer
* NEVER inside Controllers
* Always use abstraction layer