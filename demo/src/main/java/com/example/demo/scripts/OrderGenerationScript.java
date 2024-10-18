package com.example.demo.scripts;

import com.example.demo.model.*;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class OrderGenerationScript {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Random random = new Random();

    // 生成订单并保存到数据库
    public void generateOrders() {
        List<Customer> customers = customerRepository.findAll(); // 获取所有顾客

        // 指定的商品ID列表
        List<Long> productIds = List.of(2L, 3L, 4L, 5L, 6L, 91L, 92L, 93L, 94L, 95L);
        List<Product> products = productRepository.findAllById(productIds); // 根据ID获取指定商品

        for (Product product : products) {
            // 为每个商品生成 5-30 个订单
            int orderCount = random.nextInt(26) + 5; // 随机生成 5 到 30 个订单
            for (int i = 0; i < orderCount; i++) {
                Customer customer = customers.get(random.nextInt(customers.size())); // 随机选择顾客

                // 创建新订单
                Order order = new Order();
                order.setCustomer(customer);
                order.setMerchant(product.getMerchant());
                order.setOrderStatus(OrderStatus.RECEIVED); // 所有订单状态设为 "已收货"
                order.setOrderDate(generateRandomDate());

                // 生成订单项
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                int quantity = random.nextInt(5) + 1; // 随机生成 1-5 的数量
                orderItem.setQuantity(quantity);
                orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

                // 将订单项添加到订单中
                order.setOrderItems(List.of(orderItem));
                orderItem.setOrder(order);  // 设置订单项与订单的关系

                // 计算订单总价
                order.calculateTotalPrice();

                // 保存订单到数据库
                orderRepository.save(order);
            }
        }
        System.out.println("Orders generated successfully.");
    }

    // 随机生成订单日期：2024-09-22 到 2024-10-12
    private LocalDateTime generateRandomDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 9, 22, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 10, 12, 23, 59);
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        return startDate.plusDays(random.nextInt((int) days + 1));
    }
}
