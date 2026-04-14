# Fashion E-Commerce - Upgrade Checklist

## Phase 1: Core Infrastructure

### 1. Production Config ✅
- [x] `application-prod.yaml` - Production configuration
- [x] `.env.example` - Environment template
- [x] `application.yaml` - JWT secret as environment variable
- [x] `CorsProperties.java` - Dynamic CORS configuration

### 2. Docker Setup ✅
- [x] `backend/Dockerfile` - Multi-stage build
- [x] `backend/.dockerignore`
- [x] `frontend/Dockerfile` - Standalone output
- [x] `frontend/.dockerignore`
- [x] `docker-compose.prod.yml` - Production stack
- [x] `nginx/nginx.conf` - Reverse proxy
- [x] `.env.prod.template` - Production env template
- [x] `next.config.ts` - Standalone output + security headers
- [x] `DEPLOY.md` - Deployment guide

### 3. Refresh Token + Security ✅ **HOÀN THÀNH**
- [x] `RefreshToken.java` - Entity
- [x] `RefreshTokenRepository.java` - Repository
- [x] `RefreshTokenService.java` - Service (with rotation & device limiting)
- [x] `TokenResponse.java` - DTO (access + refresh token)
- [x] `AuthenticationResponse.java` - Updated with full user info
- [x] `RefreshTokenRequest.java` - Request DTO
- [x] `LogoutRequest.java` - Request DTO
- [x] Update `AuthenticationController` - Add refresh, logout, logout-all endpoints
- [x] Update `AuthenticationService` - Generate refresh token, logout functionality
- [x] Update `JwtAuthenticationFilter` - Check token blacklist
- [x] `TokenBlacklistService.java` - Redis-based blacklist
- [x] `RedisConfig.java` - RedisTemplate configuration
- [x] Update `User.java` - Add avatarUrl, phone, lastLoginAt
- [x] Update `JwtUtils.java` - Add getUserFromToken, extractUserId, extractRole
- [x] Update `application-prod.yaml` - Refresh token config
- [x] Update `.env.prod.template` - Refresh token env vars
- [ ] `SecurityConfig` - Add rate limiting (next iteration)
- [ ] `pom.xml` - Add Redis dependency ✅ Done

### 4. Address, Payment Entities ✅ **HOÀN THÀNH**
- [x] `Address.java` - User addresses (SHIPPING/BILLING/BOTH types)
- [x] `AddressRepository.java` - With custom queries for default address
- [x] `AddressController.java` - Full CRUD endpoints + set default
- [x] `Payment.java` - Payment records with order relationship
- [x] `PaymentRepository.java` - With status queries and revenue calculation
- [x] `PaymentService.java` - Payment processing (create, complete, fail)
- [x] `PaymentController.java` - User & Admin endpoints
- [x] `PaymentStatus.java` - Enum (PENDING, COMPLETED, FAILED, etc.)
- [x] `PaymentMethod.java` - Enum (COD, VNPAY, MOMO, etc.)

### 5. Image Upload (S3/MinIO) ✅ **HOÀN THÀNH**
- [x] `pom.xml` - MinIO dependency
- [x] `config/StorageConfig.java` - MinIO client configuration
- [x] `storage/StorageService.java` - Upload, download, delete, presigned URL
- [x] `storage/ImageController.java` - Single/multiple upload, get URL, delete
- [x] `application-prod.yaml` - Storage config
- [x] `.env.prod.template` - MinIO env vars

### 6. Dev Profile Setup ✅ **HOÀN THÀNH**
- [x] `application-dev.yaml` - Dev configuration (debug logging, SQL tracing)
- [x] Update `application.yaml` - Default profile = dev
- [x] `.env.dev.template` - Development environment template
- [x] `docs/development/dev_setup.md` - Development setup guide

---

## Phase 2: Product & Catalog

### Product Model Enhancement
- [ ] Update `Product.java` - Add fields:
  - `sku` (unique)
  - `slug` (unique, URL-friendly)
  - `isActive`
  - `isFeatured`
  - `tags` (String[])
  - `brand`
  - `material`
  - `careInstructions`
  - `weight`
  - `dimensions`
  - `viewCount`
  - `soldCount`
  - `ratingAvg`
  - `metaTitle`
  - `metaDescription`

### Product Image
- [ ] `ProductImage.java` - Entity
  - `product_id`
  - `url`
  - `alt`
  - `sortOrder`
  - `isPrimary`
- [ ] `ProductImageRepository.java`
- [ ] `ProductImageService.java`
- [ ] Update `ProductResponse` - Include images

### Product Review
- [ ] `Review.java` - Entity
  - `user_id`
  - `product_id`
  - `rating` (1-5)
  - `comment`
  - `images[]`
  - `isVerifiedPurchase`
  - `isHelpful` count
- [ ] `ReviewRepository.java`
- [ ] `ReviewController.java` - CRUD + helpful vote
- [ ] `ReviewService.java`
- [ ] Update `Product` - Calculate average rating

### Product Search & Filter
- [ ] Update `ProductController`:
  - `/products/search?keyword=`
  - `/products?category=&minPrice=&maxPrice=&sort=`
  - `/products/featured`
  - `/products/new-arrivals`

### Frontend - Product Detail
- [ ] `ImageGallery.tsx` - Zoom, thumbnails
- [ ] `SizeSelector.tsx` - With size guide
- [ ] `ColorSelector.tsx` - Color swatches
- [ ] `QuantitySelector.tsx`
- [ ] `ReviewList.tsx` - Reviews display
- [ ] `AddToCartButton.tsx` - With animation
- [ ] `ProductPage.tsx` - Complete product page

