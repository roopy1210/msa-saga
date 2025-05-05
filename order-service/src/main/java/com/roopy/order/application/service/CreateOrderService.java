package com.roopy.order.application.service;

import com.roopy.order.domain.command.CreateOrderCommand;
import com.roopy.order.port.in.CreateOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final CommandGateway commandGateway;

    @Override
    public void createOrder(CreateOrderCommand command) {
        // 주문 생성 이벤트 발행
        commandGateway.send(command);
    }
}