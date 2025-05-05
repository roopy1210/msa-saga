package com.roopy.foreign.port.in;

public interface ProcessForeignPaymentUseCase {
    boolean process(String paymentId, int amount, String currency);
}