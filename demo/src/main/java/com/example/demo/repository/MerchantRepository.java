package com.example.demo.repository;

import com.example.demo.model.Merchant;
// import com.example.demo.model.Product;

// import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
// import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByName(String name);

    // 根据 userId 查找商家
    Optional<Merchant> findByUserId(Long userId);
}
