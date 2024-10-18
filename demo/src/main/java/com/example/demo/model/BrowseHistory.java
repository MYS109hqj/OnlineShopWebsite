package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "browse_history", 
       indexes = {
           @Index(name = "idx_customer_product", columnList = "customer_id, product_id"),
           @Index(name = "idx_browse_time", columnList = "browse_time")
       })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BrowseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 顾客外键
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 产品外键
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 浏览时间
    @Column(name = "browse_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime browseTime = LocalDateTime.now();

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(LocalDateTime browseTime) {
        this.browseTime = browseTime;
    }
}
