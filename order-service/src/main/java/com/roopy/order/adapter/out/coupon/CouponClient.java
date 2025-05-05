package com.roopy.order.adapter.out.coupon;

import com.roopy.order.port.out.coupon.CouponPort;
import org.springframework.stereotype.Component;

@Component
public class CouponClient implements CouponPort {

    public boolean applyCoupon(String orderId, double price) {
        // 쿠폰 차감 요청
        // 외부 쿠폰 시스템과 통신 (여기서는 mock 처리)
        return true; // 쿠폰 차감 성공
    }

    public void cancelCoupon(String orderId) {
        // 쿠폰 취소 요청
        // 외부 쿠폰 시스템과 통신 (여기서는 mock 처리)
    }
}