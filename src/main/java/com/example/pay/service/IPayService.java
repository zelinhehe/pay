package com.example.pay.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

public interface IPayService {
    /**
     * 创建/发起支付
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 接收 订单支付后的异步通知（我们设置的回调）
     * @param notifyData 通知内容
     */
    String asyncNotify(String notifyData);
}
