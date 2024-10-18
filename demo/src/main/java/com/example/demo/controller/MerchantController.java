package com.example.demo.controller;

import com.example.demo.model.Merchant;
import com.example.demo.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    // 添加商家
    @PostMapping
    public ResponseEntity<Merchant> addMerchant(@RequestBody Merchant merchant) {
        Merchant createdMerchant = merchantService.addMerchant(merchant);
        return ResponseEntity.status(201).body(createdMerchant); // 201 Created
    }

    // 更新商家
    @PutMapping("/{id}")
    public ResponseEntity<Merchant> updateMerchant(@PathVariable Long id, @RequestBody Merchant updatedMerchant) {
        Merchant updatedMerchantEntity = merchantService.updateMerchant(id, updatedMerchant);
        return ResponseEntity.ok(updatedMerchantEntity); // 200 OK
    }

    // 删除商家
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 获取所有商家
    @GetMapping("/all")
    public ResponseEntity<List<Merchant>> getAllMerchants() {
        List<Merchant> merchants = merchantService.getAllMerchants();
        return ResponseEntity.ok(merchants); // 200 OK
    }

    // 获取特定商家信息
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        Merchant merchant = merchantService.getMerchantById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(merchant); // 200 OK
    }

    // 根据 userId 获取商家 ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Long> getMerchantIdByUserId(@PathVariable String userId) {
        Long userIdLong = Long.valueOf(userId);
        Long merchantId = merchantService.getMerchantIdByUserId(userIdLong);
        if (merchantId == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(merchantId); // 200 OK
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Merchant> getMerchantByName(@PathVariable String name) {
        Merchant merchant = merchantService.getMerchantByName(name);
        if (merchant != null) {
            return ResponseEntity.ok(merchant); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // @GetMapping("/user/{userId}")
    // public ResponseEntity<Long> getMerchantIdByUserId(@PathVariable Long userId) {
    //     Long merchantId = merchantService.getMerchantIdByUserId(userId);
    //     if (merchantId == null) {
    //         return ResponseEntity.notFound().build(); // 404 Not Found
    //     }
    //     return ResponseEntity.ok(merchantId); // 200 OK
    // }
}
