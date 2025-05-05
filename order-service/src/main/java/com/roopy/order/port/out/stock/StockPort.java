package com.roopy.order.port.out.stock;

public interface StockPort {
    boolean deductStock(String product, int quantity);

    void cancelStock(String orderId);
}
