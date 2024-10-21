<template>
  <div>
    <!-- 商品单元的模块 -->
    <div class="product-item" @click="viewDetails">
      <img :src="product.imageUrl || defaultImage" alt="商品图片" class="product-image" />
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-price">{{ product.price }} 元</p>
      <p class="product-seller">
        <img :src="product.merchant.avatar" alt="商家头像" class="seller-avatar" />
        <a :href="`http://localhost:8081/user-home/${product.merchant.name}?role=SELLER`" class="seller-name" title="进入商家主页">{{ product.merchant.name }}</a>
      </p> 
    </div>

    <!-- 商品详细信息模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeDetails">
      <div class="modal-content">
        <h2>{{ product.name }}</h2>
        <img :src="product.imageUrl || defaultImage" alt="商品图片" class="modal-image" />
        <p>价格：{{ product.price }} 元</p>
        <p>详细信息：{{ product.description }}</p>
        
        <!-- 数量选择 -->
        <div class="quantity-selector">
          <button @click="decreaseQuantity" class="quantity-button">-</button>
          <input type="number" v-model="quantity" class="quantity-input" min="1" />
          <button @click="increaseQuantity" class="quantity-button">+</button>
        </div>
        
        <button @click="addToCart" class="add-to-cart-button">添加到购物车</button>
        <button @click="closeDetails" class="close-button">返回商品列表</button>
      </div>
    </div>

    <!-- 添加到购物车模态框 -->
    <div v-if="showAddToCartModal" class="modal" @click.self="showAddToCartModal = false">
      <div class="modal-content">
        <p>{{ addToCartMessage }}</p>
        <button @click="showAddToCartModal = false" class="close-button">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ProductItem',
  props: {
    product: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      defaultImage: 'https://via.placeholder.com/150',
      showModal: false,
      showAddToCartModal: false, // 控制添加到购物车模态框的显示
      addToCartMessage: '', // 存储添加到购物车结果消息
      quantity: 1, // 默认数量为1
    };
  },
  methods: {
    async recordBrowseHistory() {
      const role = localStorage.getItem('role');
      if( role !== "USER"){
        console.log("访问用户非顾客");
        return;
      }
      
      const customerId = localStorage.getItem('customerId'); // 从 localStorage 获取顾客ID
      if (customerId && this.product.id) {
        try {
          console.log(customerId, this.product.id);
          await axios.post(`http://localhost:8080/api/customers/${customerId}/browse-history`, {
            productId: this.product.id,
          });
          console.log('浏览记录已保存');
        } catch (error) {
          console.error('保存浏览记录失败', error);
        }
      } else {
        console.error('顾客ID或产品ID未找到');
      }
    },
    viewDetails() {
      this.showModal = true;
      this.recordBrowseHistory(); // 在查看商品详情时记录浏览历史
    },
    closeDetails() {
      this.showModal = false;
      this.resetQuantity(); // 关闭时重置数量
    },
    increaseQuantity() {
      this.quantity++;
    },
    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--;
      }
    },
    resetQuantity() {
      this.quantity = 1; // 重置数量为1
    },
    async addToCart() {
      try {
        let cartId = localStorage.getItem('cartId');
        let role = localStorage.getItem('role');
        if(role == "SELLER"){
          this.addToCartMessage = '当前为商家用户，没有购物功能';
          this.showAddToCartModal = true; // 显示添加失败的模态框
          return;
        }else if(!role){
          this.addToCartMessage = '未登录或登录失败，请先登录';
          this.showAddToCartModal = true; // 显示添加失败的模态框
          return;
        }
        if (!cartId) {
          let userId = localStorage.getItem('userId');
          await this.viewCart(userId);
          cartId = localStorage.getItem('cartId');
        }
        // 使用查询参数传递 productId 和 quantity，null表示请求体为空，post需要请求体的输入
        const response = await axios.post(`http://localhost:8080/cart/${cartId}/add`, null, {
          params: {
            productId: this.product.id,
            quantity: this.quantity
          }
        });

        // 检查响应状态
        if (response.status === 200) {
          this.addToCartMessage = '商品已添加到购物车'; // 设置成功消息
        } else {
            this.addToCartMessage = '添加到购物车失败，请重试'; // 设置失败消息
        }
        this.showAddToCartModal = true; // 显示添加结果的模态框
        this.closeDetails(); // 关闭商品详情模态框
      } catch (error) {
        console.error('添加到购物车失败', error);
        this.addToCartMessage = '添加到购物车失败，请重试'; // 设置失败消息
        this.showAddToCartModal = true; // 显示添加失败的模态框
      }
    },
    async viewCart(userId) {
      try {
        // 先尝试获取购物车
        const response = await axios.get(`http://localhost:8080/cart/user/${userId}`);
        if (response.data && response.data.id) {
          // 如果购物车存在，则存储 ID
          localStorage.setItem('cartId', response.data.id);
          console.log('Cart retrieved:', response.data.id);
          return; // 退出函数
        }
      } catch (error) {
        // 打印详细的错误信息
        console.error('Error viewing cart:', error.response ? error.response.data : error.message);
      }
    }
  },
}
</script>


<style scoped>
.product-item {
  width: 200px;
  padding: 16px;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  cursor: pointer;
  text-align: center;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 10px;
}

.product-name {
  font-size: 18px;
  margin: 10px 0;
  color: #333;
}

.product-price {
  font-size: 16px;
  color: #ff5722;
  font-weight: bold;
}

.product-seller {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 5px;
}

.seller-name {
  color: #007bff;
  text-decoration: underline;
  cursor: pointer;
}

.seller-name:hover {
  color: #0056b3;
}

.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000; /* 确保模态框在最上层 */
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  max-width: 500px; /* 设置最大宽度 */
  width: 90%; /* 设置为 90% 以适应响应式设计 */
  text-align: center;
  overflow-y: auto; /* 如果内容超出最大高度，允许滚动 */
  max-height: 80vh; /* 限制模态框的最大高度 */
}

.modal-image {
  width: 100%; /* 设置宽度为 100% */
  height: auto; /* 高度自适应 */
  max-height: 300px; /* 限制最大高度，避免图片过大 */
  object-fit: contain; /* 确保图片等比例缩放，并保持在容器内 */
  display: block; /* 确保图片为块级元素，避免下边距 */
  margin: 0 auto; /* 居中显示图片 */
}

.close-button {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #ff5722;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.close-button:hover {
  background-color: #e64a19;
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px 0;
}

.quantity-button {
  padding: 10px;
  background-color: #ff5722;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.quantity-input {
  width: 50px;
  text-align: center;
  margin: 0 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.add-to-cart-button {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-to-cart-button:hover {
  background-color: #45a049;
}
</style>
