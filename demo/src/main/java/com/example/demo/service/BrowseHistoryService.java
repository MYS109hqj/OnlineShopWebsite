// 文件路径: com/example/demo/service/BrowseHistoryService.java

package com.example.demo.service;

import com.example.demo.dto.BrowseHistoryDTO;
import com.example.demo.model.BrowseHistory;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.repository.BrowseHistoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // 添加浏览记录
    public void addBrowseHistory(Long customerId, BrowseHistoryDTO browseHistory) {
        // 获取浏览的商品ID
        Long productId = browseHistory.getProductId();

        // 从数据库中获取顾客和商品对象
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("顾客未找到"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品未找到"));

        // 创建浏览记录对象
        BrowseHistory newBrowseHistory = new BrowseHistory();
        newBrowseHistory.setCustomer(customer);
        newBrowseHistory.setProduct(product);

        // 保存浏览记录
        browseHistoryRepository.save(newBrowseHistory);
    }

    // 获取顾客的浏览历史记录
    public List<BrowseHistory> getBrowseHistoryForCustomer(Long customerId) {
        return browseHistoryRepository.findByCustomerId(customerId);
    }

    // 删除顾客的某条浏览历史记录
    public void deleteBrowseHistory(Long customerId, Long historyId) {
        Optional<BrowseHistory> historyOpt = browseHistoryRepository.findByIdAndCustomerId(historyId, customerId);
        if (historyOpt.isPresent()) {
            browseHistoryRepository.deleteById(historyId);
        } else {
            throw new IllegalArgumentException("浏览记录不存在或无权删除");
        }
    }
}
