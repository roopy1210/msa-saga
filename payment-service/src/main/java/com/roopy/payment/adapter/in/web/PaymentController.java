package com.roopy.payment.adapter.in.web;

import com.roopy.payment.port.in.CreatePaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    @PostMapping
    public String createPayment(@RequestParam String userId,
                                @RequestParam int amount,
                                @RequestParam String currency) {
        log.info("[Controller] Create payment request: userId={}, amount={}, currency={}",
                userId, amount, currency);
        createPaymentUseCase.createPayment(userId, amount, currency);
        return "Payment requested";
    }
}