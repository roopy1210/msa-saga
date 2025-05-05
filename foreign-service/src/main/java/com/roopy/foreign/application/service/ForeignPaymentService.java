package com.roopy.foreign.application.service;

import com.roopy.foreign.port.in.ProcessForeignPaymentUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ForeignPaymentService implements ProcessForeignPaymentUseCase {

    @Override
    public boolean process(String paymentId, int amount, String currency) {
        log.info("[ForeignPaymentService] 외화 결제 처리 요청: paymentId={}, amount={}, currency={}",
                paymentId, amount, currency);

        // 실제 서비스라면 외부 금융 API 호출
        boolean isSuccess = amount <= 1000; // 임의 조건: 1000 이하만 성공
        log.info("[ForeignPaymentService] 외화 결제 처리 결과: {}", isSuccess ? "성공" : "실패");
        return isSuccess;
    }
}