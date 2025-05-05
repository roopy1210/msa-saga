package com.roopy.order.adapter.out.stock;

import com.roopy.order.port.out.stock.StockPort;
import org.springframework.stereotype.Component;

@Component
public class StockClient implements StockPort  {

    public boolean deductStock(String product, int quantity) {
        // 재고 차감 요청
        // 외부 재고 시스템과 통신 (여기서는 mock 처리)
        return true; // 재고 차감 성공
    }

    public void cancelStock(String orderId) {
        // 재고 취소 요청
        // 외부 재고 시스템과 통신 (여기서는 mock 처리)
    }
}