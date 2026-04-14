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

### Product Model Enhancement ✅ **HOÀN THÀNH**
- [x] Update `Product.java` - Add fields:
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

### Product Image ✅ **HOÀN THÀNH**
- [x] `ProductImage.java` - Entity
  - `product_id`
  - `url`
  - `alt`
  - `sortOrder`
  - `isPrimary`
- [x] `ProductImageRepository.java`
- [x] `ProductImageService.java`
- [ ] Update `ProductResponse` - Include images (Phase 2.2)

### Product Review ✅ **HOÀN THÀNH**
- [x] `Review.java` - Entity
  - `user_id`
  - `product_id`
  - `rating` (1-5)
  - `comment`
  - `images[]`
  - `isVerifiedPurchase`
  - `isHelpful` count
- [x] `ReviewRepository.java`
- [x] `ReviewController.java` - CRUD + helpful vote
- [x] `ReviewService.java`
- [x] Update `Product` - Calculate average rating (implemented in ReviewService)

### Product Search & Filter ✅ **HOÀN THÀNH**
- [x] Update `ProductController`:
  - `GET /products/search?keyword=` - Search by keyword
  - `GET /products?category=&minPrice=&maxPrice=&sort=` - Filter products
  - `GET /products/featured` - Featured products
  - `GET /products/new-arrivals` - New arrivals
  - `GET /products/brand/{brand}` - Products by brand
  - `GET /products/tag/{tag}` - Products by tag
  - `GET /products/filters/brands` - Get all brands
  - `GET /products/filters/tags` - Get all tags
  - `POST /products/{id}/view` - Increment view count

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

### Order Workflow ✅ **HOÀN THÀNH**
- [x] Update `Order.java` - Add fields:
  - `orderNumber` (formatted: ORD-2024-001)
  - `subTotal`
  - `taxAmount`
  - `shippingCost`
  - `discountAmount`
  - `discountCode`
  - `totalAmount`
  - `notes`
  - `cancelledAt`, `cancelledReason`
- [x] `OrderStatusHistory.java` - Audit log
- [x] `OrderNote.java` - Internal notes
- [x] `Shipping.java` - Shipping info
- [x] `OrderController.java` - Complete endpoints:
  - `POST /orders` - Create from cart
  - `GET /orders` - List user orders
  - `GET /orders/{id}` - Order detail
  - `PUT /orders/{id}/cancel` - Cancel order
  - `GET /orders/{id}/status-history` - Order status history
  - `GET /orders/{id}/tracking` - Tracking info

### Payment Integration ✅ **HOÀN THÀNH**
- [x] `PaymentGateway.java` - Interface
- [ ] `StripeService.java` - Stripe implementation (future)
- [x] `VNPayService.java` - VNPay implementation
- [x] `MomoService.java` - Momo implementation
- [x] `PaymentController.java` - Webhook handlers (VNPay/Momo)
- [ ] `PaymentCallback.tsx` - Frontend callback page (Phase 3.2)

### Frontend - Checkout (Phase 3.2)
- [ ] `CheckoutPage.tsx` - Main checkout
- [ ] `ShippingForm.tsx` - Address input
- [ ] `PaymentMethodSelector.tsx`
- [ ] `OrderSummary.tsx`
- [ ] `CheckoutProvider.tsx` - Context

### Frontend - Order Tracking (Phase 3.2)
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
