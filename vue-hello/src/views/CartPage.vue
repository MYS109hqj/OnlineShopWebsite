<template>
  <div class="container">
    <h1 class="title">购物车</h1>

    <!-- <div class="form-group">
      <label for="customerSelect" class="label">选择顾客:</label>
      <select v-model="selectedCustomerId" @change="fetchCartByUser" class="select">
        <option value="">请选择顾客</option>
        <option v-for="customer in customers" :key="customer.id" :value="customer.id">
          {{ customer.name }}
        </option>
      </select>
    </div> -->

    <!-- <button @click="fetchCart" class="refresh-btn">刷新购物车列表</button> -->

    <h2 class="subtitle">购物车商品列表</h2>
    
    <div class="cart-item-list">
      <CartItem 
        v-for="item in cartItems" 
        :key="item.id" 
        :item="item" 
        @updateSelection="updateSelection" 
        @remove="removeFromCart"
        @updateQuantity="updateQuantity" />
    </div>

    <div class="cart-actions">
      <button @click="removeSelectedItems" class="clear-btn">删除选中商品</button>
      <button @click="clearCart" class="clear-btn">清空购物车</button>
      <button @click="purchaseSelectedItems" class="checkout-btn">结算选中商品</button>
    </div>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <p>购物车为空。</p>
    </div>

    <div class="total-price">
      <h3>选中商品总价: {{ totalSelectedPrice }} 元</h3>
      <!-- <button @click="getTotalSelectedPrice" class="refresh-price-btn">刷新总价</button> -->
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import CartItem from '../components/CartItem.vue'; 

export default {
  name: 'CartPage',
  props: {
    username: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      customers: [],
      selectedCustomerId: null,
      cartId: null,
      cartItems: [],
      totalSelectedPrice: 0, // 初始化总价
      userInfo: {
        email: '',
        name: '',
        avatar: ''
      },
    };
  },
  components: {
    CartItem,
  },
  created() {
    this.fetchCustomerDetails();
    // this.fetchCartByUser();
    // this.fetchCustomers(); 
  },
  methods: {
    async fetchCustomerDetails() {
      const customerName = this.$route.params.username;
      console.log("customerName is :", customerName);
      try {
        const response = await axios.get(`http://localhost:8080/api/customers/name/${customerName}`);
        if (response.data) {
          console.log(response.data);
          this.userInfo = response.data;
          this.userAvatarUrl = this.userInfo.avatar;
          this.selectedCustomerId = response.data.id;
          console.log("CustomerId is:", this.selectedCustomerId)
        }
      } catch (error) {
        console.error('获取顾客信息失败', error);
      }
      this.fetchCartByUser();
    },
    // async fetchCustomers() {
    //   try {
    //     const response = await axios.get('http://localhost:8080/api/customers/all');
    //     this.customers = response.data;
    //   } catch (error) {
    //     console.error('获取顾客失败', error);
    //   }
    // },
    async fetchCartByUser() {
      if (this.selectedCustomerId) {
        try {
          const response = await axios.get(`http://localhost:8080/cart/customer/${this.selectedCustomerId}`);
          this.cartId = response.data.id;
          this.fetchCart(); 
        } catch (error) {
          console.error('获取购物车失败', error);
        }
      }
    },
    async fetchCart() {
      if (this.cartId) {
        try {
          const response = await axios.get(`http://localhost:8080/cart/${this.cartId}`);
          this.cartItems = response.data.cartItems.map(item => ({
            ...item,
            selected: item.selected || false, // 确保使用服务器返回的选中状态
            quantity: item.quantity || 1, // 确保数量至少为1
          }));
          this.getTotalSelectedPrice(); // 重新计算总价
        } catch (error) {
          console.error('刷新购物车失败', error);
        }
      }
    },
    async updateSelection(itemId, selected) {
      if (this.cartId) {
        try {
          await axios.post(`http://localhost:8080/cart/${this.cartId}/select/${itemId}?selected=${selected}`);

          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('更新选中状态失败', error);
        }
      }
    },
    async updateQuantity(itemId, quantity) {
      if (this.cartId) {
        try {
          await axios.put(`http://localhost:8080/cart/${this.cartId}/update?itemId=${itemId}&quantity=${quantity}`);
          this.fetchCart(); // 更新购物车商品数量

          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('更新商品数量失败', error);
        }
      }
    },
    async removeSelectedItems() {
      if (this.cartId) {
        try {
          await axios.delete(`http://localhost:8080/cart/${this.cartId}/remove/selected`);
          this.fetchCart();

          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('删除选中商品失败', error);
        }
      }
    },
    async removeFromCart(itemId) {
      if (this.cartId) {
        try {
          await axios.delete(`http://localhost:8080/cart/${this.cartId}/remove/${itemId}`);
          this.fetchCart();

          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('删除商品失败', error);
        }
      }
    },
    async clearCart() {
      if (this.cartId) {
        try {
          await axios.delete(`http://localhost:8080/cart/${this.cartId}/clear`);
          this.fetchCart();

          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('清空购物车失败', error);
        }
      }
    },
    async purchaseSelectedItems() {
      if (this.cartId) {
        try {
          await axios.post(`http://localhost:8080/cart/${this.cartId}/purchase`);
          alert('结算成功！');
          this.fetchCart();
          await this.getTotalSelectedPrice();
        } catch (error) {
          console.error('结算失败', error);
        }
      }
    },
    async getTotalSelectedPrice(){
      if (this.cartId) {
        try {
          const response = await axios.get(`http://localhost:8080/cart/${this.cartId}/totalprice`);
          this.totalSelectedPrice = response.data; // 更新总价
          // console.log("当前总价为：", this.totalSelectedPrice);
        } catch (error) {
          console.error('获取购物车总价失败', error);
        }
      }
    }
  },
  computed: {
    computedTotalSelectedPrice() {
      return this.cartItems
        .filter(item => item.selected)
        .reduce((total, item) => total + (item.product.price * item.quantity), 0)
        .toFixed(2);
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.refresh-btn {
  background-color: #4caf50;
  color: white;
  margin-top: 10px;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.refresh-price-btn {
  background-color: #2196F3; /* 蓝色背景 */
  color: white; /* 白色字体 */
  margin-top: 10px; /* 上边距 */
  padding: 8px 12px; /* 内边距 */
  border: none; /* 无边框 */
  border-radius: 4px; /* 圆角 */
  cursor: pointer; /* 鼠标指针 */
  font-size: 14px; /* 字体大小 */
}

.cart-item-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.cart-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.empty-cart {
  text-align: center;
  margin-top: 20px;
}

.total-price {
  margin-top: 20px;
  font-weight: bold;
}
</style>
