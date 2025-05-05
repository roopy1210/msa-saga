package com.roopy.order.port.out.card;

public interface CardPort {
    boolean processPayment(String orderId, int paymentAmt);
}
