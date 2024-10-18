<template>
    <div>
      <h1>Merchant API Test</h1>
  
      <button @click="fetchAllMerchants">获取所有商家</button>
      <div v-if="merchants.length">
        <h2>商家列表:</h2>
        <ul>
          <li v-for="merchant in merchants" :key="merchant.id">{{ merchant.name }}</li>
        </ul>
      </div>
  
      <hr />
  
      <h2>添加商家</h2>
      <input v-model="newMerchant.name" placeholder="商家名称" />
      <button @click="addMerchant">添加</button>
  
      <hr />
  
      <h2>更新商家</h2>
      <input v-model="updateMerchantId" placeholder="商家ID" />
      <input v-model="updatedMerchant.name" placeholder="新商家名称" />
      <button @click="updateMerchant">更新</button>
  
      <hr />
  
      <h2>删除商家</h2>
      <input v-model="deleteMerchantId" placeholder="商家ID" />
      <button @click="deleteMerchant">删除</button>
  
      <hr />
  
      <h2>获取特定商家信息</h2>
      <input v-model="fetchMerchantId" placeholder="商家ID" />
      <button @click="getMerchantById">获取商家信息</button>
      <div v-if="merchant">
        <h3>商家信息:</h3>
        <p>名称: {{ merchant.name }}</p>
        <p>ID: {{ merchant.id }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        merchants: [],
        newMerchant: {
          name: ''
        },
        updateMerchantId: '',
        updatedMerchant: {
          name: ''
        },
        deleteMerchantId: '',
        fetchMerchantId: '', // 用于输入获取商家的 ID
        merchant: null // 存储获取的特定商家信息
      };
    },
    methods: {
      async fetchAllMerchants() {
        try {
          const response = await axios.get('http://localhost:8080/api/merchants/all');
          this.merchants = response.data;
        } catch (error) {
          console.error('获取商家列表失败:', error);
        }
      },
      async addMerchant() {
        try {
          const response = await axios.post('http://localhost:8080/api/merchants', this.newMerchant);
          console.log('添加商家成功:', response.data);
          this.fetchAllMerchants(); // 刷新商家列表
        } catch (error) {
          console.error('添加商家失败:', error);
        }
      },
      async updateMerchant() {
        try {
          const response = await axios.put(`http://localhost:8080/api/merchants/${this.updateMerchantId}`, this.updatedMerchant);
          console.log('更新商家成功:', response.data);
          this.fetchAllMerchants(); // 刷新商家列表
        } catch (error) {
          console.error('更新商家失败:', error);
        }
      },
      async deleteMerchant() {
        try {
          await axios.delete(`http://localhost:8080/api/merchants/${this.deleteMerchantId}`);
          console.log('删除商家成功');
          this.fetchAllMerchants(); // 刷新商家列表
        } catch (error) {
          console.error('删除商家失败:', error);
        }
      },
      async getMerchantById() {
        try {
          const response = await axios.get(`http://localhost:8080/api/merchants/${this.fetchMerchantId}`);
          this.merchant = response.data; // 将获取的商家信息赋值给 merchant
        } catch (error) {
          console.error('获取商家信息失败:', error);
          this.merchant = null; // 清空商家信息
        }
      }
    }
  };
  </script>
  
  <style scoped>
  h1 {
    font-size: 2em;
  }
  button {
    margin: 5px;
  }
  input {
    margin: 5px;
  }
  </style>
  