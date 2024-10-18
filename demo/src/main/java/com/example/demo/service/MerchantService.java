package com.example.demo.service;

import com.example.demo.model.Merchant;
import com.example.demo.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    // 添加商家
    public Merchant addMerchant(Merchant merchant) {
        // 检查商家名称是否已存在
        Optional<Merchant> existingMerchant = merchantRepository.findByName(merchant.getName());
        if (existingMerchant.isPresent()) {
            throw new IllegalArgumentException("商家名称已存在");
        }
        return merchantRepository.save(merchant);
    }

    // 更新商家信息
    public Merchant updateMerchant(Long id, Merchant updatedMerchant) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(id);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            merchant.setName(updatedMerchant.getName());
            merchant.setAvatar(updatedMerchant.getAvatar());
            return merchantRepository.save(merchant);
        } else {
            throw new IllegalArgumentException("商家不存在");
        }
    }

    // 根据商家 ID 获取商家
    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id).orElse(null);
    }

    // 获取所有商家
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    // 删除商家
    public void deleteMerchant(Long id) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(id);
        if (merchantOpt.isPresent()) {
            merchantRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("商家不存在，无法删除");
        }
    }

    // 根据 userId 获取商家
    public Merchant getMerchantByUserId(Long userId) {
        return merchantRepository.findByUserId(userId).orElse(null);
    }

    // 根据 userId 获取商家 ID
    public Long getMerchantIdByUserId(Long userId) {
        Merchant merchant = getMerchantByUserId(userId);
        return (merchant != null) ? merchant.getId() : null; // 如果找到了商家，返回其 ID，否则返回 null
    }

    // 根据商家的name获取商家信息
    public Merchant getMerchantByName(String name) {
        return merchantRepository.findByName(name).orElse(null);
    }

}
