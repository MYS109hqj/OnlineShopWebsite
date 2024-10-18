// 文件路径: com/example/demo/repository/BrowseHistoryRepository.java

package com.example.demo.repository;

import com.example.demo.model.BrowseHistory;
import com.example.demo.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {

    // 获取顾客的所有浏览记录
    List<BrowseHistory> findByCustomerId(Long customerId);

    // 根据顾客ID和浏览记录ID查找记录（用于删除时确认权限）
    Optional<BrowseHistory> findByIdAndCustomerId(Long historyId, Long customerId);

        // 获取某商家的所有商品的浏览记录
    List<BrowseHistory> findByProductIn(List<Product> products);

    // 获取某个商品的浏览记录
    List<BrowseHistory> findByProduct(Product product);

    // 获取某个商品的浏览数量
    Long countByProduct(Product product);
}
