package com.roopy.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * PaymentGatewayService는 카드사와의 통신을 담당하는 시뮬레이션 서비스입니다.
 * <p>
 * 1. Kafka의 "card-payment-request" 토픽에서 결제 요청을 수신합니다. (Consumer)
 * 2. 카드사 API와 통신한 것처럼 결과를 생성합니다.
 * 3. Kafka의 "card-payment-result" 토픽으로 결제 결과를 전송합니다. (Producer)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    // Kafka 메시지 전송을 위한 KafkaTemplate
    private final KafkaTemplate<String, String> kafkaTemplate;

    // JSON 파싱을 위한 Jackson ObjectMapper
    private final ObjectMapper objectMapper;

    /**
     * 카드 결제 요청을 Kafka에서 수신하는 Consumer 메소드입니다.
     * 카드사 통신을 시뮬레이션하고 결과를 다시 Kafka로 발행합니다.
     *
     * @param message 결제 요청 메시지 (JSON 문자열)
     */
    @KafkaListener(topics = "test-topic", groupId = "pg-gateway-group")
    public void consumePaymentRequest(String message) {
        log.info("[PG Gateway] 결제 요청 수신 - {}", message);

        try {
            // 메시지를 JSON 형식으로 파싱
            JsonNode jsonNode = objectMapper.readTree(message);
            String orderId = jsonNode.get("orderId").asText();
            int paymentAmt = jsonNode.get("paymentAmt").asInt();

            // 카드사 API 호출을 시뮬레이션 (여기서는 항상 성공 처리)
            String result = "success";

            // 결과 JSON 생성
            String resultJson = objectMapper.writeValueAsString(
                    Map.of("orderId", orderId, "result", result)
            );

            // 결과 메시지를 Kafka로 발행
            kafkaTemplate.executeInTransaction(kt -> {
                kt.send("card-payment-result", resultJson);
                return true;
            });
            log.info("[PG Gateway] 결제 결과 전송 - {}", resultJson);

        } catch (Exception e) {
            log.error("[PG Gateway] 결제 요청 처리 중 오류", e);
        }
    }
}
