package com.example.pay.service.impl;

import com.example.pay.dao.PayInfoMapper;
import com.example.pay.enums.PayPlatformEnum;
import com.example.pay.pojo.PayInfo;
import com.example.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
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

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {

        if (bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE && bestPayTypeEnum != BestPayTypeEnum.ALIPAY_PC) {
            throw new RuntimeException("暂不支持此支付类型。仅支持：微信Native，支付宝PC");
        }

        // 写入数据库，创建订单记录
        PayInfo payInfo = new PayInfo(
                Long.parseLong(orderId),
                PayPlatformEnum.getBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("1321-我的订单");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
//        request.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        request.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(request);

        log.info("发起支付 response={}", response);

        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {
        // 1.签名校验 option+cmd+b 查看源码
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse={}", payResponse);

        // 2.金额校验（从数据库中查订单，看1.是否由此订单号 2.异步通知回调中的金额是否和创建订单时记录的金额一致）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null) {
            // 告警
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        if (! payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            // 使用compareTo比较 BigDecimal类型。Double类型不好比较，精度问题 1.00 1.0
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                // 告警
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo=" + payResponse.getOrderId());
            }
        }

        // 3.修改订单状态
        payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
        payInfo.setPlatformNumber(payResponse.getOutTradeNo());
        // payInfo.setUpdateTime(null);
        payInfoMapper.updateByPrimaryKeySelective(payInfo);

        // TODO:pay发送MQ消息，mall接收MQ消息

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

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
