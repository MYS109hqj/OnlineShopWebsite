package com.example.demo.scripts;

import com.example.demo.model.BrowseHistory;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.repository.BrowseHistoryRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class BrowseHistoryGenerationScript {

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    // 随机生成5~20条浏览记录
    @Transactional
    public void generateBrowseHistoryForProducts() {
        // 指定的商品ID列表
        List<Long> productIds = List.of(2L, 3L, 4L, 5L, 6L, 91L, 92L, 93L, 94L, 95L);

        // 获取所有客户
        List<Customer> customers = customerRepository.findAll();

        Random random = new Random();

        for (Long productId : productIds) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                System.out.println("Product with ID " + productId + " not found.");
                continue;
            }

            // 随机生成5到20条浏览记录
            int browseCount = random.nextInt(16) + 5; // 生成5~20之间的随机数
            for (int i = 0; i < browseCount; i++) {
                // 随机选择一个顾客
                Customer customer = customers.get(random.nextInt(customers.size()));

                // 随机生成浏览时间，在9/30到10/20之间
                LocalDateTime browseTime = generateRandomDateTimeBetween(
                        LocalDateTime.of(2023, 9, 30, 0, 0),
                        LocalDateTime.of(2023, 10, 20, 23, 59)
                );

                // 创建浏览记录
                BrowseHistory browseHistory = new BrowseHistory();
                browseHistory.setCustomer(customer);
                browseHistory.setProduct(product);
                browseHistory.setBrowseTime(browseTime);

                browseHistoryRepository.save(browseHistory);
            }
        }
        System.out.println("Browse history records generated successfully.");
    }

    // 生成在给定日期范围内的随机日期时间
    private LocalDateTime generateRandomDateTimeBetween(LocalDateTime start, LocalDateTime end) {
        Random random = new Random();
        long startEpoch = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpoch = end.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpoch = startEpoch + (long) (random.nextDouble() * (endEpoch - startEpoch));
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
    }
}
