// 文件路径: com/example/demo/service/MerchantBrowseHistoryService.java

package com.example.demo.service;

import com.example.demo.model.BrowseHistory;
import com.example.demo.model.Product;
import com.example.demo.repository.BrowseHistoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantBrowseHistoryService {

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // 获取商家所有商品的浏览记录
    public List<BrowseHistory> getBrowseHistoryForMerchant(Long merchantId) {
        List<Product> merchantProducts = productRepository.findByMerchantId(merchantId);
        return browseHistoryRepository.findByProductIn(merchantProducts);
    }

    // 获取某个商品的浏览记录
    public List<BrowseHistory> getBrowseHistoryForProduct(Long merchantId, Long productId) {
        Product product = productRepository.findByIdAndMerchantId(productId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在或不属于该商家"));
        return browseHistoryRepository.findByProduct(product);
    }

    // 获取商家所有商品的浏览量统计
    public Map<Long, Long> getProductBrowseCounts(Long merchantId) {
        List<Product> merchantProducts = productRepository.findByMerchantId(merchantId);
        return merchantProducts.stream()
                .collect(Collectors.toMap(Product::getId, 
                        product -> browseHistoryRepository.countByProduct(product)));
    }

    public Long getBrowseCountForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品未找到"));
        return browseHistoryRepository.countByProduct(product);
    }
}
