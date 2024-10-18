<template>
  <div>
    <h1>商家订单管理</h1>

    <!-- 筛选和排序选项 -->
    <div class="filter-section">
      <label for="status-filter">按状态筛选:</label>
      <select v-model="selectedStatus" @change="applyFilters">
        <option value="ALL">所有订单</option>
        <option value="PENDING_PAYMENT">待付款</option>
        <option value="PAID">已付款</option>
        <option value="SHIPPED">已发货</option>
        <option value="AWAITING_RECEIPT">待收货</option>
        <option value="RECEIVED">已收货</option>
        <option value="CANCELED">已取消</option>
        <option value="INCOMPLETE">未完成</option>
      </select>

      <label for="sort-order">排序:</label>
      <select v-model="sortOrder" @change="applyFilters">
        <option value="desc">由新到旧</option>
        <option value="asc">由旧到新</option>
      </select>
    </div>

    <div v-if="filteredOrders.length === 0">没有符合条件的订单</div>

    <ul v-else>
      <li v-for="order in filteredOrders" :key="order.orderId" class="order-card">
        <div class="order-header">
          <p>订单 ID: {{ order.orderId }}</p>
          <p>顾客: {{ order.customer.name }}</p>
          <p>状态: {{ getOrderStatusDescription(order.orderStatus) }}</p>
          <p>总价: {{ order.totalPrice }} 元</p>
        </div>

        <div class="products-list">
          <!-- 使用 orderItems 显示订单商品 -->
          <OrderDetailItem
            v-for="(item, index) in order.orderItems"
            :key="index"
            :product="item.product"
            :quantity="item.quantity"
          />
        </div>

        <!-- 商家操作按钮 -->
        <div class="order-actions">
          <button v-if="order.orderStatus === 'PAID'" @click="shipOrder(order.orderId)">发货</button>
          <button v-if="order.orderStatus === 'SHIPPED'" @click="markAsAwaitingReceipt(order.orderId)">标记为送达</button>
          <button v-if="order.orderStatus !== 'RECEIVED'" @click="cancelOrder(order.orderId)">取消订单</button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';
import OrderDetailItem from '../components/OrderDetailItem.vue'; // 导入新组件

export default {
  props: {
    username: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      orders: [], // 所有订单数据
      filteredOrders: [], // 筛选后的订单数据
      merchantId: null, // 用于存储商家 ID
      sortOrder: 'desc', // 默认排序由新到旧
      selectedStatus: 'ALL', // 默认显示所有订单
    };
  },
  created() {
    this.fetchMerchantId(); // 在组件创建时获取商家 ID
  },
  methods: {
    async fetchMerchantId() {
      try {
        const response = await axios.get(`http://localhost:8080/api/merchants/name/${this.$route.params.username}`);
        this.merchantId = response.data.id;
        this.fetchMerchantOrders(); // 成功获取商家 ID 后获取订单
      } catch (error) {
        console.error('获取商家信息失败', error);
      }
    },
    async fetchMerchantOrders() {
      if (!this.merchantId) {
        console.log('Error in fetchMerchantOrders: merchantId is undefined!');
        return;
      }
      try {
        const { data } = await axios.get(`http://localhost:8080/api/orders/merchant/${this.merchantId}`);
        this.orders = data;
        this.applyFilters(); // 获取订单数据后应用默认筛选和排序
      } catch (error) {
        console.error('获取商家订单失败', error);
      }
    },
    applyFilters() {
      let filtered = [...this.orders];

      // 根据订单状态进行筛选
      if (this.selectedStatus !== 'ALL') {
        if (this.selectedStatus === 'INCOMPLETE') {
          // 筛选未完成订单
          filtered = filtered.filter(
            (order) => order.orderStatus !== 'RECEIVED' && order.orderStatus !== 'CANCELED'
          );
        } else {
          filtered = filtered.filter((order) => order.orderStatus === this.selectedStatus);
        }
      }

      // 根据订单日期排序 (使用 orderDate)
      filtered.sort((a, b) => {
        const dateA = new Date(a.orderDate);
        const dateB = new Date(b.orderDate);
        return this.sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
      });

      this.filteredOrders = filtered; // 更新筛选后的订单数据
    },
    async shipOrder(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/ship`);
        this.fetchMerchantOrders(); // 发货成功后刷新订单列表
        alert(`订单 ${orderId} 已发货`);
      } catch (error) {
        console.error('发货失败', error);
      }
    },
    async markAsAwaitingReceipt(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/awaiting-receipt`);
        this.fetchMerchantOrders(); // 标记成功后刷新订单列表
        alert(`订单 ${orderId} 已标记为送达`);
      } catch (error) {
        console.error('标记为送达失败', error);
      }
    },
    async cancelOrder(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/cancel`);
        this.fetchMerchantOrders(); // 取消成功后刷新订单列表
        alert(`订单 ${orderId} 已取消`);
      } catch (error) {
        console.error('取消订单失败', error);
      }
    },
    getOrderStatusDescription(status) {
      // 根据 orderStatus 返回对应的描述
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
  },
  components: {
    OrderDetailItem, // 注册新组件
  },
};
</script>

<style scoped>
/* 筛选和排序选项的样式 */
.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
}

/* 原有的样式保持不变 */
.order-card {
  border: 1px solid #d3d3d3;
  background-color: #f9f9f9;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  margin-bottom: 20px;
}

.products-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.order-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.order-actions button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.order-actions button:hover {
  background-color: #45a049;
}

.order-actions button:last-child {
  background-color: #f44336;
}

.order-actions button:last-child:hover {
  background-color: #e53935;
}
</style>
