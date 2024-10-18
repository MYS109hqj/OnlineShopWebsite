<template>
  <div class="container">
      <h1>{{ isLoginMode ? (role === 'USER' ? '用户登录' : '商家登录') : (role === 'USER' ? '用户注册' : '商家注册') }}</h1>

      <div class="role-selection">
          <label>选择角色:</label>
          <select v-model="role">
              <option value="USER">用户</option>
              <option value="SELLER">商家</option>
          </select>
      </div>

      <form @submit.prevent="isLoginMode ? login() : register()" class="auth-form">
          <div class="form-group">
              <label>用户名</label>
              <input v-model="username" type="text" required />
          </div>
          <div v-if="!isLoginMode && role==='USER'" class="form-group">
              <label>邮箱</label>
              <input v-model="email" type="email" required />
          </div>
          <div class="form-group">
              <label>密码</label>
              <input v-model="password" type="password" required />
          </div>
          <div v-if="!isLoginMode" class="form-group">
              <label>确认密码</label>
              <input v-model="confirmPassword" type="password" required />
          </div>
          <button type="submit">{{ isLoginMode ? '登录' : '注册' }}</button>
      </form>

      <button @click="toggleMode" class="toggle-button">
          {{ isLoginMode ? '注册新账号' : '登录已有账号' }}
      </button>
  </div>
</template>

<script>
import axios from 'axios';
import eventBus from '../eventBus'; // 引入事件总线

export default {
  data() {
      return {
          isLoginMode: true,
          username: '',
          password: '',
          confirmPassword: '',
          role: 'USER',
          CustomerId: '',
          email: '',
      };
  },
  methods: {
    async fetchCustomerDetails(customerName) {
      console.log("customerName is :", customerName);
      try {
        const response = await axios.get(`http://localhost:8080/api/customers/name/${customerName}`);
        if (response.data) {
          console.log(response.data);
          this.CustomerId = response.data.id;
          console.log("LRpage: CustomerId is:", this.CustomerId);
          localStorage.setItem('customerId', this.CustomerId);
          console.log(typeof(this.CustomerId));
          await this.createCart();
        }
      } catch (error) {
        console.error('LRpage: 获取顾客信息失败', error);
      }
    },
    async createCart() {
      try {
          // 先尝试获取购物车
          const response = await axios.get(`http://localhost:8080/cart/customer/${this.CustomerId}`);
          if (response.data && response.data.id) {
              // 如果购物车存在，则存储 ID
              localStorage.setItem('cartId', response.data.id);
              console.log('Cart retrieved:', response.data.id);
              return; // 退出函数
          }
      } catch (error) {
          // 打印详细的错误信息
          console.error('Error viewing cart:', error.response ? error.response.data : error.message);
      }

      try {
          // 如果未找到购物车，创建一个新的购物车
          const response = await axios.post(`http://localhost:8080/cart/create/${this.CustomerId}`);
          if (response.data && response.data.id) {
              localStorage.setItem('cartId', response.data.id);
              console.log('Cart created:', response.data.id);
          } else {
              console.error('Failed to create cart, no ID returned:', response.data);
          }
      } catch (error) {
          console.error('Error creating cart:', error.response ? error.response.data : error.message);
      }
  },
    async login() {
        if (this.username && this.password) {
            try {
                const response = await axios.post('http://localhost:8080/auth/login', {
                    username: this.username,
                    password: this.password,
                    role: this.role
                });

                // 存储用户信息到 localStorage
                localStorage.setItem('userId', response.data.userId); // 存储用户 ID
                localStorage.setItem(this.role.toLowerCase(), response.data.token); // 确保 token 存储
                localStorage.setItem('username', this.username);
                localStorage.setItem('role', this.role);

                console.log(`${this.role === 'USER' ? '用户' : '商家'}登录成功：`, this.username);

                // 触发登录事件

                if( this.role === 'USER'){
                    // 如果是顾客，创建、获取购物车
                    this.fetchCustomerDetails(this.username);
                    // this.createCart();
                    console.log("创建/获取了购物车");
                }
                else{
                    localStorage.setItem('cartId', 0);
                    console.log("商家登录，无关购物车功能");
                }
                // 默认转到主页面（所有商品列表）
                this.$router.push('/');

                // 使用事件总线通知登录成功
                eventBus.emit('login-success', {
                    isLoggedIn: true,
                    role: this.role,
                    username: this.username
                });
            } catch (error) {
                console.log(error.response);
                alert('登录失败，请检查您的用户名和密码');
            }
        } else {
            alert('请输入有效的用户名和密码');
        }
    },
    async register() {
        if (this.password !== this.confirmPassword) {
            alert('两次输入的密码不一致！');
            return;
        }

        if (this.username && this.password) {
            try {
                console.log(this.email);
                const response = await axios.post(`http://localhost:8080/auth/register?email=${this.email}`, {
                    username: this.username,
                    password: this.password,
                    role: this.role // 注册时包含角色信息
                });

                // 存储用户信息到 localStorage
                localStorage.setItem('userId', response.data.userId); // 存储用户 ID
                alert('注册成功，请登录');
                this.isLoginMode = true;
            } catch (error) {
                alert('注册失败，请重试');
            }
          } else {
            alert('请填写完整的注册信息');
          }
      },
    toggleMode() {
        this.isLoginMode = !this.isLoginMode;
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 20px;
}

.role-selection {
  margin-bottom: 15px;
}

.auth-form {
  width: 100%;
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 10px;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
input[type="email"] {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
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

.toggle-button {
  margin-top: 10px;
  background-color: #f1c40f;
}
</style>
