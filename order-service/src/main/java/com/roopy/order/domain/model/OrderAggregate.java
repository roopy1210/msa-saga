package com.roopy.order.domain.model;

import com.roopy.order.domain.command.CreateOrderCommand;
import com.roopy.order.domain.event.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Slf4j
@Aggregate
@NoArgsConstructor
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productCode;
    private int quantity;
    private int cardPayment;
    private int couponPayment;


    /**
     * 결제 생성 커맨드를 처리하는 CommandHandler.
     *
     * @param cmd CreatePaymentCommand - 결제 생성 요청 정보
     */
    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        log.info("[Aggregate] Handling CreateOrderCommand: {}", cmd.getOrderId());

        apply(new OrderCreatedEvent(
                cmd.getOrderId(),
                cmd.getProductCode(),
                cmd.getQuantity(),
                cmd.getCardPayment(),
                cmd.getCouponPayment()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("[Aggregate] Applied OrderCreatedEvent: {}", event.getOrderId());
        this.orderId = event.getOrderId();
        this.productCode = event.getProductCode();
        this.quantity = event.getQuantity();
        this.cardPayment = event.getCardPayment();
        this.couponPayment = event.getCouponPayment();
    }
}