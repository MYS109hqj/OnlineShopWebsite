package com.example.demo.controller;

import com.example.demo.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/purchase-quantities")  // 统一使用 purchase-quantities
public class PurchaseQuantityController {

    @Autowired
    private OrderItemService orderItemService;

    // 获取所有商品的购买量
    @GetMapping("/")
    public Map<Long, Long> getAllProductPurchaseQuantities() {
        return orderItemService.getProductPurchaseQuantities();
    }

    // 获取单个商品的购买量
    @GetMapping("/product/{productId}")
    public Long getPurchaseQuantityByProductId(@PathVariable Long productId) {
        return orderItemService.getPurchaseQuantityByProductId(productId);
    }

    // 获取某个商家所有商品的购买量
    @GetMapping("/merchant/{merchantId}")
    public Map<Long, Long> getPurchaseQuantitiesByMerchantId(@PathVariable Long merchantId) {
        return orderItemService.getPurchaseQuantitiesByMerchantId(merchantId);
    }
}
