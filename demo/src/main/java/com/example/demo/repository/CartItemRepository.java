package com.example.demo.repository;

import com.example.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 根据商品ID查找所有与该商品相关的购物车项
    List<CartItem> findByProductId(Long productId);
}

