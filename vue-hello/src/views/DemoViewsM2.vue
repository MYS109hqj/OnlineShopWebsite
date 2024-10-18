<template>
  <div>
    <h1>商家后台</h1>
    <p><strong>商家 ID:</strong> {{ merchantId }}</p>
    <p><strong>商家所有商品的总浏览量:</strong> {{ totalViews }}</p>
    <p><strong>商家所有商品的总购买量:</strong> {{ totalPurchaseQuantity }}</p>

    <h2>商品列表</h2>
    <div>
      <h3>上架商品</h3>
      <ul>
        <li v-for="product in availableProducts" :key="product.id" class="product-item">
          <div class="product-info">
            <span class="product-name">{{ product.name }}</span>
            <span class="product-price">{{ product.price }} 元</span>
          </div>
          <div class="product-actions">
            <button @click="fetchBrowseAndPurchaseData(product.id)">查看浏览量和购买量</button>
            <button @click="toggleProductLogs(product.id)">查看浏览日志</button>
          </div>
          <div v-if="currentProductId === product.id && showProductData" class="product-data">
            <p><strong>当前商品的浏览量:</strong> {{ singleProductBrowseCount }}</p>
            <p><strong>当前商品的购买量:</strong> {{ singleProductPurchaseQuantity }}</p>
          </div>
          <div v-if="currentProductId === product.id && showLogs" class="product-logs">
            <h4>浏览日志</h4>
            <ul>
              <li v-for="log in productViewLogs" :key="log.id">
                浏览时间: {{ log.browseTime }}，浏览用户ID: {{ log.customer.id }}
              </li>
            </ul>
            <button @click="closeLogs">关闭日志</button>
          </div>
        </li>
      </ul>

      <h3>下架商品</h3>
      <ul>
        <li v-for="product in unavailableProducts" :key="product.id" class="product-item">
          <div class="product-info">
            <span class="product-name">{{ product.name }}</span>
            <span class="product-price">{{ product.price }} 元</span>
          </div>
          <div class="product-actions">
            <button @click="fetchBrowseAndPurchaseData(product.id)">查看浏览量和购买量</button>
            <button @click="toggleProductLogs(product.id)">查看浏览日志</button>
          </div>
          <div v-if="currentProductId === product.id && showProductData" class="product-data">
            <p><strong>当前商品的浏览量:</strong> {{ singleProductBrowseCount }}</p>
            <p><strong>当前商品的购买量:</strong> {{ singleProductPurchaseQuantity }}</p>
          </div>
          <div v-if="currentProductId === product.id && showLogs" class="product-logs">
            <h4>浏览日志</h4>
            <ul>
              <li v-for="log in productViewLogs" :key="log.id">
                浏览时间: {{ log.browseTime }}，浏览用户ID: {{ log.customer.id }}
              </li>
            </ul>
            <button @click="closeLogs">关闭日志</button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      products: [],                // 所有商品
      availableProducts: [],        // 上架商品
      unavailableProducts: [],      // 下架商品
      merchantId: '',               // 当前商家的ID
      productViewLogs: [],          // 商品的浏览日志
      productTotalViews: 0,         // 商品的总浏览量
      allBrowseLogs: [],            // 所有商品的浏览日志
      totalViews: 0,                // 商家所有商品的总浏览量
      singleProductBrowseCount: 0,  // 单个商品的浏览量
      singleProductPurchaseQuantity: 0,  // 单个商品的购买量
      totalPurchaseQuantity: 0,     // 商家所有商品的总购买量
      currentProductId: null,       // 当前查看的商品ID
      showLogs: false,              // 用于控制日志显示状态
      showProductData: false,       // 用于控制浏览量和购买量显示状态
    };
  },
  
  async created() {
    await this.fetchMerchantId();       // 获取商家ID
    await this.fetchProducts();         // 获取商品列表
    await this.fetchAllBrowseLogs();    // 获取商家所有商品的浏览记录
    await this.fetchTotalViews();       // 获取商家所有商品的总浏览量
    await this.fetchTotalPurchaseQuantity();  // 获取商家所有商品的总购买量
  },

  methods: {
    // 获取商家的ID
    async fetchMerchantId() {
      try {
        const userId = localStorage.getItem('userId');
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

    // 获取浏览量和购买量的合并方法
    async fetchBrowseAndPurchaseData(productId) {
      this.currentProductId = productId;
      this.showLogs = false; // 关闭日志显示
      this.showProductData = true; // 显示浏览量和购买量
      await this.fetchProductBrowseCount(productId);
      await this.fetchProductPurchaseQuantity(productId);
    },

    // 获取单个商品的浏览量
    async fetchProductBrowseCount(productId) {
      if (!this.merchantId || !productId) {
        console.error('商家ID或商品ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history/products/${productId}/views`);
        this.singleProductBrowseCount = response.data;
      } catch (error) {
        console.error('获取商品浏览量失败', error);
      }
    },

    // 获取单个商品的购买量
    async fetchProductPurchaseQuantity(productId) {
      if (!this.merchantId || !productId) {
        console.error('商家ID或商品ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/purchase-quantities/product/${productId}`);
        this.singleProductPurchaseQuantity = response.data;
      } catch (error) {
        console.error('获取商品购买量失败', error);
      }
    },

    // 显示或隐藏商品的浏览日志
    async toggleProductLogs(productId) {
      if (this.currentProductId === productId && this.showLogs) {
        this.showLogs = false;
        return;
      }
      this.currentProductId = productId;
      this.showProductData = false; // 关闭浏览量和购买量显示
      await this.fetchProductBrowseLogs(productId);
      this.showLogs = true;
    },

    // 获取某个商品的浏览日志
    async fetchProductBrowseLogs(productId) {
      if (!this.merchantId || !productId) {
        console.error('商家ID或商品ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history/products/${productId}`);
        this.productViewLogs = response.data;
      } catch (error) {
        console.error('获取商品浏览日志失败', error);
      }
    },

    // 关闭日志显示
    closeLogs() {
      this.showLogs = false;
    },

    // 获取商家所有商品的浏览记录
    async fetchAllBrowseLogs() {
      if (!this.merchantId) {
        console.error('商家ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history`);
        this.allBrowseLogs = response.data;
      } catch (error) {
        console.error('获取所有商品的浏览日志失败', error);
      }
    },

    // 获取商家所有商品的总浏览量
    async fetchTotalViews() {
      if (!this.merchantId) {
        console.error('商家ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history/total`);
        this.totalViews = Object.values(response.data).reduce((sum, count) => sum + count, 0);
      } catch (error) {
        console.error('获取商家所有商品的总浏览量失败', error);
      }
    },

    // 获取商家所有商品的总购买量
    async fetchTotalPurchaseQuantity() {
      if (!this.merchantId) {
        console.error('商家ID未设置');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/purchase-quantities/merchant/${this.merchantId}`);
        this.totalPurchaseQuantity = Object.values(response.data).reduce((sum, count) => sum + count, 0);
      } catch (error) {
        console.error('获取商家所有商品的总购买量失败', error);
      }
    },
  },
};
</script>

<style scoped>
h1, h2, h3 {
  color: #2c3e50;
}

.product-item {
  border: 1px solid #e1e1e1;
  padding: 15px;
  margin: 10px 0;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.product-info {
  display: flex;
  justify-content: space-between;
}

.product-name {
  font-weight: bold;
}

.product-price {
  color: #2c3e50;
}

.product-actions {
  margin: 10px 0;
}

.product-data {
  margin-top: 10px;
  padding: 10px;
  background-color: #e8f5e9;
  border-radius: 5px;
}

.product-logs {
  margin-top: 10px;
  padding: 10px;
  background-color: #fffbcc;
  border-radius: 5px;
  border: 1px solid #ffeb3b;
}

button {
  margin: 5px;
  padding: 8px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #2980b9;
}

button:focus {
  outline: none;
}
</style>
