<template>
  <div class="container">
    <h1>提交商品数据</h1>

    <div v-if="isLoggedIn" class="login-status">
      <p>{{ loginTypeText }}：{{ loggedInUser }}</p>
      <button @click="logout">退出登录</button>
    </div>
    <div v-else>
      <p>请先登录后再提交商品数据。</p>
    </div>

    <div v-if="isLoggedIn" class="file-upload-section">
      <input type="file" @change="handleFileUpload" accept=".csv" />
      <button @click="submitToBackend">提交商品数据</button>
    </div>
  </div>
</template>

<script>
import Papa from 'papaparse';

export default {
  data() {
    return {
      products: [],
      loggedInUser: null,
      isLoggedIn: false,
      loginTypeText: ''
    };
  },
  mounted() {
    const user = localStorage.getItem('user');
    const seller = localStorage.getItem('seller');
    const username = localStorage.getItem('username');

    if (user) {
      this.loggedInUser = username || user;
      this.loginTypeText = '当前用户登录';
      this.isLoggedIn = true;
    } else if (seller) {
      this.loggedInUser = username || seller;
      this.loginTypeText = '当前商户登录';
      this.isLoggedIn = true;
    } else {
      this.isLoggedIn = false;
    }
  },
  methods: {
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        Papa.parse(file, {
          header: true,
          complete: (result) => {
            this.products = result.data.map((item, index) => ({
              id: index + 1,
              name: item.name,
              price: parseFloat(item.price),
              imageUrl: item.imageUrl || 'https://via.placeholder.com/150'
            }));
          }
        });
      }
    },
    submitToBackend() {
      if (this.isLoggedIn) {
        console.log('提交商品数据到后端：', this.products);
        // 模拟向后端提交数据
      } else {
        alert('请先登录再提交数据！');
      }
    },
    logout() {
      localStorage.removeItem('user');
      localStorage.removeItem('seller');
      this.loggedInUser = null;
      this.isLoggedIn = false;
      this.loginTypeText = '';
      alert('您已退出登录');
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.login-status {
  font-weight: bold;
}

.file-upload-section {
  margin-top: 10px;
}

button {
  padding: 10px 15px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #36976b;
}
</style>
