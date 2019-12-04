package com.example.pay.controller;

import com.example.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    IPayService payService;

    // 创建支付订单
    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount")BigDecimal amount,
                               @RequestParam("payType")BestPayTypeEnum bestPayTypeEnum) {
        PayResponse response = payService.create(orderId, amount, bestPayTypeEnum);

        // 支付方式不同，渲染就不同。WXPAY_NATIVE使用codeUrl，ALIPAY_PC使用body
        Map<String, String> map = new HashMap<>();
        if (bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE) {
            map.put("codeUrl", response.getCodeUrl());
            return new ModelAndView("createForWxNative", map);
        } else if (bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC) {
            map.put("body", response.getBody());
            return new ModelAndView("createForAlipayPc", map);
        } else {
            throw new RuntimeException("暂不支持此支付类型。仅支持：微信Native，支付宝PC");
        }
    }

    // 支付回调，异步通知
    @PostMapping("/notify")
    @ResponseBody
    public String AsyncNotify(@RequestBody String notifyData){
        log.info("notifyData={}", notifyData);
        return payService.asyncNotify(notifyData);
    }
}
