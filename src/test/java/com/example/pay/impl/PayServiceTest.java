package com.example.pay.impl;

import com.example.pay.PayApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    PayService payService;

    @Test
    public void create() {
        // BigDecimal.valueOf(0.01)
        // new BigDecimal("0.01")
        // new BigDecimal(0.01)  这是错误的
        payService.create("12413231212", BigDecimal.valueOf(0.01));
    }
}