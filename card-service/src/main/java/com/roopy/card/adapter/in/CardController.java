package com.roopy.card.adapter.in;

import com.roopy.card.port.in.CardPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class CardController {

    private final CardPaymentUseCase cardPaymentUseCase;

    @PostMapping("/{orderId}")
    public boolean processPayment(@PathVariable String orderId, @RequestParam int paymentAmt) {
        return cardPaymentUseCase.processPayment(orderId, paymentAmt);
    }
}