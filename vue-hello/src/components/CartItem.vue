<template>
    <div class="cart-item">
      <div class="product-info">
        <img :src="item.product.imageUrl" alt="商品图片" class="product-image" />
        <input type="checkbox" v-model="isSelected" @change="handleSelectionChange" />
        <div class="product-details">
          <span class="product-name">{{ item.product.name }}</span>
          <span class="product-price">{{ item.product.price }} 元</span>
          <div class="product-quantity">
            <input 
              type="number" 
              :value="item.quantity" 
              @input="updateQuantity($event.target.value)" 
              min="1" 
              class="quantity-input"
            />
          </div>
          <span class="current-total">当前总价: {{ currentTotal }} 元</span>
        </div>
      </div>
      <div class="product-actions">
        <button class="delete-btn" @click="handleRemove">删除</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'CartItem',
    props: {
      item: {
        type: Object,
        required: true,
      },
    },
    computed: {
      isSelected: {
        get() {
          return this.item.selected; // 只读取 item 的 selected 属性
        },
        set(value) {
          this.$emit('updateSelection', this.item.id, value); // 通过 emit 通知父组件
        },
      },
      currentTotal() {
        return (this.item.product.price * this.item.quantity).toFixed(2);
      },
    },
    methods: {
      updateQuantity(quantity) {
        const qty = Math.max(1, Number(quantity)); // 确保数量至少为 1
        if (qty !== this.item.quantity) {
          this.$emit('updateQuantity', this.item.id, qty); // 通过 emit 通知父组件
        }
      },
      handleRemove() {
        this.$emit('remove', this.item.id); // 通过 emit 通知父组件
      },
    },
  };
  </script>
  
  <style scoped>
  .cart-item {
    border: 1px solid #ddd;
    padding: 10px;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 10px; /* 增加商品间距 */
  }
  
  .product-info {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-grow: 1; /* 允许商品信息区域扩展 */
  }
  
  .product-image {
    width: 80px; /* 设置图片宽度 */
    height: 80px; /* 设置图片高度 */
    object-fit: cover; /* 保持图片比例 */
    border-radius: 4px; /* 圆角效果 */
  }
  
  .product-details {
    flex-grow: 1; /* 允许商品详细信息区域扩展 */
    display: flex;
    flex-direction: column;
  }
  
  .product-quantity {
    margin-top: 5px; /* 量输入框与价格的间距 */
  }
  
  .quantity-input {
    width: 60px; /* 设置数量输入框宽度 */
    text-align: center; /* 让数字居中 */
  }
  
  .product-actions {
    display: flex;
  }
  
  .delete-btn {
    background-color: #f44336;
    color: white;
    padding: 5px 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  </style>
  