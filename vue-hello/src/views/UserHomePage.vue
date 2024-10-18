<template>
  <div class="user-home">
    <div class="role-indicator">
      <span v-if="role === 'SELLER'" class="role-text">商家页面</span>
      <span v-else-if="role === 'USER'" class="role-text">用户页面</span>
    </div>

    <div class="user-info">
      <img :src="userAvatarUrl" alt="User Avatar" class="user-avatar" />
      <span class="user-name">{{ username }}</span>
    </div>

    <!-- <button v-if="isOwner" @click="showEditForm = true" class="edit-btn">编辑个人信息</button> -->

    <div v-if="showEditForm" class="edit-form">
      <h3>编辑个人信息</h3>
      <form @submit.prevent="updateUserInfo">
        <div>
          <label for="email">邮箱:</label>
          <input type="email" v-model="userInfo.email" required />
        </div>
        <div>
          <label for="name">用户名:</label>
          <input type="text" v-model="userInfo.name" required />
        </div>
        <div>
          <label for="avatar">头像 URL:</label>
          <input type="text" v-model="userInfo.avatar" />
        </div>
        <button type="submit">保存</button>
        <button type="button" @click="showEditForm = false">取消</button>
      </form>
    </div>

    <button v-if="isOwner && role === 'SELLER'" @click="goToProductManagement" class="manage-btn">商品管理</button>

    <div v-if="role === 'SELLER'">
      <h2>商品列表</h2>
      <div v-if="products.length === 0" class="no-products">没有商品可显示。</div>
      <div v-else class="product-list">
        <!-- 同HomePage，交给商品单元组件处理 -->
        <ProductItemSellerPage v-for="product in products" :key="product.id" :product="product" />
      </div>
    </div>

    <div v-else-if="role === 'USER'">
      <h2>我的订单</h2>
      <div>
        <DemoOrderPageC/>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import ProductItemSellerPage from '../components/ProductItemSellerPage.vue';
import DemoOrderPageC from '../views/DemoOrderPageC.vue'

export default {
  props: {
    username: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      products: [],
      orders: ['<订单>'],
      loggedInUser: null,
      role: '',
      merchantId: '',
      showProductDetails: false,
      selectedProduct: {},
      userAvatarUrl: 'https://i0.hippopx.com/photos/227/22/547/wave-atlantic-pacific-ocean-thumb.jpg',
      showEditForm: false,
      userInfo: {
        email: '',
        name: '',
        avatar: ''
      },
      quantity: 1, // 新增数量
    };
  },
  components: {
    ProductItemSellerPage,
    DemoOrderPageC
  },
  computed: {
    isOwner() {
      return this.username === this.loggedInUser;
    },
  },
  async created() {
    this.loggedInUser = localStorage.getItem('username');
    this.role = this.$route.query.role;
    this.targettedUsername = this.username;

    if (this.role === 'SELLER') {
      // await this.fetchMerchantId();
      await this.fetchMerchantDetails();
      await this.fetchSellerProducts();
    } else if (this.role === 'USER') {
      await this.fetchUserInfo();
      this.orders = ['<订单>', '<订单>', '<订单>'];
    }

    // await this.fetchMerchantDetails();
  },
  methods: {
    // async fetchMerchantId() {
    //   const userId = localStorage.getItem('userId');
    //   try {
    //     const response = await axios.get(`http://localhost:8080/api/merchants/user/${userId}`);
    //     this.merchantId = response.data;
    //   } catch (error) {
    //     console.error('获取商家ID失败', error);
    //   }
    // },

    async fetchMerchantDetails() {
      const merchantName = this.$route.params.username;
      // console.log("merchantName is :", merchantName);
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/name/${merchantName}`);
        if (response.data) {
          // console.log(response.data);
          this.userInfo = response.data;
          this.userAvatarUrl = this.userInfo.avatar;
          this.merchantId = response.data.id;
          // console.log("merchantid is:",this.merchantId)
        }
      } catch (error) {
        console.error('获取商家信息失败', error);
      }
    },

    async fetchSellerProducts() {
      if (!this.merchantId) return;
      try {
        const response = await axios.get('http://localhost:8080/api/products', {
          params: { merchantId: this.merchantId, status: true },
        });
        this.products = response.data;
      } catch (error) {
        console.error('获取商家商品失败', error);
      }
    },

    async fetchUserInfo() {
      const userId = localStorage.getItem('userId');
      try {
        const response = await axios.get(`http://localhost:8080/api/users/${userId}`);
        this.userInfo = response.data;
        this.userAvatarUrl = this.userInfo.avatar;
      } catch (error) {
        console.error('获取用户信息失败', error);
      }
    },

    async updateUserInfo() {
      const userId = localStorage.getItem('userId');
      try {
        await axios.put(`http://localhost:8080/api/users/${userId}`, this.userInfo);
        this.showEditForm = false;
        await this.fetchUserInfo();
      } catch (error) {
        console.error('更新用户信息失败', error);
      }
    },

    goToProductManagement() {
      this.$router.push('/product-management');
    },
  },
};
</script>

<style scoped>
.user-home {
  padding: 20px;
}

.role-indicator {
  margin-bottom: 10px;
}

.role-text {
  font-weight: bold;
  font-size: 16px;
  color: #555;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
}

.edit-btn {
  margin-bottom: 20px;
}

.edit-form {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.edit-form label {
  display: block;
  margin-bottom: 5px;
}

.edit-form input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
}

.manage-btn {
  margin-bottom: 20px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-item {
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  text-align: center;
}
</style>
