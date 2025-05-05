package com.roopy.payment.port.out;

public interface ForeignPaymentPort {
    boolean requestForeignPayment(String paymentId, int amount, String currency);
}