package com.example.pay;

import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

public interface IPayService {
    /**
     * 创建/发起支付
     */
    PayResponse create(String orderId, BigDecimal amount);

    void asyncNotify(String notifyData);
}
