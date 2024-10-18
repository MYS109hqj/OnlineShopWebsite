// 文件路径: com/example/demo/controller/MerchantBrowseHistoryController.java

package com.example.demo.controller;

import com.example.demo.model.BrowseHistory;
import com.example.demo.service.MerchantBrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchants/{merchantId}/browse-history")
public class MerchantBrowseHistoryController {

    @Autowired
    private MerchantBrowseHistoryService browseHistoryService;

    // 获取商家所有商品的浏览记录
    @GetMapping
    public ResponseEntity<List<BrowseHistory>> getBrowseHistoryForMerchant(@PathVariable Long merchantId) {
        List<BrowseHistory> historyList = browseHistoryService.getBrowseHistoryForMerchant(merchantId);
        return ResponseEntity.ok(historyList);
    }

    // 获取商家某个商品的浏览记录
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<BrowseHistory>> getBrowseHistoryForProduct(@PathVariable Long merchantId, @PathVariable Long productId) {
        List<BrowseHistory> historyList = browseHistoryService.getBrowseHistoryForProduct(merchantId, productId);
        return ResponseEntity.ok(historyList);
    }

    
    // 获取单个商品的浏览量
    @GetMapping("/products/{productId}/views")
    public ResponseEntity<Long> getBrowseCountForProduct(@PathVariable Long merchantId, @PathVariable Long productId) {
        Long browseCount = browseHistoryService.getBrowseCountForProduct(productId);
        return ResponseEntity.ok(browseCount);
    }

    // 获取商家所有商品的浏览量统计
    @GetMapping("/total")
    public ResponseEntity<Map<Long, Long>> getProductBrowseCounts(@PathVariable Long merchantId) {
        Map<Long, Long> browseCounts = browseHistoryService.getProductBrowseCounts(merchantId);
        return ResponseEntity.ok(browseCounts);
    }

}
