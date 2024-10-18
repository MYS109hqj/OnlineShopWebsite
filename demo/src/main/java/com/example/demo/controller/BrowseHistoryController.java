// 文件路径: com/example/demo/controller/BrowseHistoryController.java

package com.example.demo.controller;

import com.example.demo.dto.BrowseHistoryDTO;
import com.example.demo.model.BrowseHistory;
import com.example.demo.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/browse-history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    // 顾客获取自己的浏览历史记录
    @GetMapping
    public ResponseEntity<List<BrowseHistory>> getBrowseHistoryForCustomer(@PathVariable Long customerId) {
        List<BrowseHistory> historyList = browseHistoryService.getBrowseHistoryForCustomer(customerId);
        return ResponseEntity.ok(historyList);
    }

    // 顾客删除某条浏览历史记录
    @DeleteMapping("/{historyId}")
    public ResponseEntity<String> deleteBrowseHistory(@PathVariable Long customerId, @PathVariable Long historyId) {
        browseHistoryService.deleteBrowseHistory(customerId, historyId);
        return ResponseEntity.ok("浏览记录删除成功");
    }

    // 添加浏览记录
    @PostMapping
    public ResponseEntity<String> addBrowseHistory(
            @PathVariable Long customerId,
            @RequestBody BrowseHistoryDTO browseHistory) {
        
        // 调用服务层方法来保存浏览记录
        browseHistoryService.addBrowseHistory(customerId, browseHistory);
        
        return ResponseEntity.ok("浏览记录添加成功");
    }
}
