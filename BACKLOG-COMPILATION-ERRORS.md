# Pre-existing Compilation Errors Catalog

**Date Identified:** 2026-05-03  
**Build Command:** `cd backend && ./mvnw clean compile -DskipTests`  
**Root Cause:** Missing domain classes, DTOs, and package structures that are out of scope for the compliance fix task.

---

## Error Categories

### Category 1: Missing Payment Module Classes (HIGH)

**Root Cause:** Payment domain classes (`Payment`, `PaymentStatus`, `PaymentMethod`, `PaymentRepository`) are referenced but not found in expected packages.

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `payment/application/PaymentRepository.java` | cannot find symbol | `PaymentStatus` |
| `payment/application/Payment.java` | cannot find symbol | `PaymentMethod`, `PaymentStatus` |
| `payment/interfaces/PaymentController.java` | cannot find symbol | `PaymentService`, `PaymentMethod`, `PaymentStatus`, `Payment` |
| `payment/application/usecase/InitiatePaymentUseCase.java` | cannot find symbol | `Payment` |
| `payment/application/usecase/HandlePaymentCallbackUseCase.java` | cannot find symbol | `Payment` |
| `payment/application/PaymentCallbackLedger.java` | cannot find symbol | `PaymentMethod` |
| `payment/gateway/PaymentGateway.java` | cannot find symbol | `Payment` |
| `saga/application/OrderSagaContext.java` | cannot find symbol | `Payment` |
| `saga/application/ProcessPaymentStep.java` | cannot find symbol | `Payment`, `OrderSagaContext` |
| `saga/application/SagaPaymentService.java` | cannot find symbol | `Payment`, `PaymentRepository` |

**Required Action:** Create missing Payment domain classes in `payment/domain/`:
- `Payment.java` (domain model)
- `PaymentStatus.java` (enum)
- `PaymentMethod.java` (enum)

---

### Category 2: Missing Saga Framework Classes (HIGH)

**Root Cause:** Saga pattern classes (`SagaStep`, `OrderSagaContext`) are missing.

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `saga/application/OrderSagaService.java` | cannot find symbol | `SagaStep`, `OrderSagaContext` |
| `saga/application/CreateOrderStep.java` | cannot find symbol | `SagaStep`, `OrderSagaContext` |
| `saga/application/UpdateInventoryStep.java` | cannot find symbol | `SagaStep`, `OrderSagaContext` |
| `saga/application/SagaOrchestrator.java` | cannot find symbol | `SagaStep` |

**Required Action:** Create Saga framework in `saga/domain/`:
- `SagaStep.java` (interface)
- `OrderSagaContext.java` (context class)

---

### Category 3: Missing Common Domain Classes (HIGH)

**Root Cause:** Common exception and response classes are missing from `common/domain/`.

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `common/infrastructure/GlobalExceptionHandler.java` | cannot find symbol | `ApiResponse`, `ResourceNotFoundException`, `BusinessException`, `AiServiceUnavailableException` |
| `product/application/ProductInventoryService.java` | cannot find symbol | `ResourceNotFoundException` |
| `cart/application/CartService.java` | cannot find symbol | `ResourceNotFoundException`, `CartDto`, `AddToCartRequest`, `UpdateCartRequest` |
| `coupon/application/CouponService.java` | cannot find symbol | `ResourceNotFoundException` |
| `wishlist/application/WishlistService.java` | cannot find symbol | `ResourceNotFoundException` |
| `ai/application/chatMessageRepository.java` | cannot find symbol | `ChatMessage` |
| `ai/domain/AiServiceUnavailableException.java` | (exists but not found) | `AiServiceUnavailableException` |

**Required Action:** Create missing common classes in `common/domain/`:
- `ResourceNotFoundException.java`
- `BusinessException.java`
- `ApiResponse.java` (DTO)

---

### Category 4: Missing DTO Classes (MEDIUM)

**Root Cause:** DTO classes referenced in controllers and services are missing.

| Module | Missing DTOs |
|--------|--------------|
| `cart` | `CartDto`, `CartItemDto`, `AddToCartRequest`, `UpdateCartRequest`, `MergeCartRequest`, `ApplyCouponRequest` |
| `payment` | `PaymentDto` (inner class in PaymentController) |
| `ai` | `AiChatRequest`, `AiChatResponse` |

**Required Action:** Create DTOs in respective `interfaces/dto/` or `application/dto/` directories.

---

### Category 5: Missing Package Directories (MEDIUM)

**Root Cause:** Package directories referenced in imports don't exist.

| File | Error |
|------|-------|
| `common/infrastructure/config/WebMvcConfig.java` | `package com.skaly.fashion_backend.interceptor does not exist` |
| `cart/application/CartService.java` | `package com.skaly.fashion_backend.cart.api.dto does not exist` |

