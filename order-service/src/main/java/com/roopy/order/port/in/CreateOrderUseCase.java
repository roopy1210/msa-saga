package com.roopy.order.port.in;

import com.roopy.order.domain.command.CreateOrderCommand;

public interface CreateOrderUseCase {
    void createOrder(CreateOrderCommand command);
}
