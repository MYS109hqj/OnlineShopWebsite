<template>
    <div class="container">
      <h1 class="title">当前商家商品管理</h1>
      
      <div class="merchant-info">
        <label for="merchantSelect" class="label">当前商家:</label>
        <select v-model="currentMerchantId" class="select" @change="fetchCurrentMerchantProducts">
          <option v-for="merchant in merchants" :key="merchant.id" :value="merchant.id">
            {{ merchant.name }}
          </option>
        </select>
        <button @click="fetchCurrentMerchantProducts" class="refresh-btn">刷新商品列表</button>
      </div>
  
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
        <button type="button" @click="resetForm" class="return-btn">返回</button>
      </form>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        merchants: [],
        products: [],
        currentMerchantId: '',
        productForm: {
          name: '',
          price: '',
          imageUrl: '',
          description: '',
          status: 'true',
        },
        editingProduct: null,
      };
    },
    created() {
      this.fetchMerchants();
      this.fetchCurrentMerchantProducts();
    },
    methods: {
      async fetchMerchants() {
        try {
          const response = await axios.get('http://localhost:8080/api/merchants/all');
          this.merchants = response.data;
        } catch (error) {
          console.error('获取商家失败', error);
        }
      },
      async fetchCurrentMerchantProducts() {
        try {
          const response = await axios.get('http://localhost:8080/api/products', {
            params: { merchantId: this.currentMerchantId },
          });
          this.products = response.data;
        } catch (error) {
          console.error('获取当前商家的商品失败', error);
        }
      },
      async saveProduct() {
        try {
          if (this.editingProduct) {
            await axios.put(`http://localhost:8080/api/products/${this.editingProduct.id}`, this.productForm);
          } else {
            const productToAdd = { ...this.productForm, merchant: { id: this.currentMerchantId } };
            await axios.post('http://localhost:8080/api/products', productToAdd);
          }
          this.resetForm();
          this.fetchCurrentMerchantProducts();
        } catch (error) {
          console.error('保存商品失败', error);
        }
      },
      editProduct(product) {
        this.editingProduct = product;
        this.productForm = { ...product };
      },
      async deleteProduct(productId) {
        try {
          await axios.delete(`http://localhost:8080/api/products/${productId}`);
          this.fetchCurrentMerchantProducts();
        } catch (error) {
          console.error('删除商品失败', error);
        }
      },
      resetForm() {
        this.editingProduct = null;
        this.productForm = {
          name: '',
          price: '',
          imageUrl: '',
          description: '',
          status: 'true',
        };
      },
    },
  };
  </script>
  
  <style scoped>
  /* 样式和上面的示例一致 */
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
    background-color: #f9f9f9;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  /* 其他样式... */
  </style>
  