**Required Action:** Create missing packages:
- `common/infrastructure/interceptor/` (for `CacheControlInterceptor`, `CorrelationIdInterceptor`)
- `cart/interfaces/dto/` or `cart/application/dto/`

---

### Category 6: Missing User Module Classes (MEDIUM)

**Root Cause:** User domain classes are missing.

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `user/infrastructure/persistence/jpa/BodyProfileRepository.java` | cannot find symbol | `BodyProfile` |
| `common/infrastructure/security/OAuth2AuthenticationSuccessHandler.java` | cannot find symbol | `CustomOAuth2User` |

**Required Action:** Create missing User domain classes:
- `BodyProfile.java` in `user/domain/model/`
- `CustomOAuth2User.java` in `user/domain/model/` or `user/application/`

---

### Category 7: Java Preview API Issues (LOW)

**Root Cause:** `StructuredTaskScope` requires `--enable-preview` flag (Java 21 preview feature).

| File | Error |
|------|-------|
| `cart/infrastructure/persistence/jpa/CartPersistenceAdapter.java` | `java.util.concurrent.StructuredTaskScope is a preview API and is disabled by default` |

**Required Action:** Update `pom.xml` maven-compiler-plugin configuration to enable preview:
```xml
<configuration>
  <release>21</release>
  <compilerArgs>--enable-preview</compilerArgs>
</configuration>
```

---

### Category 8: Missing Spring AI Classes (LOW)

**Root Cause:** Spring AI `EmbeddingModel` class not found (version mismatch or missing dependency).

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `ai/infrastructure/AiModelPortConfig.java` | cannot find symbol | `EmbeddingModel` |
| `ai/infrastructure/SpringAiChatClientAdapter.java` | (uses EmbeddingModel) | `EmbeddingModel` |

**Required Action:** Verify Spring AI version in `pom.xml` and ensure correct import statement. Check if class is in `org.springframework.ai.embedding.EmbeddingModel` or different package.

---

### Category 9: Product Domain UUID Import Issue (LOW)

**Root Cause:** `UUID` class not imported in `Product.java`.

| File | Error | Missing Class/Symbol |
|------|-------|-------------------|
| `product/domain/model/Product.java` | cannot find symbol | `UUID` (lines 9, 48, 85, 86, 184, 211) |

**Required Action:** Add import statement to `Product.java`:
```java
import java.util.UUID;
```

---

## Summary of Missing Classes by Module

### payment/ (6 missing classes)
- `Payment.java` (domain model)
- `PaymentStatus.java` (enum)
- `PaymentMethod.java` (enum)
- `PaymentRepository.java` (may need fixing)
- `PaymentService.java`
- `PaymentDto.java`

### saga/ (3 missing classes)
- `SagaStep.java`
- `OrderSagaContext.java`
- `SagaOrchestrator.java` (may need fixing)

### common/ (4 missing classes)
- `ResourceNotFoundException.java`
- `BusinessException.java`
- `ApiResponse.java`
- `CacheControlInterceptor.java`
- `CorrelationIdInterceptor.java`

### cart/ (6 missing DTOs)
- `CartDto.java`
- `CartItemDto.java`
- `AddToCartRequest.java`
- `UpdateCartRequest.java`
- `MergeCartRequest.java`
- `ApplyCouponRequest.java`

### user/ (2 missing classes)
- `BodyProfile.java`
- `CustomOAuth2User.java`

### ai/ (1 missing import)
- Fix `EmbeddingModel` import

---

## Recommended Remediation Priority

| Priority | Category | Estimated Effort |
|----------|----------|-------------------|
| P0 | Category 3: Common domain classes | 2 hours |
| P0 | Category 1: Payment module | 3 hours |
| P1 | Category 2: Saga framework | 4 hours |
| P1 | Category 4: DTO classes | 2 hours |
| P2 | Category 6: User module | 1 hour |
| P2 | Category 5: Package directories | 30 mins |
| P3 | Category 7: Preview API flag | 15 mins |
| P3 | Category 8: Spring AI imports | 30 mins |
| P3 | Category 9: Product UUID import | 5 mins |

**Total estimated effort:** ~13 hours

---

## Next Steps for Remediation Stream

1. Create a new task/branch: `fix/compilation-errors`
2. Start with P0 items (common domain, payment module)
3. Verify each module compiles independently: `./mvnw compile -pl <module>`
4. Run full compile after each priority level
5. Run tests: `./mvnw test`
6. Merge back to main when all errors resolved

---

**Note:** None of these errors were caused by the compliance fix task. They are pre-existing issues in the codebase.
