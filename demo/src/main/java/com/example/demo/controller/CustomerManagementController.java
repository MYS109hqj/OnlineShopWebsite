package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class CustomerManagementController {

    private final OrderService orderService;

    public CustomerManagementController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 获取商家所有下过订单的顾客
    @GetMapping("/{merchantId}/customers")
    public ResponseEntity<List<Customer>> getCustomersByMerchantId(@PathVariable Long merchantId) {
        List<Customer> customers = orderService.getCustomersByMerchantId(merchantId);
        return ResponseEntity.ok(customers);
    }

    // 获取商家某个顾客的订单
    @GetMapping("/{merchantId}/customers/{customerId}/orders")
    public ResponseEntity<List<Order>> getOrdersByMerchantIdAndCustomerId(@PathVariable Long merchantId,
                                                                          @PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByMerchantIdAndCustomerId(merchantId, customerId);
        return ResponseEntity.ok(orders);
    }

    // 检查顾客是否有进行中的订单
    @GetMapping("/{merchantId}/customers/{customerId}/ongoing-orders")
    public ResponseEntity<Boolean> hasOngoingOrders(@PathVariable Long merchantId,
                                                    @PathVariable Long customerId) {
        boolean hasOngoing = orderService.hasOngoingOrders(merchantId, customerId);
        return ResponseEntity.ok(hasOngoing);
    }
}
