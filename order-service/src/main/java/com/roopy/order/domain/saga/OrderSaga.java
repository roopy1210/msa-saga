package com.roopy.order.domain.saga;

import com.roopy.order.domain.event.OrderCreatedEvent;
import com.roopy.order.port.out.card.CardPort;
import com.roopy.order.port.out.coupon.CouponPort;
import com.roopy.order.port.out.stock.StockPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import javax.inject.Inject;

/**
 * 주문 처리 SAGA 클래스입니다.
 *
 * <p>
 * 이 클래스는 주문 생성 이후 재고 차감, 쿠폰 적용, 카드 결제 등의 프로세스를 순차적으로 처리합니다.
 * 실패 시에는 보상 트랜잭션(재고 복원, 쿠폰 복원)을 수행합니다.
 * <p>
 * <b>처리 흐름 요약:</b>
 * <ol>
 *     <li>OrderAggregate 내부에서 {@code apply(new OrderCreatedEvent(...))} 호출</li>
 *     <li>Axon이 {@code OrderCreatedEvent} 이벤트를 발행</li>
 *     <li>Axon이 해당 이벤트를 감지하여 이 SAGA의 {@code on(OrderCreatedEvent)} 메서드를 실행</li>
 *     <li>재고 차감 → 쿠폰 적용 → 카드 결제 순으로 처리</li>
 *     <li>중간 실패 시 보상 트랜잭션 수행</li>
 * </ol>
 */
@Saga
@Slf4j
@RequiredArgsConstructor
public class OrderSaga {

    /**
     * 명령을 전송하기 위한 Axon Framework의 게이트웨이입니다.
     * 직렬화 대상에서 제외되며, 런타임에 주입됩니다.
     */
    @Inject
    private transient CommandGateway commandGateway;

    /**
     * 재고 차감/복원 포트
     */
    @Inject
    private transient StockPort stockPort;

    /**
     * 쿠폰 적용/복원 포트
     */
    @Inject
    private transient CouponPort couponPort;

    /**
     * 카드 결제 포트
     */
    @Inject
    private transient CardPort cardPort;

    /**
     * 주문 생성 이벤트를 수신하여 Saga 처리를 시작합니다.
     *
     * @param event 주문 생성 이벤트
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("[Saga:START] 주문 생성 이벤트 수신 - orderId={}", event.getOrderId());

        // 1. 재고 차감 처리
        if (!deductStock(event)) return;

        // 2. 쿠폰 적용 처리
        if (!applyCoupon(event)) {
            rollbackStock(event);
            return;
        }

        // 3. 카드 결제 처리
        if (!processCardPayment(event)) {
            rollbackCoupon(event);
            rollbackStock(event);
            return;
        }

        log.info("[Saga:SUCCESS] 주문 처리 완료 - orderId={}", event.getOrderId());
    }

    /**
     * 재고 차감을 시도합니다.
     */
    private boolean deductStock(OrderCreatedEvent event) {
        log.info("[Saga:STEP] 재고 차감 시도 - productCode={}, quantity={}", event.getProductCode(), event.getQuantity());
        boolean result = stockPort.deductStock(event.getProductCode(), event.getQuantity());
        if (!result) {
            log.error("[Saga:FAIL] 재고 차감 실패 - orderId={}", event.getOrderId());
        }
        return result;
    }

    /**
     * 쿠폰을 적용합니다.
     */
    private boolean applyCoupon(OrderCreatedEvent event) {
        log.info("[Saga:STEP] 쿠폰 적용 시도 - orderId={}, 금액={}", event.getOrderId(), event.getCouponPayment());
        boolean result = couponPort.applyCoupon(event.getOrderId(), event.getCouponPayment());
        if (!result) {
            log.error("[Saga:FAIL] 쿠폰 적용 실패 - orderId={}", event.getOrderId());
        }
        return result;
    }

    /**
     * 카드 결제를 처리합니다.
     */
    private boolean processCardPayment(OrderCreatedEvent event) {
        log.info("[Saga:STEP] 카드 결제 시도 - orderId={}, 금액={}", event.getOrderId(), event.getCardPayment());
        boolean result = cardPort.processPayment(event.getOrderId(), event.getCardPayment());
        if (!result) {
            log.error("[Saga:FAIL] 카드 결제 실패 - orderId={}", event.getOrderId());
        }
        return result;
    }

    /**
     * 재고 보상 처리 (재고 복원)
     */
    private void rollbackStock(OrderCreatedEvent event) {
        log.warn("[Saga:ROLLBACK] 재고 복원 수행 - orderId={}, quantity={}", event.getOrderId(), event.getQuantity());
        stockPort.cancelStock(event.getOrderId());
    }

    /**
     * 쿠폰 보상 처리 (쿠폰 복원)
     */
    private void rollbackCoupon(OrderCreatedEvent event) {
        log.warn("[Saga:ROLLBACK] 쿠폰 복원 수행 - orderId={}, paymentAmt={}", event.getOrderId(), event.getCouponPayment());
        couponPort.cancelCoupon(event.getOrderId());
    }
}
