package com.roopy.payment.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCreatedEvent {
    private final String paymentId;
    private final String userId;
    private final int amount;
    private final String currency;
}