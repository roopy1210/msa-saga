package com.roopy.order.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private final String orderId;
    private final String productCode;
    private final int quantity;
    private final int cardPayment;
    private final int couponPayment;
}