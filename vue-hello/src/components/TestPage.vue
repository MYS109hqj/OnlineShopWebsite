<template>
  <div class="container">
    <h1 class="title">商家商品管理</h1>

    <!-- 当前商家选择框，用于商品增删改 -->
    <div class="form-group">
      <label for="currentMerchant" class="label">当前登录商家:</label>
      <select v-model="currentMerchantId" @change="fetchCurrentMerchantProducts" class="select">
        <option v-for="merchant in merchants" :key="merchant.id" :value="merchant.id">
          {{ merchant.name }}
        </option>
      </select>
      <button @click="refreshCurrentMerchantProducts" class="refresh-btn">刷新当前商家商品</button>
    </div>

    <!-- 商家选择框，用于查看商品 -->
    <div class="form-group">
      <label for="merchantFilter" class="label">选择商家:</label>
      <select v-model="selectedMerchantId" @change="fetchProducts" class="select">
        <option value="">全部</option>
        <option v-for="merchant in merchants" :key="merchant.id" :value="merchant.id">
          {{ merchant.name }}
        </option>
      </select>
      <button @click="refreshProducts" class="refresh-btn">刷新商品列表</button>
    </div>

    <!-- 刷新商家列表按钮 -->
    <button @click="refreshMerchants" class="refresh-btn">刷新商家列表</button>

    <!-- 商品列表 -->
    <h2 class="subtitle">商品列表</h2>
    <ul class="product-list">
      <li v-for="product in products" :key="product.id" class="product-item">
        <div class="product-info">
          <span class="product-name">{{ product.name }}</span> - 
          <span class="product-price">{{ product.price }} 元</span>
        </div>
        <div class="product-actions">
          <button class="edit-btn" @click="editProduct(product)">编辑</button>
          <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
        </div>
      </li>
    </ul>

    <!-- 添加/编辑商品表单 -->
    <h2 class="subtitle">{{ editingProduct ? '编辑商品' : '添加商品' }}</h2>
    <form @submit.prevent="saveProduct" class="form">
      <div class="form-group">
        <input type="text" v-model="productForm.name" placeholder="商品名称" required class="input" />
      </div>
      <div class="form-group">
        <input type="number" v-model="productForm.price" placeholder="价格" required class="input" step="0.01" />
      </div>
      <div class="form-group">
        <input type="text" v-model="productForm.imageUrl" placeholder="图片 URL" class="input" />
      </div>
      <div class="form-group">
        <textarea v-model="productForm.description" placeholder="描述" class="textarea"></textarea>
      </div>
      <div class="form-group">
        <label for="status" class="label">状态:</label>
        <select v-model="productForm.status" class="select">
          <option value="true">上架</option>
          <option value="false">下架</option>
        </select>
      </div>
      <button type="submit" class="submit-btn">{{ editingProduct ? '更新' : '添加' }}</button>
      <!-- 新增的返回按钮 -->
      <button type="button" @click="goBack" class="return-btn">返回</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      merchants: [],  // 商家列表
      products: [],  // 商品列表
      selectedMerchantId: '',  // 当前选择的商家，用于查看商品
      currentMerchantId: '',  // 当前登录的商家，用于增删改商品
      productForm: {
        name: '',
        price: '',
        imageUrl: '',
        description: '',
        status: 'true',  // 默认商品状态为上架
      },
      editingProduct: null,  // 当前编辑的商品
    };
  },
  created() {
    this.fetchMerchants();  // 初始化获取所有商家
    this.fetchProducts();   // 初始化获取所有商品
  },
  methods: {
    // 获取所有商家
    async fetchMerchants() {
      try {
        const response = await axios.get('http://localhost:8080/api/merchants/all');
        this.merchants = response.data;
      } catch (error) {
        console.error('获取商家失败', error);
      }
    },

    // 刷新商家列表
    refreshMerchants() {
      this.fetchMerchants();
    },

    // 根据选择的商家ID获取商品列表
    async fetchProducts() {
      try {
        const response = await axios.get('http://localhost:8080/api/products', {
          params: {
            merchantId: this.selectedMerchantId || null,  // 如果为空则获取所有商品
          },
        });
        this.products = response.data;
      } catch (error) {
        console.error('获取商品失败', error);
      }
    },

    // 刷新商品列表
    refreshProducts() {
      this.fetchProducts();
    },

    // 获取当前登录商家的商品列表
    async fetchCurrentMerchantProducts() {
      try {
        const response = await axios.get('http://localhost:8080/api/products', {
          params: {
            merchantId: this.currentMerchantId,
          },
        });
        this.products = response.data;
      } catch (error) {
        console.error('获取当前商家的商品失败', error);
      }
    },

    // 刷新当前商家的商品列表
    refreshCurrentMerchantProducts() {
      this.fetchCurrentMerchantProducts();
    },

    // 添加或更新商品
    async saveProduct() {
      try {
        if (!this.currentMerchantId) {
          console.error('请先选择一个商家');
          return;
        }
        
        if (this.editingProduct) {
          // 更新商品
          await axios.put(`http://localhost:8080/api/products/${this.editingProduct.id}`, this.productForm);
        } else {
          // 添加商品
          const productToAdd = { ...this.productForm, merchant: { id: this.currentMerchantId } };
          console.log('当前商家 ID:', this.currentMerchantId);
          console.log('ID的类型:', typeof(this.currentMerchantId));
          await axios.post('http://localhost:8080/api/products', productToAdd);
        }
        this.resetForm();  // 重置表单
        this.fetchCurrentMerchantProducts();  // 刷新当前商家商品列表
      } catch (error) {
        console.error('保存商品失败', error);
      }
    },

    // 编辑商品
    editProduct(product) {
      this.editingProduct = product;
      this.productForm = { ...product };  // 将表单数据设置为商品数据
    },

    // 删除商品
    async deleteProduct(productId) {
      try {
        await axios.delete(`http://localhost:8080/api/products/${productId}`);
        this.fetchCurrentMerchantProducts();  // 刷新当前商家商品列表
      } catch (error) {
        console.error('删除商品失败', error);
      }
    },

    // 返回按钮的方法
    goBack() {
      this.resetForm();  // 重置表单
    },

    // 重置表单
    resetForm() {
      this.editingProduct = null;
      this.productForm = {
        name: '',
        price: '',
        imageUrl: '',
        description: '',
        status: 'true',  // 默认商品状态为上架
      };
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 20px;
}

.subtitle {
  margin-top: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  margin-right: 10px;
}

.select,
.input,
.textarea {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.textarea {
  min-height: 100px;
}

.product-list {
  list-style: none;
  padding: 0;
}

.product-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.product-info {
  font-size: 16px;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn,
.submit-btn,
.refresh-btn,
.return-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-btn {
  background-color: #4caf50;
  color: white;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.submit-btn {
  background-color: #2196f3;
  color: white;
  margin-top: 10px;
  width: 100%;
}

.refresh-btn {
  background-color: #ffa726; /* 刷新按钮的背景颜色 */
  color: white;
  margin-top: 10px; /* 使按钮与下方元素有间距 */
  border: none; /* 去掉边框 */
  cursor: pointer; /* 鼠标悬停时变成手指形状 */
}

.return-btn {
  background-color: #9e9e9e; /* 返回按钮的背景颜色 */
  color: white;
  margin-top: 10px; /* 使按钮与下方元素有间距 */
  width: 100%; /* 使按钮宽度与表单相同 */
}
</style>
