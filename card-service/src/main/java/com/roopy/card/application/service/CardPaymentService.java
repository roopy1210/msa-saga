package com.roopy.card.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roopy.card.port.in.CardPaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 카드 결제 서비스 클래스입니다.
 * <p>
 * 이 클래스는 Kafka를 통해 결제 요청을 전송하고, 결제 결과를 비동기적으로 수신합니다.
 * 각 결제 요청은 주문 ID(orderId)를 기준으로 결과를 구분하며, 동시성 처리를 위해
 * ConcurrentHashMap을 사용합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardPaymentService implements CardPaymentUseCase {

    // Kafka를 통해 메시지를 전송하기 위한 템플릿 객체
    private final KafkaTemplate<String, String> kafkaTemplate;

    // JSON 직렬화 및 역직렬화를 위한 ObjectMapper 객체
    private final ObjectMapper objectMapper;

    /**
     * 주문 ID별로 결제 결과를 저장하는 맵입니다.
     * 여러 사용자가 동시에 결제 요청을 보낼 수 있으므로,
     * 동시성 처리를 위해 ConcurrentHashMap을 사용합니다.
     */
    private final Map<String, CompletableFuture<String>> paymentResultFutures = new ConcurrentHashMap<>();

    /**
     * 결제 요청을 Kafka를 통해 발행하고, 결과를 비동기적으로 기다립니다.
     *
     * @param orderId 주문 ID (예: "ORDER123")
     * @param amount 결제 금액 (예: 10000)
     * @return 결제 결과를 담은 CompletableFuture
     */
    public CompletableFuture<String> sendPaymentRequest(String orderId, int amount) {
        // 비동기 결과를 위한 CompletableFuture 객체 생성
        CompletableFuture<String> future = new CompletableFuture<>();

        // 주문 ID를 키로 하여 맵에 저장 (결제 결과 수신 시 사용)
        paymentResultFutures.put(orderId, future);

        try {
            // 결제 요청 데이터를 JSON 문자열로 변환
            String json = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "paymentAmt", amount,
                    "success", true
            ));

            // Kafka 토픽(card-payment-request)으로 메시지 전송
            kafkaTemplate.executeInTransaction(kt -> {
                kt.send("test-topic", json);
                return true;
            });
            log.info("결제 요청 전송 완료: {}", json);

        } catch (Exception e) {
            // JSON 변환 또는 Kafka 전송 중 오류가 발생한 경우
            log.error("결제 요청 전송 실패", e);
            future.completeExceptionally(e);
            paymentResultFutures.remove(orderId); // 실패한 요청은 맵에서 제거
        }

        // 비동기적으로 결과를 반환
        return future;
    }

    /**
     * Kafka로부터 결제 결과 메시지를 수신합니다.
     * <p>
     * Kafka 토픽(card-payment-result)으로부터 수신된 메시지는 JSON 형식이어야 합니다.
     * 메시지 예시: {"orderId":"ORDER123", "result":"success"}
     *
     * @param message 수신된 Kafka 메시지(JSON 문자열)
     */
    @KafkaListener(topics = "test-topic", groupId = "card-service-group")
    public void receivePaymentResult(String message) {
        log.info("결제 결과 수신(JSON): {}", message);

        try {
            // JSON 문자열을 객체로 파싱
            JsonNode rootNode = objectMapper.readTree(message);

            // orderId 값을 추출 (예: "ORDER123")
            String orderId = rootNode.get("orderId").asText();

            // 해당 orderId에 대한 CompletableFuture 가져오기 및 제거
            CompletableFuture<String> future = paymentResultFutures.remove(orderId);

            // 해당 요청이 존재하고, 아직 완료되지 않은 경우 결과 전달
            if (future != null && !future.isDone()) {
                future.complete(message); // 결과 메시지를 반환
            } else {
                log.warn("결제 결과에 해당하는 요청이 없습니다. orderId: {}", orderId);
            }
        } catch (Exception e) {
            // JSON 파싱 실패 또는 기타 예외 처리
            log.error("결제 결과 JSON 파싱 실패", e);
        }
    }
}
