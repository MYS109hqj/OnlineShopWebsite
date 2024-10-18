package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据顾客ID查询订单
    List<Order> findByCustomerId(Long customerId);

    // 根据商家ID查询订单
    List<Order> findByMerchantId(Long merchantId);

    // 获取下过订单的顾客列表（基于商家ID）
    @Query("SELECT DISTINCT o.customer FROM Order o WHERE o.merchant.id = :merchantId")
    List<Customer> findDistinctCustomerByMerchantId(@Param("merchantId") Long merchantId);

    // 根据商家ID和顾客ID获取该商家下的订单
    List<Order> findByMerchantIdAndCustomerId(Long merchantId, Long customerId);

    // 检查是否有进行中的订单（进行中的订单状态不是“已取消”和“已收货”）
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM Order o WHERE o.merchant.id = :merchantId " +
           "AND o.customer.id = :customerId " +
           "AND o.orderStatus NOT IN ('CANCELED', 'RECEIVED')")
    boolean hasOngoingOrders(@Param("merchantId") Long merchantId, @Param("customerId") Long customerId);

    // 根据商家ID和订单日期范围查询订单
    List<Order> findByMerchantIdAndOrderDateBetween(Long merchantId, LocalDateTime start, LocalDateTime end);
}
