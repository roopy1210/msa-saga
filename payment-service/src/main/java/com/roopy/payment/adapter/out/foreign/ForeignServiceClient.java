package com.roopy.payment.adapter.out.foreign;

import com.roopy.payment.port.out.ForeignPaymentPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ForeignServiceClient implements ForeignPaymentPort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean requestForeignPayment(String paymentId, int amount, String currency) {
        log.info("[ForeignServiceClient] Sending request to foreign-service");
        // 실제 요청은 외부 API URL 구성 필요
        try {
            String url = String.format(
                    "http://localhost:8082/foreign/payment?paymentId=%s&amount=%d&currency=%s",
                    paymentId, amount, currency
            );

            String result = restTemplate.postForObject(url, null, String.class);
            return "OK".equals(result);
        } catch (Exception e) {
            log.error("Foreign payment request failed", e);
            return false;
        }
    }
}