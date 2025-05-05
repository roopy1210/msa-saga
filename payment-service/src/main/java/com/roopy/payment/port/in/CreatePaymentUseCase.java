package com.roopy.payment.port.in;

public interface CreatePaymentUseCase {
    void createPayment(String userId, int amount, String currency);
}
