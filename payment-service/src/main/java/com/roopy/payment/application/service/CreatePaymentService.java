package com.roopy.payment.application.service;

import com.roopy.payment.domain.command.CreatePaymentCommand;
import com.roopy.payment.port.in.CreatePaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentService implements CreatePaymentUseCase {

    private final CommandGateway commandGateway;

    @Override
    public void createPayment(String userId, int amount, String currency) {
        String paymentId = UUID.randomUUID().toString();
        log.info("[Service] Sending CreatePaymentCommand with ID: {}", paymentId);
        commandGateway.send(new CreatePaymentCommand(paymentId, userId, amount, currency));
    }
}