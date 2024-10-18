package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // 添加顾客
    public Customer addCustomer(Customer customer) {
        // 这里可以添加其他验证逻辑
        return customerRepository.save(customer);
    }

    // 更新顾客信息
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setName(updatedCustomer.getName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setAvatar(updatedCustomer.getAvatar());
            // 这里可以添加其他字段的更新逻辑
            return customerRepository.save(customer);
        } else {
            throw new IllegalArgumentException("顾客不存在");
        }
    }

    // 删除顾客
    public void deleteCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            customerRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("顾客不存在，无法删除");
        }
    }

    // 获取所有顾客
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // 根据 ID 获取顾客
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // 根据用户名获取顾客
    public Customer getCustomerByName(String name) {
        return customerRepository.findByName(name).orElse(null);
    }
}
