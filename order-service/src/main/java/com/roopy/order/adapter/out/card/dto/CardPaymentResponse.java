package com.roopy.order.adapter.out.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponse {
    private String orderId;
    private int paymentAmt;
    private boolean success;
}
