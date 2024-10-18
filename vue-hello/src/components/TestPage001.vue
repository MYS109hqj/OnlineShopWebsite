<template>
    <div class="dev-merchant-test">
      <h2>开发测试：商家商品编辑界面</h2>
      <!-- 同样的表单编辑组件 -->
      <form @submit.prevent="saveProduct" class="form">
        <input type="text" v-model="productForm.name" placeholder="商品名称" required class="input" />
        <input type="number" v-model="productForm.price" placeholder="价格" required class="input" step="0.01" />
        <input type="text" v-model="productForm.imageUrl" placeholder="图片 URL" class="input" />
        <textarea v-model="productForm.description" placeholder="描述" class="textarea"></textarea>
        <label for="status">状态:</label>
        <select v-model="productForm.status" class="select">
          <option value="true">上架</option>
          <option value="false">下架</option>
        </select>
        <button type="submit" class="submit-btn">{{ editingProduct ? '更新' : '添加' }}</button>
        <button type="button" @click="resetForm" class="return-btn">取消</button>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
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
    methods: {
      async saveProduct() {
        // 商品保存逻辑
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
  .dev-merchant-test {
    padding: 20px;
    background-color: #f9f9f9;
  }
  </style>
  