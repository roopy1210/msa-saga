package com.roopy.payment.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class CancelPaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String reason;
}
