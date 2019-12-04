package com.example.pay.service.impl;

import com.example.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    PayService payService;

    @Test
    public void create() {
        // BigDecimal.valueOf(0.01)
        // new BigDecimal("0.01")
        // new BigDecimal(0.01)  这是错误的
        payService.create("12413231212", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
    }
}