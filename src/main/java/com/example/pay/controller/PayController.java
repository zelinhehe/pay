package com.example.pay.controller;

import com.example.pay.IPayService;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    IPayService payService;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount")BigDecimal amount) {
        PayResponse response = payService.create(orderId, amount);

        Map<String, String> map = new HashMap<>();
        map.put("codeUrl", response.getCodeUrl());
        return new ModelAndView("create", map);
    }
}