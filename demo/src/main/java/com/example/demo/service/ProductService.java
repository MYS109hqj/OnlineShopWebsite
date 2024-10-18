package com.example.demo.service;

import com.example.demo.model.Product;
// import com.example.demo.model.OrderItem;
import com.example.demo.model.BrowseHistory;
import com.example.demo.model.CartItem;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.BrowseHistoryRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.exception.ProductDeletionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // 获取所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 按照商家ID获取商品
    public List<Product> getProductsByMerchantId(Long merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    // 根据商家ID和状态获取商品列表
    public List<Product> getProductsByMerchantIdAndStatus(Long merchantId, Boolean status) {
        return productRepository.findByMerchantIdAndStatus(merchantId, status);
    }

    // 根据状态获取所有商品
    public List<Product> getProductsByStatus(Boolean status) {
        return productRepository.findByStatus(status);
    }

    // 通过商品ID获取商品
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // 添加商品，并与商家关联
    public Product addProduct(Product product) {
        // 检查商家是否存在
        Long merchantId = product.getMerchant() != null ? product.getMerchant().getId() : null;
        if (merchantId == null || !merchantExists(merchantId)) {
            throw new IllegalArgumentException("商家不存在或商家信息不完整");
        }

        product.setModifiedAt(java.time.LocalDateTime.now());
        return productRepository.save(product);
    }

    // 检查商家是否存在
    private boolean merchantExists(Long merchantId) {
        return merchantRepository.findById(merchantId).isPresent();
    }

    // 删除商品
    @Transactional
    public void deleteProduct(Long id) {
        // 检查商品是否出现在任何订单项中
        Long orderCount = orderItemRepository.findPurchaseQuantityByProductId(id);
        if (orderCount != null && orderCount > 0) {
            throw new ProductDeletionException("无法删除商品，该商品与现有订单相关联。");
        }
    
        // 删除与该商品相关的购物车项
        List<CartItem> cartItems = cartItemRepository.findByProductId(id);
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems); // 删除相关购物车项
        }
    
        // 删除与该商品相关的浏览记录
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("商品未找到"));
        List<BrowseHistory> browseHistories = browseHistoryRepository.findByProduct(product);
        if (!browseHistories.isEmpty()) {
            browseHistoryRepository.deleteAll(browseHistories); // 删除浏览记录
        }
    
        // 最后删除商品
        productRepository.deleteById(id);
    }
    

    // 更新商品
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setImageUrl(productDetails.getImageUrl());
            product.setDescription(productDetails.getDescription());
            product.setStatus(productDetails.getStatus());
            product.setModifiedAt(java.time.LocalDateTime.now());
            return productRepository.save(product);
        }
        return null;
    }
}
