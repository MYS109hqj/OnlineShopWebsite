<template>
  <div>
    <h1>商家后台</h1>
    <p><strong>商家 ID:</strong> {{ merchantId }}</p>
    <p><strong>商家所有商品的总浏览量:</strong> {{ totalViews }}</p>

    <h2>商品列表</h2>
    <div>
      <h3>上架商品</h3>
      <ul>
        <li v-for="product in availableProducts" :key="product.id">
          {{ product.name }} - {{ product.price }} 元
          <button @click="fetchProductBrowseLogs(product.id)">查看浏览日志</button>
          <button @click="fetchProductBrowseCount(product.id)">查看浏览量</button> <!-- 新增按钮 -->
        </li>
      </ul>

      <h3>下架商品</h3>
      <ul>
        <li v-for="product in unavailableProducts" :key="product.id">
          {{ product.name }} - {{ product.price }} 元
          <button @click="fetchProductBrowseLogs(product.id)">查看浏览日志</button>
          <button @click="fetchProductBrowseCount(product.id)">查看浏览量</button> <!-- 新增按钮 -->
        </li>
      </ul>
    </div>

    <div v-if="productViewLogs.length > 0">
      <h2>商品的浏览日志</h2>
      <ul>
        <li v-for="log in productViewLogs" :key="log.id">
          浏览时间: {{ log.browseTime }}，浏览用户ID: {{ log.customer.id }}
        </li>
      </ul>
    </div>

    <div v-if="singleProductBrowseCount >= 0">
      <p><strong>当前商品的浏览量:</strong> {{ singleProductBrowseCount }}</p> <!-- 显示单个商品的浏览量 -->
    </div>

    <div v-if="allBrowseLogs.length > 0">
      <h2>商家所有商品的浏览日志</h2>
      <ul>
        <li v-for="log in allBrowseLogs" :key="log.id">
          商品ID: {{ log.product.id }}，浏览时间: {{ log.browseTime }}，浏览用户ID: {{ log.customer.id }}
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
        singleProductBrowseCount: 0,  // 存储单个商品的浏览量
      };
    },
    
    async created() {
      await this.fetchMerchantId();   // 在组件创建时获取 merchantId
      await this.fetchProducts();     // 获取商品列表
      await this.fetchAllBrowseLogs();// 获取商家所有商品的浏览记录
      await this.fetchTotalViews();   // 获取商家所有商品的总浏览量
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
  
      // 获取单个商品的浏览量
      async fetchProductBrowseCount(productId) {
            if (!this.merchantId || !productId) {
                console.error('商家ID或商品ID未设置');
                return;
            }
            try {
                const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history/products/${productId}/views`);
                this.singleProductBrowseCount = response.data; // 更新单个商品浏览量
            } catch (error) {
                console.error('获取商品浏览量失败', error);
            }
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
      async fetchTotalViews() {
          if (!this.merchantId) {
              console.error('商家ID未设置');
              return;
          }
          try {
              const response = await axios.get(`http://localhost:8080/api/merchants/${this.merchantId}/browse-history/total`);
              this.totalViews = Object.values(response.data).reduce((sum, count) => sum + count, 0); // 计算总浏览量
              this.productBrowseCounts = response.data; // 存储每个商品的浏览量
          } catch (error) {
              console.error('获取商家所有商品总浏览量失败', error);
          }
      },
    }
  };
  </script>
  
  <style scoped>
  h1, h2 {
    color: #2c3e50;
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
  </style>
  