package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Merchant;
import com.example.demo.model.Customer; // 引入 Customer 模型
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MerchantService;
import com.example.demo.service.CustomerService; // 引入 CustomerService
import com.example.demo.service.UserService; // 引入 UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CustomerService customerService; // 注入 CustomerService

    @Autowired
    private UserService userService; // 注入 UserService

    // 注册接口：支持普通用户（顾客）和商家注册
    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user, @RequestParam String email) {
        // 检查用户名是否存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "用户名已存在"));
        }

        // 保存用户
        User savedUser = userRepository.save(user);

        // 创建响应体
        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getId()); // 返回用户 ID
        response.put("role", savedUser.getRole()); // 返回角色

        // 如果是商家注册，保存商家信息
        if ("SELLER".equals(user.getRole())) {
            Merchant merchant = new Merchant();
            merchant.setName(savedUser.getUsername()); // 使用用户名作为商家名称
            merchant.setUser(savedUser); // 直接将 User 对象设置为商家
            merchantService.addMerchant(merchant);
        }

        // 如果是顾客注册，保存顾客信息
        if ("USER".equals(user.getRole())) {
            Customer customer = new Customer();
            customer.setName(savedUser.getUsername());
            customer.setEmail(email);
            customer.setUserId(savedUser.getId());
            customerService.addCustomer(customer);
        }

        return ResponseEntity.ok(response); // 返回用户 ID 和角色
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "用户名或密码错误"));
        }
        if (user.getRole() != null && !user.getRole().equals(existingUser.getRole())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "角色不匹配"));
        }

        // 创建响应体
        Map<String, Object> response = new HashMap<>();
        response.put("userId", existingUser.getId()); // 返回用户 ID
        response.put("role", existingUser.getRole()); // 返回角色

        return ResponseEntity.ok(response); // 返回用户 ID 和角色
    }

    // 注销接口
    @DeleteMapping("/deactive")
    public ResponseEntity<String> deactive(@RequestHeader("userId") Long userId) {
        String result = userService.deleteUser(userId); // 调用 UserService 的注销逻辑
        return result.equals("用户注销成功") 
            ? ResponseEntity.ok(result) 
            : ResponseEntity.badRequest().body(result);
    }
}
