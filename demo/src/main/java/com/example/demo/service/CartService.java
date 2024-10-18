package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Customer;
import com.example.demo.model.Merchant;
import com.example.demo.model.Product;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderRepository;
// import com.example.demo.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Cart createCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found for ID: " + customerId));
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }

    public Cart addToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found!"));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(cart);

        cartItem = cartItemRepository.save(cartItem);
        cart.getCartItems().add(cartItem);

        cart.calculateTotalPrice();
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(Long cartId, Long itemId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("购物车未找到"));

        // 查找购物车中指定的商品项
        Optional<CartItem> itemToUpdateOpt = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(itemId)) // 使用 itemId 查找
                .findFirst();

        if (itemToUpdateOpt.isPresent()) {
            CartItem itemToUpdate = itemToUpdateOpt.get();
            itemToUpdate.setQuantity(quantity); // 更新数量
            
            // 计算购物车总价
            cart.calculateTotalPrice();
            
            cartRepository.save(cart); // 保存更新后的购物车
            return cart; // 返回更新后的购物车
        } else {
            throw new RuntimeException("购物车中未找到该商品");
        }
    }

    public Cart viewCart(Long cartId) {
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    public BigDecimal getCartTotalPrice(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
        return cart.getTotalPrice();
    }

    public void removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        cart.getCartItems().removeIf(item -> item.getId().equals(itemId));
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    public void removeSelectedItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        cart.getCartItems().removeIf(CartItem::isSelected);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        cart.clearCart();
        cartRepository.save(cart);
    }

    public void selectItem(Long cartId, Long itemId, boolean selected) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        cart.getCartItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .ifPresent(item -> item.setSelected(selected));

        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    public void selectAll(Long cartId, boolean selected) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        cart.getCartItems().forEach(item -> item.setSelected(selected));
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    // 购买购物车中的已选商品，并为不同商家的商品生成多个订单
    public void purchaseSelectedItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));

        // 获取购物车中所有被选中的商品
        List<CartItem> selectedItems = cart.getCartItems().stream()
            .filter(CartItem::isSelected)
            .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No items selected for purchase!");
        }

        // 按商家分组商品项
        Map<Merchant, List<CartItem>> itemsGroupedByMerchant = selectedItems.stream()
            .collect(Collectors.groupingBy(item -> item.getProduct().getMerchant()));

        // 为每个商家创建订单
        List<Order> orders = new ArrayList<>();
        for (Map.Entry<Merchant, List<CartItem>> entry : itemsGroupedByMerchant.entrySet()) {
            Merchant merchant = entry.getKey();
            List<CartItem> merchantItems = entry.getValue();

            Order order = createOrderFromMerchantItems(cartId, merchant, merchantItems);
            orders.add(order);
        }

        // 保存所有订单
        orders.forEach(orderRepository::save);

        // 移除已选中的购物车项
        cart.removeSelectedItems();
        cartRepository.save(cart);
    }

    // 根据商家和商品项创建订单
    private Order createOrderFromMerchantItems(Long cartId, Merchant merchant, List<CartItem> merchantItems) {
        Order order = new Order();
    
        // 设置订单的顾客和商家
        order.setCustomer(getCustomerFromCart(cartId));  // 从购物车获取顾客
        order.setMerchant(merchant);  // 设置商家
    
        // 创建订单项并添加到订单
        List<OrderItem> orderItems = merchantItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity()); // 确保 quantity 正确设置
            orderItem.setTotalPrice(cartItem.getTotalPrice()); // 确保 totalPrice 正确设置
            orderItem.setOrder(order);  // 关联订单
    
            // // 输出每个订单项的信息
            // System.out.println("Order Item Created: ");
            // System.out.println("Product: " + cartItem.getProduct().getName());
            // System.out.println("Quantity: " + cartItem.getQuantity());
            // System.out.println("Total Price: " + cartItem.getTotalPrice());
            
            return orderItem;
        }).collect(Collectors.toList());
    
        order.setOrderItems(orderItems); // 设定订单项列表
        order.calculateTotalPrice(); // 计算总价
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT); // 设置订单状态为待付款
    
        return order;
    }
    
    
    // 购物车id -> 顾客实体（外键）
    public Customer getCustomerFromCart(Long cartId) {
        // 获取购物车实体
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found!"));
    
        // 返回顾客
        return cart.getCustomer();  
    }
    

    public Cart getCartByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found for ID: " + customerId));
        return customer.getCart();
    }
}
