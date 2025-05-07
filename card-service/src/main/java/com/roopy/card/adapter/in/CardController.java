package com.roopy.card.adapter.in;

import com.roopy.card.port.in.CardPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class CardController {

    private final CardPaymentUseCase cardPaymentUseCase;

    /**
     * 결제 요청을 처리하고 Kafka에서 결제 결과를 비동기적으로 받습니다.
     *
     * @param orderId 주문 ID
     * @param paymentAmt 결제 금액
     * @return 결제 요청 처리 결과
     * @throws ExecutionException 예외 발생 시
     * @throws InterruptedException 스레드가 중단될 경우 발생
     */
    @PostMapping("/{orderId}")
    public String processPayment(@PathVariable String orderId, @RequestParam int paymentAmt) throws ExecutionException, InterruptedException {
        // 결제 요청을 Kafka로 발행
        var resultFuture = cardPaymentUseCase.sendPaymentRequest(orderId, paymentAmt);
        // Kafka 수신 결과를 기다림
        return resultFuture.get(); // 결제 결과 반환
    }
}
