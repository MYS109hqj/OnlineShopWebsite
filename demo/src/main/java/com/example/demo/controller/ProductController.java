package com.example.demo.controller;

import com.example.demo.exception.ProductDeletionException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
// @CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 获取所有商品或根据商家ID和状态获取商品
    @GetMapping
    public List<Product> getProductsByMerchant(@RequestParam(required = false) Long merchantId,
                                               @RequestParam(required = false) Boolean status) {
        if (merchantId != null && status != null) {
            return productService.getProductsByMerchantIdAndStatus(merchantId, status);
        } else if (merchantId != null) {
            return productService.getProductsByMerchantId(merchantId);
        } else if (status != null) {
            return productService.getProductsByStatus(status);
        } else {
            return productService.getAllProducts();
        }
    }

    // 获取特定商品
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 添加商品
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    // 更新商品
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails);
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("商品删除成功");
        } catch (ProductDeletionException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("商品未找到");
        }
    }
}
