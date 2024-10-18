package com.example.demo.service;

import com.example.demo.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 获取所有商品的购买量
    public Map<Long, Long> getProductPurchaseQuantities() {
        List<Object[]> results = orderItemRepository.findProductPurchaseQuantities();
        Map<Long, Long> productQuantities = new HashMap<>();

        for (Object[] result : results) {
            Long productId = (Long) result[0];
            Long totalQuantity = (Long) result[1];
            productQuantities.put(productId, totalQuantity);
        }

        return productQuantities;
    }

    // 获取单个商品的购买量
    public Long getPurchaseQuantityByProductId(Long productId) {
        return orderItemRepository.findPurchaseQuantityByProductId(productId);
    }

    // 获取某个商家所有商品的购买量
    public Map<Long, Long> getPurchaseQuantitiesByMerchantId(Long merchantId) {
        List<Object[]> results = orderItemRepository.findPurchaseQuantitiesByMerchantId(merchantId);
        Map<Long, Long> productQuantities = new HashMap<>();

        for (Object[] result : results) {
            Long productId = (Long) result[0];
            Long totalQuantity = (Long) result[1];
            productQuantities.put(productId, totalQuantity);
        }

        return productQuantities;
    }
}
