<template>
  <div class="container">
    <h1 class="title">商品管理</h1>

    <div class="view-toggle">
      <button @click="isGridView = true" :class="{ active: isGridView }">方格视图</button>
      <button @click="isGridView = false" :class="{ active: !isGridView }">表格视图</button>
    </div>

    <!-- 上架商品 -->
    <h2 class="subtitle">上架商品</h2>
    <div v-if="availableProducts.length === 0" class="no-products">
      <p>当前无上架商品。</p>
    </div>

    <div v-if="isGridView && availableProducts.length > 0" class="grid-view">
      <div v-for="product in availableProducts" :key="product.id" class="grid-item">
        <img :src="product.imageUrl" alt="Product Image" class="product-image" />
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        <p class="product-price">{{ product.price }} 元</p>
        <p class="product-status">状态: 上架</p>
        <p class="product-modified">最后编辑: {{ formatDate(product.modifiedAt) }}</p>
        <div class="product-actions">
          <button class="edit-btn" @click="editProduct(product)">编辑</button>
          <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-else-if="!isGridView && availableProducts.length > 0" class="table-view">
      <table>
        <thead>
          <tr>
            <th>商品名称</th>
            <th>图片</th>
            <th>详细信息</th>
            <th>价格</th>
            <th>状态</th>
            <th>最后编辑</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in availableProducts" :key="product.id">
            <td>{{ product.name }}</td>
            <td><img :src="product.imageUrl" alt="Product Image" class="table-image" /></td>
            <td>{{ product.description }}</td>
            <td>{{ product.price }} 元</td>
            <td>上架</td>
            <td>{{ formatDate(product.modifiedAt) }}</td>
            <td>
              <button class="edit-btn" @click="editProduct(product)">编辑</button>
              <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 下架商品 -->
    <h2 class="subtitle">下架商品</h2>
    <div v-if="unavailableProducts.length === 0" class="no-products">
      <p>当前无下架商品。</p>
    </div>

    <div v-if="isGridView && unavailableProducts.length > 0" class="grid-view">
      <div v-for="product in unavailableProducts" :key="product.id" class="grid-item">
        <img :src="product.imageUrl" alt="Product Image" class="product-image" />
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        <p class="product-price">{{ product.price }} 元</p>
        <p class="product-status">状态: 下架</p>
        <p class="product-modified">最后编辑: {{ formatDate(product.modifiedAt) }}</p>
        <div class="product-actions">
          <button class="edit-btn" @click="editProduct(product)">编辑</button>
          <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-else-if="!isGridView && unavailableProducts.length > 0" class="table-view">
      <table>
        <thead>
          <tr>
            <th>商品名称</th>
            <th>图片</th>
            <th>详细信息</th>
            <th>价格</th>
            <th>状态</th>
            <th>最后编辑</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in unavailableProducts" :key="product.id">
            <td>{{ product.name }}</td>
            <td><img :src="product.imageUrl" alt="Product Image" class="table-image" /></td>
            <td>{{ product.description }}</td>
            <td>{{ product.price }} 元</td>
            <td>下架</td>
            <td>{{ formatDate(product.modifiedAt) }}</td>
            <td>
              <button class="edit-btn" @click="editProduct(product)">编辑</button>
              <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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
      products: [], // 所有商品
      availableProducts: [], // 上架商品
      unavailableProducts: [], // 下架商品
      merchantId: '', // 当前商家的ID
      productForm: {
        name: '',
        price: '',
        imageUrl: '',
        description: '',
        status: 'true',
      },
      editingProduct: null,
      isGridView: true, // 用于切换视图
    };
  },
  async created() {
    await this.fetchMerchantId(); // 在创建组件时获取 merchantId
    await this.fetchProducts(); // 获取商品列表
  },
  methods: {
    // 获取商家的ID
    async fetchMerchantId() {
      try {
        const userId = localStorage.getItem('userId'); // 从 localStorage 获取 userId
        if (!userId) {
          console.error('userId 未找到');
          return;
        }

        const response = await axios.get(`http://localhost:8080/api/merchants/user/${userId}`);
        this.merchantId = response.data;
      } catch (error) {
        console.error('获取商家ID失败', error);
      }
    },

    // 获取商品列表
    async fetchProducts() {
      if (!this.merchantId) {
        console.error('商家ID未设置');
        return;
      }
      try {
        const response = await axios.get('http://localhost:8080/api/products', {
          params: { merchantId: this.merchantId },
        });
        this.products = response.data;
        this.availableProducts = this.products.filter((product) => product.status === true);
        this.unavailableProducts = this.products.filter((product) => product.status === false);
      } catch (error) {
        console.error('获取商品失败', error);
      }
    },

    // 保存或更新商品
    async saveProduct() {
      try {
        if (this.editingProduct) {
          await axios.put(`http://localhost:8080/api/products/${this.editingProduct.id}`, this.productForm);
        } else {
          await axios.post('http://localhost:8080/api/products', { ...this.productForm, merchant: { id: this.merchantId } });
        }
        this.resetForm();
        this.fetchProducts();
      } catch (error) {
        console.error('保存商品失败', error);
      }
    },

  // 编辑商品
  editProduct(product) {
  this.editingProduct = product;
  this.productForm = { ...product };

  // 等待 Vue 的 DOM 更新完成后再滚动到表单
  this.$nextTick(() => {
    const formElement = this.$el.querySelector('.form');
    if (formElement) {
      formElement.scrollIntoView({ behavior: 'smooth' });
    }
  });
},


    // 删除商品
    async deleteProduct(productId) {
      try {
        await axios.delete(`http://localhost:8080/api/products/${productId}`);
        this.fetchProducts();
      } catch (error) {
        if (error.response && error.response.status === 409) {
          // 显示错误原因，例如商品与订单关联
          console.error('删除商品失败:', error.response.data);
          alert('删除商品失败: ' + error.response.data); // 弹出详细的错误信息
        } else if (error.response && error.response.status === 404) {
          console.error('商品未找到');
          alert('商品未找到');
        } else {
          console.error('删除商品失败:', error);
          alert('删除商品失败');
        }
      }
    },

    // 重置表单
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

    // 格式化日期
    formatDate(dateString) {
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 1200px; /* Increased maximum width */
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 2rem;
  margin-bottom: 20px;
  text-align: center;
}

.subtitle {
  font-size: 1.5rem;
  margin: 20px 0 10px;
}

.view-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.view-toggle button {
  margin: 0 10px;
  padding: 10px 15px;
  font-size: 1rem;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
}

.view-toggle button.active {
  background-color: #0056b3;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.grid-item {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: white;
  text-align: center;
}

.product-image {
  width: 100%;
  height: auto;
}

.product-name {
  font-size: 1.2rem;
  margin: 10px 0;
}

.product-description {
  font-size: 0.9rem;
  color: #555;
}

.product-price {
  font-size: 1.1rem;
  color: #28a745;
}

.product-status,
.product-modified {
  font-size: 0.8rem;
  color: #888;
}

.product-actions {
  margin-top: 10px;
}

.edit-btn,
.delete-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.edit-btn {
  background-color: #28a745;
  color: white;
}

.delete-btn {
  background-color: #dc3545;
  color: white;
  margin-left: 5px;
}

.table-view {
  width: 100%;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
}

.table-image {
  width: 50px;
  height: 50px;
}

.form {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
}

.form-group {
  margin-bottom: 15px;
}

.input, .textarea, .select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.textarea {
  height: 100px;
}

.submit-btn, .return-btn {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn {
  background-color: #007bff;
  color: white;
}

.return-btn {
  background-color: #6c757d;
  color: white;
  margin-left: 10px;
}
</style>
