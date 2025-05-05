package com.roopy.payment.domain.saga;

import com.roopy.payment.adapter.out.foreign.ForeignServiceClient;
import com.roopy.payment.domain.command.CancelPaymentCommand;
import com.roopy.payment.domain.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import javax.inject.Inject;

/**
 * 결제 프로세스를 관리하는 SAGA 클래스입니다.
 * <p>
 * 이 클래스는 {@code @Saga} 어노테이션이 적용되어 있으며, Axon Framework에 의해 런타임에 자동으로 관리됩니다.
 * 개발자가 명시적으로 이 클래스를 호출하지 않습니다.
 * <p>
 * <b>작동 순서 요약:</b>
 * <ol>
 *     <li>PaymentAggregate 내부에서 {@code apply(new PaymentCreatedEvent(...))} 호출</li>
 *     <li>Axon이 {@code PaymentCreatedEvent} 이벤트를 발행</li>
 *     <li>Axon이 해당 이벤트를 감지하여 이 SAGA 클래스의 {@code on(PaymentCreatedEvent)} 메서드를 실행</li>
 *     <li>외부 결제 시스템 호출</li>
 *     <li>실패 시 보상 트랜잭션으로 {@code CancelPaymentCommand} 전송</li>
 * </ol>
 */
@Saga
@Slf4j
@RequiredArgsConstructor
public class PaymentSaga {

    /**
     * Axon Framework를 통해 명령을 전송하는 컴포넌트입니다.
     * <p>
     * {@code transient} 키워드는 이 필드가 SAGA 인스턴스 직렬화 시 제외되어야 함을 나타냅니다.
     * <br>
     * Axon은 SAGA 상태를 persistence store (JPA, JDBC 등)에 저장하기 때문에,
     * 직렬화 가능한 필드만 유지되며, 프레임워크가 런타임에 주입합니다.
     */
    @Inject
    private transient CommandGateway commandGateway;

    /**
     * 외부 결제 시스템(foreign-service)과 통신하는 포트입니다.
     * <p>
     * 실제 외부 PG사 호출은 이 포트를 통해 진행되며, 마찬가지로 SAGA 직렬화에서 제외됩니다.
     */
    @Inject
    private transient ForeignServiceClient foreignServiceClient;

    /**
     * 결제 생성 이벤트를 수신하여 SAGA를 시작하는 메서드입니다.
     * <p>
     * {@code @StartSaga}는 이 이벤트가 SAGA를 시작하는 트리거임을 나타냅니다.
     * {@code associationProperty = "paymentId"} 설정으로 이벤트와 SAGA 인스턴스를 연결합니다.
     *
     * @param event 결제 생성 이벤트
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentCreatedEvent event) {
        log.info("[Saga:START] 결제 생성 이벤트 수신 - paymentId={}, amount={}, currency={}",
                event.getPaymentId(), event.getAmount(), event.getCurrency());

        // 외부 결제 시스템 호출
        boolean success = callForeignPayment(event.getPaymentId(), event.getAmount(), event.getCurrency());

        // 결제 실패 시 보상 트랜잭션 실행
        if (!success) {
            log.warn("[Saga:FAIL] 외부 결제 실패 - paymentId={}. 취소 명령 전송 중...", event.getPaymentId());
            commandGateway.send(new CancelPaymentCommand(event.getPaymentId(), "Foreign payment failed"));
        } else {
            log.info("[Saga:SUCCESS] 외부 결제 성공 - paymentId={}", event.getPaymentId());
        }
    }

    /**
     * 외부 결제 시스템을 호출하는 내부 메서드입니다.
     * <p>
     * 실제 구현에서는 외부 API와 비동기 통신을 하며, 현재는 {@code ForeignServiceClient}를 통해 호출됩니다.
     *
     * @param paymentId 결제 ID
     * @param amount    결제 금액
     * @param currency  통화 코드 (예: KRW, USD)
     * @return 외부 결제 성공 여부
     */
    private boolean callForeignPayment(String paymentId, int amount, String currency) {
        log.info("[Saga:CALL] 외부 결제 API 호출 시작 - paymentId={}, amount={}, currency={}",
                paymentId, amount, currency);

        boolean result = foreignServiceClient.requestForeignPayment(paymentId, amount, currency);

        log.info("[Saga:CALL] 외부 결제 API 호출 결과 - paymentId={}, result={}", paymentId, result);
        return result;
    }
}
