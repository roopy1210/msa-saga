package com.roopy.order.port.out.coupon;

public interface CouponPort {
    boolean applyCoupon(String orderId, double price);

    void cancelCoupon(String orderId);
}
