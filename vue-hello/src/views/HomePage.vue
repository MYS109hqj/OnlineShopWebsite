<template>
  <div>
    <h1>商品列表</h1>
    <div class="product-list">
      <!-- v-for 循环显示产品列表，使用ProductItem单元 -->
      <ProductItem v-for="product in products" :key="product.id" :product="product" />
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import ProductItem from '../components/ProductItem.vue';

export default {
  name: 'HomePage',
  data() {
    return {
      products: [], // 初始化为空数组，待会从后端获取数据
    };
  },
  components: {
    ProductItem,
  },
  created() {
    // 页面加载时，获取上架商品
    this.fetchAvailableProducts();
  },
  methods: {
    // 获取上架商品的请求
    async fetchAvailableProducts() {
      try {
        // 发送请求获取上架的商品
        const response = await axios.get('http://localhost:8080/api/products', {
          params: { status: true }, // 只请求状态为true的商品
        });
        this.products = response.data; // 将获取到的商品列表赋值到 products 数组中
      } catch (error) {
        console.error('获取上架商品失败', error);
      }
    },
  },
};
</script>

<style>
.product-list {
  display: grid; /* 使用 grid 布局 */
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* 自适应列数 */
  gap: 20px; /* 列之间的间距 */
}

.product-item {
  border: 1px solid #ddd; /* 可选：边框 */
  padding: 10px; /* 可选：内边距 */
  border-radius: 8px; /* 可选：圆角 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 可选：阴影 */
}
</style>
