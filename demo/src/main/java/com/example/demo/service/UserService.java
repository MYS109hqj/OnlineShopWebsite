package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 注销用户
    public String deleteUser(Long userId) {
        // 找到用户
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "用户不存在";
        }

        // 找到顾客信息
        Customer customer = customerRepository.findByUserId(userId).orElse(null);
        if (customer == null) {
            return "顾客信息不存在";
        }

        // 检查该用户的所有订单
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());
        for (Order order : orders) {
            if (!order.getOrderStatus().equals(OrderStatus.CANCELED) && !order.getOrderStatus().equals(OrderStatus.RECEIVED)) {
                return "用户有未完成的订单，无法注销";
            }
        }

        // 删除用户及相关信息
        userRepository.delete(user);
        return "用户注销成功";
    }
}
