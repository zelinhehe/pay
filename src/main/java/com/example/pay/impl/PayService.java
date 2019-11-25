package com.example.pay.impl;

import com.example.pay.IPayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayService implements IPayService {

    @Autowired
    BestPayService bestPayService;

    @Override
    public PayResponse create(String orderId, BigDecimal amount) {

        PayRequest request = new PayRequest();
        request.setOrderName("1321-我的订单");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        PayResponse response = bestPayService.pay(request);

        log.info("response={}", response);

        return response;
    }

    @Override
    public void asyncNotify(String notifyData) {
        // 签名校验 option+cmd+b 查看源码
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);
    }
}