### Frontend - Product Listing
- [ ] `FilterSidebar.tsx` - Price, category, brand
- [ ] `SortDropdown.tsx`
- [ ] `ProductGrid.tsx`
- [ ] `Pagination.tsx`
- [ ] `ShopPage.tsx`

---

## Phase 3: Order & Checkout

### Order Workflow
- [ ] Update `Order.java` - Add fields:
  - `orderNumber` (formatted: ORD-2024-001)
  - `subTotal`
  - `taxAmount`
  - `shippingCost`
  - `discountAmount`
  - `discountCode`
  - `totalAmount`
  - `notes`
  - `cancelledAt`, `cancelledReason`
- [ ] `OrderStatusHistory.java` - Audit log
- [ ] `OrderNote.java` - Internal notes
- [ ] `Shipping.java` - Shipping info
- [ ] `OrderController.java` - Complete endpoints:
  - `POST /orders` - Create from cart
  - `GET /orders` - List user orders
  - `GET /orders/{id}` - Order detail
  - `PUT /orders/{id}/cancel` - Cancel order
  - `GET /orders/{id}/track` - Tracking info

### Payment Integration
- [ ] `PaymentGateway.java` - Interface
- [ ] `StripeService.java` - Stripe implementation
- [ ] `VNPayService.java` - VNPay implementation
- [ ] `MomoService.java` - Momo implementation
- [ ] `PaymentController.java` - Webhook handlers
- [ ] `PaymentCallback.tsx` - Frontend callback page

### Frontend - Checkout
- [ ] `CheckoutPage.tsx` - Main checkout
- [ ] `ShippingForm.tsx` - Address input
- [ ] `PaymentMethodSelector.tsx`
- [ ] `OrderSummary.tsx`
- [ ] `CheckoutProvider.tsx` - Context

### Frontend - Order Tracking
- [ ] `OrderListPage.tsx`
- [ ] `OrderDetailPage.tsx`
- [ ] `OrderStatusTimeline.tsx`
- [ ] `CancelOrderDialog.tsx`

---

## Phase 4: Admin Dashboard

### Admin Layout
- [ ] `AdminLayout.tsx`
- [ ] `AdminSidebar.tsx`
- [ ] `AdminHeader.tsx`
- [ ] `AdminRouteGuard.tsx`

### Dashboard
- [ ] `AdminDashboardPage.tsx`
- [ ] `StatsCards.tsx` - Orders, revenue, users
- [ ] `RevenueChart.tsx`
- [ ] `RecentOrdersTable.tsx`
- [ ] `PopularProducts.tsx`

### Product Management
- [ ] `AdminProductsPage.tsx` - Product list
- [ ] `AdminProductForm.tsx` - Create/Edit product
- [ ] `ImageUploader.tsx` - Multi-image upload
- [ ] `RichTextEditor.tsx` - Description editor
- [ ] `BulkActions.tsx`

### Order Management
- [ ] `AdminOrdersPage.tsx`
- [ ] `OrderFilters.tsx`
- [ ] `OrderStatusBadge.tsx`
- [ ] `UpdateStatusDialog.tsx`
- [ ] `PrintInvoiceButton.tsx`
- [ ] `AdminOrderDetailPage.tsx`

### User Management
- [ ] `AdminUsersPage.tsx`
- [ ] `UserTable.tsx`
- [ ] `BanUserDialog.tsx`
- [ ] `UserDetailPage.tsx`

### Coupon Management
- [ ] `AdminCouponsPage.tsx`
- [ ] `CouponForm.tsx`
- [ ] `CouponStats.tsx`

### Category Management
- [ ] `AdminCategoriesPage.tsx`
- [ ] `CategoryTree.tsx`
- [ ] `CategoryForm.tsx`

---

## Phase 5: Enhancements

### Wishlist
- [ ] `Wishlist.java` - Entity
- [ ] `WishlistRepository.java`
- [ ] `WishlistController.java`
- [ ] `WishlistPage.tsx`
- [ ] `WishlistButton.tsx`

### Reviews Enhancement
- [ ] `ReviewHelpfulVote.java` - Track helpful votes
- [ ] Update `ReviewController` - Helpful vote endpoint
- [ ] `ReviewForm.tsx` - Write review
- [ ] `ReviewImages.tsx` - Upload review images

### AI Chat Improvements
- [ ] `ChatSession.java` - Session management
- [ ] `ChatMessage.java` - Message history
- [ ] Update `FashionAssistantService` - Context-aware
- [ ] `AiProductRecommendation.tsx`
- [ ] `ChatFeedback.tsx` - Thumbs up/down

### Email Notifications
- [ ] `EmailConfig.java` - SMTP config
- [ ] `EmailService.java`
- [ ] `OrderConfirmationEmail.java`
- [ ] `ShippingNotificationEmail.java`
- [ ] `WelcomeEmail.java`
- [ ] `PasswordResetEmail.java`

### SEO Optimization
- [ ] `SEO.tsx` - Component
- [ ] `SitemapGenerator.java`
- [ ] `Robots.txt` endpoint
- [ ] `OpenGraph` meta tags
- [ ] `StructuredData.tsx`

### Performance
- [ ] Redis caching for products
- [ ] Image optimization (WebP, lazy loading)
- [ ] API response compression
- [ ] Database indexing

---

## Notes

- Priority: 🔴 Cao | 🟡 Trung bình | 🟢 Thấp
- Check items as they are completed
- Update this file regularly
