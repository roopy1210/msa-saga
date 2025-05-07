package com.roopy.card.port.in;

import java.util.concurrent.CompletableFuture;

public interface CardPaymentUseCase {

    /**
     * 결제 요청을 처리합니다.
     *
     * @param orderId 주문 ID
     * @param amount 결제 금액
     */
    CompletableFuture<String> sendPaymentRequest(String orderId, int amount);

    /**
     * 결제 결과 메시지를 처리합니다.
     *
     * @param message 결제 결과 메시지
     */
    void receivePaymentResult(String message);
}
