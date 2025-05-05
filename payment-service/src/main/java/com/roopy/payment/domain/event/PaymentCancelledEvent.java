package com.roopy.payment.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCancelledEvent {
    private final String paymentId;
    private final String reason;
}