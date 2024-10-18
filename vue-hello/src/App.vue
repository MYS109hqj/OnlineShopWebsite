<template>
  <div id="app">
    <header>
      <nav>
        <router-link to="/">商品列表</router-link>
        <!-- <router-link to="/submit">提交商品数据</router-link> -->
        

        <div class="user-info">
          <span v-if="isLoggedIn">
            当前{{ role === 'USER' ? '用户' : '商户' }}：
            <router-link :to="{ name: 'UserHome', params: { username: username }, query: { role: role} }">{{ username }}</router-link>
            <button @click="logout">退出登录</button>
            
            <div v-if="role === 'USER'">
              <nav>
                <router-link :to="{ name: 'OrderPageC', params: { username: username }}">订单状态</router-link>
                <router-link :to="{ name: 'CartPage', params: { username: username }}">购物车</router-link>
              </nav>
            </div>
            <div v-else>
              <nav>
                <router-link to="/product-management">产品管理</router-link>
                <router-link :to="{ name: 'OrderPageM', params: { username: username }}">订单管理</router-link>
                <!-- <router-link to="/DemoViewsM">浏览日志</router-link> -->
                <router-link to="/DemoViewsM2">浏览日志</router-link>
                <router-link to="/SalesReport">查看报表</router-link>
                <router-link to="/CustomerManagement">客户管理</router-link>
                
              </nav>
            </div>

          </span>
          <span v-else>未登录 </span>
          <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
        </div>
        
      </nav>
    </header>

    <router-view />
  </div>
</template>

<script>
import eventBus from './eventBus'; // 引入事件总线

export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false,
      role: null,
      username: null
    };
  },
  mounted() {
    // 立即检查登录状态
    this.checkLoginStatus();

    // 监听登录成功事件
    eventBus.on('login-success', ({ isLoggedIn, role, username }) => {
      this.isLoggedIn = isLoggedIn; // 更新登录状态
      this.role = role; // 更新角色
      this.username = username; // 更新用户名
    });
  },
  beforeUnmount() {
    // 组件卸载前移除监听器
    eventBus.off('login-success');
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('user') || localStorage.getItem('seller');
      const role = localStorage.getItem('role');
      const username = localStorage.getItem('username');

      if (token && role && username) {
        this.isLoggedIn = true;
        this.role = role;
        this.username = username;
      } else {
        this.isLoggedIn = false;
        this.role = null;
        this.username = null;
      }
    },
    logout() {
      localStorage.removeItem('user');
      localStorage.removeItem('seller');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      this.isLoggedIn = false;
      this.role = null;
      this.username = null;
      this.$router.push('/');
    }
  }
};
</script>

<style>
/* 顶部导航栏样式 */
header {
  display: flex;
  justify-content: flex-end; /* 右对齐 */
  background-color: #f8f9fa;
  padding: 10px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

nav {
  display: flex;
  gap: 20px; /* 导航链接之间的间距 */
  align-items: center; /* 垂直居中 */
}

nav a {
  text-decoration: none;
  color: #007bff;
  font-weight: bold;
}

nav a:hover {
  text-decoration: underline;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info button {
  margin-left: 10px;
}

/* 页面内容的样式 */
#app {
  padding: 20px;
}
</style>
