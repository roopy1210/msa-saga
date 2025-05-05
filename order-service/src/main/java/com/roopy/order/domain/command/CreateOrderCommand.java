package com.roopy.order.domain.command;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Getter
public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productCode;
    private final int quantity;
    private final int price;
    private final int cardPayment;
    private final int couponPayment;

    // orderId는 UUID로 자동 생성, price는 cardPayment + couponPayment 로 자동 계산
    public CreateOrderCommand(String productCode, int quantity, int cardPayment, int couponPayment) {
        this.orderId = UUID.randomUUID().toString();
        this.productCode = productCode;
        this.quantity = quantity;
        this.cardPayment = cardPayment;
        this.couponPayment = couponPayment;
        this.price = cardPayment + couponPayment;
    }
}
