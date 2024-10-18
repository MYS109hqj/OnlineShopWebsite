// 文件路径: com/example/demo/repository/ProductRepository.java

package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 按商品状态查找
    List<Product> findByStatus(Boolean status);

    // 按商家ID查找商品
    List<Product> findByMerchantId(Long merchantId);

    // 根据商家ID和商品状态获取商品
    List<Product> findByMerchantIdAndStatus(Long merchantId, Boolean status);

    // 根据商家ID和商品ID查找商品，确保商品属于该商家
    Optional<Product> findByIdAndMerchantId(Long productId, Long merchantId);
}
