package com.roopy.foreign.adapter.in;

import com.roopy.foreign.port.in.ProcessForeignPaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/foreign")
@RequiredArgsConstructor
public class ForeignPaymentController {

    private final ProcessForeignPaymentUseCase paymentUseCase;

    @PostMapping("/payment")
    public String processPayment(@RequestParam String paymentId,
                                 @RequestParam int amount,
                                 @RequestParam String currency) {
        log.info("[ForeignPaymentController] 외화 결제 요청 수신");
        boolean success = paymentUseCase.process(paymentId, amount, currency);
        return success ? "OK" : "FAIL";
    }
}