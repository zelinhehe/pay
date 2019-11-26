package com.example.pay.impl;

import com.example.pay.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
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
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {

        if (bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE && bestPayTypeEnum != BestPayTypeEnum.ALIPAY_PC) {
            throw new RuntimeException("暂不支持此支付类型。仅支持：微信Native，支付宝PC");
        }

        // 写入数据库，创建订单记录


        PayRequest request = new PayRequest();
        request.setOrderName("1321-我的订单");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
//        request.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        request.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(request);

        log.info("response={}", response);

        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {
        // 1.签名校验 option+cmd+b 查看源码
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);

        // 2.金额校验（从数据库中查订单，看异步通知回调中的金额是否和创建订单时记录的金额一致）


        // 3.修改订单状态


        // 4.告诉微信或支付宝我收到通知了，不要继续通知了
        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        }

        throw new RuntimeException("异步通知中不支持的支付平台");
    }
}
