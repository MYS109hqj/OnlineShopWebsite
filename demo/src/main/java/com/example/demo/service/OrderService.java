package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 根据顾客ID获取订单
    public List<Order> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for customer with ID: " + customerId);
        }
        return orders;
    }

    // 根据商家ID获取订单
    public List<Order> getOrdersByMerchantId(Long merchantId) {
        List<Order> orders = orderRepository.findByMerchantId(merchantId);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for merchant with ID: " + merchantId);
        }
        return orders;
    }

    // 更新订单状态
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    // 获取商家下的所有顾客
    public List<Customer> getCustomersByMerchantId(Long merchantId) {
        List<Customer> customers = orderRepository.findDistinctCustomerByMerchantId(merchantId);
        if (customers.isEmpty()) {
            throw new RuntimeException("No customers found for merchant with ID: " + merchantId);
        }
        return customers;
    }

    // 根据商家ID和顾客ID获取订单
    public List<Order> getOrdersByMerchantIdAndCustomerId(Long merchantId, Long customerId) {
        List<Order> orders = orderRepository.findByMerchantIdAndCustomerId(merchantId, customerId);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for merchant ID: " + merchantId + " and customer ID: " + customerId);
        }
        return orders;
    }

    // 检查顾客是否有进行中的订单
    public boolean hasOngoingOrders(Long merchantId, Long customerId) {
        return orderRepository.hasOngoingOrders(merchantId, customerId);
    }
}
