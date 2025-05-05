package com.roopy.payment.domain.model;

import com.roopy.payment.domain.command.CreatePaymentCommand;
import com.roopy.payment.domain.command.CancelPaymentCommand;
import com.roopy.payment.domain.event.PaymentCreatedEvent;
import com.roopy.payment.domain.event.PaymentCancelledEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * PaymentAggregate는 결제 도메인의 Aggregate로,
 * 결제 생성 및 취소 명령(Command)을 처리하고 그에 따른 이벤트(Event)를 발생시킵니다.
 * <p>
 * Axon Framework에서 Aggregate는 도메인의 상태를 일관되게 유지하면서
 * Command를 받아서 Event를 통해 상태를 변화시키는 중심 객체입니다.
 */
@Slf4j
@Aggregate  // Axon에서 이 클래스가 Aggregate임을 나타냄
@NoArgsConstructor  // Axon에서 리플렉션으로 인스턴스를 생성할 때 필요
public class PaymentAggregate {

    /**
     * 이 식별자는 Aggregate 인스턴스를 유일하게 구분하기 위한 필드입니다.
     */
    @AggregateIdentifier
    private String paymentId;

    private String userId;
    private int amount;
    private String currency;

    /**
     * 결제 생성 커맨드를 처리하는 CommandHandler.
     *
     * @param cmd CreatePaymentCommand - 결제 생성 요청 정보
     */
    @CommandHandler
    public PaymentAggregate(CreatePaymentCommand cmd) {
        log.info("[Aggregate] Handling CreatePaymentCommand: {}", cmd.getPaymentId());

        /**
         * apply()는 Axon에서 이벤트 소싱 방식으로 상태를 변경하기 위한 메서드입니다.
         * <p>
         * 아래 코드에서는 'PaymentCreatedEvent'라는 이벤트를 생성하여 시스템에 알립니다.
         * 이 이벤트는:
         *  - 이벤트 스토어에 저장되고
         *  - EventSourcingHandler 메서드(on 메서드)가 호출되어 상태가 변경됩니다.
         * <p>
         * 즉, 상태 변경은 이벤트를 "적용(apply)"한 뒤에, 해당 이벤트를 처리하는 핸들러에서 이뤄집니다.
         * 이 방식은 나중에 상태 복원(replay)을 위해 이벤트를 재적용할 수 있게 해 줍니다.
         */
        apply(new PaymentCreatedEvent(
                cmd.getPaymentId(),
                cmd.getUserId(),
                cmd.getAmount(),
                cmd.getCurrency()
        ));
    }

    /**
     * 결제 취소 커맨드를 처리하는 CommandHandler.
     *
     * @param cmd CancelPaymentCommand - 결제 취소 요청 정보
     */
    @CommandHandler
    public void handle(CancelPaymentCommand cmd) {
        log.info("[Aggregate] Handling CancelPaymentCommand: {}", cmd.getPaymentId());

        // 결제 취소 이벤트를 적용
        apply(new PaymentCancelledEvent(
                cmd.getPaymentId(),
                cmd.getReason()
        ));
    }

    /**
     * 결제 생성 이벤트에 의해 Aggregate 상태를 실제로 업데이트합니다.
     * <p>
     * 이 메서드는 이벤트 소싱 방식으로 Aggregate 상태를 복원할 때도 사용됩니다.
     *
     * @param event PaymentCreatedEvent
     */
    @EventSourcingHandler
    public void on(PaymentCreatedEvent event) {
        log.info("[Aggregate] Applied PaymentCreatedEvent: {}", event.getPaymentId());
        this.paymentId = event.getPaymentId();
        this.userId = event.getUserId();
        this.amount = event.getAmount();
        this.currency = event.getCurrency();
    }

    /**
     * 결제 취소 이벤트에 의해 상태를 업데이트합니다.
     * 현재는 로그만 남기며, 상태 변경 로직은 필요 시 추가합니다.
     *
     * @param event PaymentCancelledEvent
     */
    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        log.info("[Aggregate] Applied PaymentCancelledEvent: {}", event.getPaymentId());
        // 상태 업데이트가 필요하다면 여기에 추가합니다.
    }
}
