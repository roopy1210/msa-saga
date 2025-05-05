package com.roopy.card.application.service;

import com.roopy.card.port.in.CardPaymentUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CardPaymentService implements CardPaymentUseCase {
    @Override
    public boolean processPayment(String orderId, int paymentAmt) {
        log.info("[CardPaymentService] 카드 결제 처리 요청: orderId={}, paymentAmt={}", orderId, paymentAmt);

        // 실제 서비스라면 외부 금융 API 호출
        boolean isSuccess = paymentAmt > 8000;
        log.info("[CardPaymentService] 카드 결제 처리 결과: {}", isSuccess ? "성공" : "실패");
        return isSuccess;
    }
}
