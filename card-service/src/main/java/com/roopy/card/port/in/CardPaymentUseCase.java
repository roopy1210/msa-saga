package com.roopy.card.port.in;

public interface CardPaymentUseCase {
    boolean processPayment(String orderId, int paymentAmt);
}
