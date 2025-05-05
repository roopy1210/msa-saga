package com.roopy.payment.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class CreatePaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String userId;
    private final int amount;
    private final String currency;
}
