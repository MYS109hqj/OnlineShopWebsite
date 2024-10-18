package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.service.CartService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 创建购物车
    @PostMapping("/create/{customerId}")
    public Cart createCart(@PathVariable Long customerId) {
        return cartService.createCart(customerId);
    }

    // 通过customerId获取购物车信息
    @GetMapping("/customer/{customerId}")
    public Cart getCartByCustomerId(@PathVariable Long customerId) {
        return cartService.getCartByCustomerId(customerId);
    }

    // cartId添加商品
    @PostMapping("/{cartId}/add")
    public Cart addToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        return cartService.addToCart(cartId, productId, quantity);
    }

    // cartId itemId quantity更新商品数量
    @PutMapping("/{cartId}/update")
    public Cart updateItemQuantity(@PathVariable Long cartId,
                                    @RequestParam Long itemId,
                                    @RequestParam int quantity) {
        return cartService.updateItemQuantity(cartId, itemId, quantity);
    }

    // cartId查看购物车
    @GetMapping("/{cartId}")
    public Cart viewCart(@PathVariable Long cartId) {
        return cartService.viewCart(cartId);
    }

    // cartId查看购物车选中商品总价
    @GetMapping("/{cartId}/totalprice")
    public BigDecimal getCartTotalPrice(@PathVariable Long cartId){
        return cartService.getCartTotalPrice(cartId);
    }

    @DeleteMapping("/{cartId}/remove/{itemId}")
    public void removeItem(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartService.removeItem(cartId, itemId);
    }

    @DeleteMapping("/{cartId}/remove/selected")
    public void removeSelectedItems(@PathVariable Long cartId) {
        cartService.removeSelectedItems(cartId);
    }

    @DeleteMapping("/{cartId}/clear")
    public void clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
    }

    @PostMapping("/{cartId}/select/{itemId}")
    public void selectItem(@PathVariable Long cartId, @PathVariable Long itemId, @RequestParam boolean selected) {
        cartService.selectItem(cartId, itemId, selected);
    }

    // @RequestParam需要通过?name=$(value)在请求中传递
    @PostMapping("/{cartId}/select/all")
    public void selectAll(@PathVariable Long cartId, @RequestParam boolean selected) {
        cartService.selectAll(cartId, selected);
    }

    @PostMapping("/{cartId}/purchase")
    public void purchaseSelectedItems(@PathVariable Long cartId) {
        cartService.purchaseSelectedItems(cartId);
    }
}
