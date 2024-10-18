<template>
  <div>
    <h1>商家客户管理</h1>

    <!-- 筛选是否有进行中的订单的选项 -->
    <div class="filter-section">
      <label for="ongoing-filter">筛选进行中订单:</label>
      <select v-model="ongoingFilter" @change="applyFilters">
        <option value="ALL">所有顾客</option>
        <option value="HAS_ONGOING">有进行中的订单</option>
        <option value="NO_ONGOING">没有进行中的订单</option>
      </select>
    </div>

    <!-- 顾客列表 -->
    <div>
      <h2>下过订单的顾客</h2>
      <div v-if="filteredCustomers.length === 0">尚无符合条件的顾客</div>
      <ul v-else>
        <li v-for="customer in filteredCustomers" :key="customer.id" class="customer-card">
          <p>顾客id: {{ customer.id }}</p>
          <p>顾客名称: {{ customer.name }}</p>
          <p>邮箱: {{ customer.email }}</p>

          <!-- 显示进行中的订单提示 -->
          <p v-if="customer.hasOngoingOrders" class="ongoing-orders-msg">
            该顾客有进行中的订单
          </p>
          <p v-else class="no-ongoing-orders-msg">
            该顾客没有进行中的订单
          </p>

          <!-- 按钮显示或隐藏顾客的所有订单 -->
          <button @click="toggleCustomerOrders(customer.id)">
            {{ selectedid === customer.id ? '隐藏订单' : '查看订单' }}
          </button>

          <!-- 显示顾客的订单 -->
          <div v-if="selectedid === customer.id && customerOrders.length > 0" class="customer-orders">
            <h3>订单列表</h3>

            <!-- 筛选订单状态 -->
            <div class="order-filter-section">
              <label for="order-status-filter">筛选订单状态:</label>
              <select v-model="orderFilter" @change="applyOrderFilter">
                <option value="ALL">所有订单</option>
                <option value="PENDING_PAYMENT">待付款</option>
                <option value="PAID">已付款</option>
                <option value="SHIPPED">已发货</option>
                <option value="AWAITING_RECEIPT">待收货</option>
                <option value="CANCELED">已取消</option>
                <option value="RECEIVED">已收货</option>
              </select>
            </div>

            <!-- 订单列表 -->
            <ul>
              <li v-for="order in filteredOrders" :key="order.orderId">
                <p>订单 ID: {{ order.orderId }}</p>
                <p>状态: {{ getOrderStatusDescription(order.orderStatus) }}</p>
                <p>总价: {{ order.totalPrice }} 元</p>
                <p>订单日期: {{ order.orderDate }}</p>
              </li>
            </ul>
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
      customers: [], // 商家的顾客列表
      customerOrders: [], // 当前选中顾客的订单
      filteredOrders: [], // 筛选后的订单列表
      filteredCustomers: [], // 筛选后的顾客列表
      selectedid: null, // 当前选中的顾客 ID
      hasOngoingOrders: false, // 是否有进行中订单
      merchantId: null, // 存储商家 ID
      ongoingFilter: 'ALL', // 默认显示所有顾客
      orderFilter: 'ALL', // 订单筛选选项，默认显示所有订单
    };
  },
  created() {
    this.fetchMerchantId(); // 获取商家 ID 并加载顾客列表
  },
  methods: {
    async fetchMerchantId() {
      try {
        const userId = localStorage.getItem('userId');
        if (!userId) {
          console.error('userId 未找到');
          return;
        }
        const response = await axios.get(`http://localhost:8080/api/merchants/user/${userId}`);
        this.merchantId = response.data;
        this.fetchCustomers(); // 获取顾客列表
      } catch (error) {
        console.error('获取商家信息失败', error);
      }
    },

    // 获取商家所有下过订单的顾客
    async fetchCustomers() {
      try {
        const response = await axios.get(`http://localhost:8080/api/merchant/${this.merchantId}/customers`);
        this.customers = response.data;

        // 对所有顾客执行“查看是否有进行中的订单”的逻辑
        await Promise.all(
          this.customers.map(async (customer) => {
            const response = await axios.get(
              `http://localhost:8080/api/merchant/${this.merchantId}/customers/${customer.id}/ongoing-orders`
            );
            customer.hasOngoingOrders = response.data;
          })
        );

        this.applyFilters(); // 过滤顾客列表
      } catch (error) {
        console.error('获取顾客列表失败', error);
      }
    },

    // 筛选顾客列表
    applyFilters() {
      if (this.ongoingFilter === 'ALL') {
        this.filteredCustomers = [...this.customers];
      } else if (this.ongoingFilter === 'HAS_ONGOING') {
        this.filteredCustomers = this.customers.filter((customer) => customer.hasOngoingOrders);
      } else if (this.ongoingFilter === 'NO_ONGOING') {
        this.filteredCustomers = this.customers.filter((customer) => !customer.hasOngoingOrders);
      }
    },

    // 获取顾客的订单
    async fetchCustomerOrders(id) {
      try {
        this.selectedid = id; // 设置选中顾客 ID
        const response = await axios.get(`http://localhost:8080/api/merchant/${this.merchantId}/customers/${id}/orders`);
        this.customerOrders = response.data;

        this.applyOrderFilter(); // 应用订单状态筛选
      } catch (error) {
        console.error('获取顾客订单失败', error);
      }
    },

    // 切换订单列表的显示/隐藏
    toggleCustomerOrders(id) {
      if (this.selectedid === id) {
        this.selectedid = null; // 隐藏订单
      } else {
        this.fetchCustomerOrders(id); // 显示订单
      }
    },

    // 获取订单状态的描述
    getOrderStatusDescription(status) {
      const statusMap = {
        PENDING_PAYMENT: '待付款',
        PAID: '已付款',
        SHIPPED: '已发货',
        AWAITING_RECEIPT: '待收货',
        CANCELED: '已取消',
        RECEIVED: '已收货',
      };
      return statusMap[status] || '未知状态';
    },

    // 订单状态筛选
    applyOrderFilter() {
      if (this.orderFilter === 'ALL') {
        this.filteredOrders = [...this.customerOrders];
      } else {
        this.filteredOrders = this.customerOrders.filter(order => order.orderStatus === this.orderFilter);
      }
    },
  },
};
</script>

<style scoped>
.customer-card {
  border: 1px solid #d3d3d3;
  background-color: #f9f9f9;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.customer-orders {
  margin-top: 20px;
  border-top: 1px solid #e0e0e0;
  padding-top: 10px;
}

.ongoing-orders-msg {
  color: green;
}

.no-ongoing-orders-msg {
  color: red;
}
</style>
