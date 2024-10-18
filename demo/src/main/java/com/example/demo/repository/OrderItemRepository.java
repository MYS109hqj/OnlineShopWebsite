package com.example.demo.repository;

import com.example.demo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 获取所有商品的购买量
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.orderStatus IN ('PAID', 'SHIPPED', 'AWAITING_RECEIPT', 'RECEIVED') " +
           "GROUP BY oi.product.id")
    List<Object[]> findProductPurchaseQuantities();

    // 获取单个商品的购买量
    @Query("SELECT SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderStatus IN ('PAID', 'SHIPPED', 'AWAITING_RECEIPT', 'RECEIVED')")
    Long findPurchaseQuantityByProductId(@Param("productId") Long productId);

    // 获取某个商家所有商品的购买量
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "WHERE p.merchant.id = :merchantId " +
           "AND o.orderStatus IN ('PAID', 'SHIPPED', 'AWAITING_RECEIPT', 'RECEIVED') " +
           "GROUP BY oi.product.id")
    List<Object[]> findPurchaseQuantitiesByMerchantId(@Param("merchantId") Long merchantId);
}
