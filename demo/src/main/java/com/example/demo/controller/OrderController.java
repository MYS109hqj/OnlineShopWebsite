package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.EmailService; // 导入 EmailService
import com.example.demo.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;
    private final EmailService emailService; // 添加 EmailService

    public OrderController(OrderService orderService, EmailService emailService) {
        this.orderService = orderService;
        this.emailService = emailService; // 初始化 EmailService
    }

    // 顾客查询自己的所有订单
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    // 商家查询自己的所有订单
    @GetMapping("/merchant/{merchantId}")
    public List<Order> getOrdersByMerchant(@PathVariable Long merchantId) {
        return orderService.getOrdersByMerchantId(merchantId);
    }

    // 顾客支付订单
    @PostMapping("/{orderId}/pay")
    public Order payForOrder(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.PAID);
    }

    // 顾客收货
    @PostMapping("/{orderId}/receive")
    public Order receiveOrder(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.RECEIVED);
    }

    // 顾客取消订单
    @PostMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
    }

    // 商家发货
    @PostMapping("/{orderId}/ship")
    public Order shipOrder(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);
    }

    // 商家标记订单为待收货
    @PostMapping("/{orderId}/awaiting-receipt")
    public Order markOrderAsAwaitingReceipt(@PathVariable Long orderId) {
        Order order = orderService.updateOrderStatus(orderId, OrderStatus.AWAITING_RECEIPT);
        
        // 获取顾客的邮箱
        String customerEmail = order.getCustomer().getEmail(); // 获取顾客邮箱
        String subject = "订单状态更新"; // 邮件主题
        String body = "您的订单 #" + orderId + " 现在标记为待收货。"; // 邮件内容
        
        // 发送邮件通知顾客
        emailService.sendEmail(customerEmail, subject, body);
        
        return order; // 返回更新后的订单
    }
}
