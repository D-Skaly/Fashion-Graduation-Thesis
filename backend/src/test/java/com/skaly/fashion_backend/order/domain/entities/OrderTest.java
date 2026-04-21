package com.skaly.fashion_backend.order.domain.entities;

import com.skaly.fashion_backend.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        void setsCancelledStatusReasonAndTimestamp() {
            Order order = Order.builder()
                    .id(UUID.randomUUID())
                    .status(OrderStatus.PENDING)
                    .build();
            LocalDateTime before = LocalDateTime.now().minusNanos(500_000_000);

            order.cancel("Khách đổi ý");

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(order.getCancelledReason()).isEqualTo("Khách đổi ý");
            assertThat(order.getCancelledAt()).isNotNull();
            assertThat(order.getCancelledAt()).isAfterOrEqualTo(before);
            assertThat(order.getCancelledAt()).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
        }
    }

    @Nested
    @DisplayName("addItem")
    class AddItem {

        @Test
        void appendsItemToCollection() {
            Order order = Order.builder().items(new ArrayList<>()).build();
            OrderItem line = OrderItem.builder()
                    .id(UUID.randomUUID())
                    .productVariantId(UUID.randomUUID())
                    .quantity(2)
                    .snapshotPrice(new BigDecimal("50.00"))
                    .build();

            order.addItem(line);

            assertThat(order.getItems()).containsExactly(line);
        }
    }

    @Nested
    @DisplayName("calculateTotal")
    class CalculateTotal {

        @Test
        void withNoItems_subTotalZero_totalReflectsTaxShippingAndDiscount() {
            Order order = Order.builder()
                    .items(new ArrayList<>())
                    .taxAmount(new BigDecimal("10.00"))
                    .shippingCost(new BigDecimal("5.00"))
                    .discountAmount(new BigDecimal("3.00"))
                    .build();

            order.calculateTotal();

            assertThat(order.getSubTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("12.00"));
        }

        @Test
        void withItems_computesSubTotalAndFinalTotal() {
            UUID v1 = UUID.randomUUID();
            Order order = Order.builder()
                    .items(new ArrayList<>(List.of(
                            OrderItem.builder()
                                    .productVariantId(v1)
                                    .quantity(2)
                                    .snapshotPrice(new BigDecimal("25.00"))
                                    .build(),
                            OrderItem.builder()
                                    .productVariantId(UUID.randomUUID())
                                    .quantity(1)
                                    .snapshotPrice(new BigDecimal("10.00"))
                                    .build()
                    )))
                    .taxAmount(new BigDecimal("5.00"))
                    .shippingCost(new BigDecimal("5.00"))
                    .discountAmount(new BigDecimal("15.00"))
                    .build();

            order.calculateTotal();

            assertThat(order.getSubTotal()).isEqualByComparingTo(new BigDecimal("60.00"));
            assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("55.00"));
        }
    }
}
