package com.roopy.order.adapter.out.card;

import com.roopy.order.adapter.out.card.dto.CardPaymentResponse;
import com.roopy.order.port.out.card.CardPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardClient implements CardPort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean processPayment(String orderId, int paymentAmt) {
        log.info("[CardClient] 주문ID={}에 대해 {}원 카드 결제 요청 전송", orderId, paymentAmt);

        try {
            String url = String.format("http://localhost:10001/payments/%s?paymentAmt=%d", orderId, paymentAmt);

            CardPaymentResponse response = restTemplate.postForObject(
                    url,
                    HttpEntity.EMPTY,
                    CardPaymentResponse.class
            );

            log.info("[CardClient] 카드사 응답 수신: {}", response);

            return response.isSuccess();

        } catch (Exception e) {
            log.error("[CardClient] 카드 결제 요청 실패 - 주문ID={}, 결제금액={}원", orderId, paymentAmt, e);
            return false;
        }
    }
}
