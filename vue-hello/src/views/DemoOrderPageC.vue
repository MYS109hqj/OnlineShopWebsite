<template>
  <div>
    <h1>顾客订单管理</h1>

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
          <p>商家: {{ order.merchant.name }}</p>
          <p>状态: {{ getOrderStatusDescription(order.orderStatus) }}</p>
          <p>总价: {{ order.totalPrice }} 元</p>
          <p>订单日期: {{ formatOrderDate(order.orderDate) }}</p>
        </div>

        <div class="products-list">
          <!-- 商品展示 -->
          <OrderDetailItem
            v-for="(item, index) in order.orderItems"
            :key="index"
            :product="item.product"
            :quantity="item.quantity"
          />
        </div>

        <div class="order-actions">
          <button v-if="order.orderStatus === 'PENDING_PAYMENT'" @click="payOrder(order.orderId)">支付订单</button>
          <button v-if="order.orderStatus === 'AWAITING_RECEIPT'" @click="confirmReceipt(order.orderId)">确认收货</button>
          <button v-if="order.orderStatus === 'PENDING_PAYMENT' || order.orderStatus === 'PAID'" @click="cancelOrder(order.orderId)">取消订单</button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';
import OrderDetailItem from '../components/OrderDetailItem.vue';

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
      customerId: null,
      sortOrder: 'desc', // 默认排序：由新到旧
      selectedStatus: 'ALL', // 默认显示所有订单
    };
  },
  created() {
    this.fetchCustomerId();
  },
  methods: {
    async fetchCustomerId() {
      try {
        const customerName = this.$route.params.username;
        const response = await axios.get(`http://localhost:8080/api/customers/name/${customerName}`);
        this.customerId = response.data.id;
        this.fetchCustomerOrders(); // 获取顾客订单数据
      } catch (error) {
        console.error('获取顾客信息失败', error);
      }
    },
    async fetchCustomerOrders() {
      if (!this.customerId) return;
      try {
        const { data } = await axios.get(`http://localhost:8080/api/orders/customer/${this.customerId}`);
        this.orders = data;
        this.applyFilters(); // 获取订单后应用默认筛选和排序
      } catch (error) {
        console.error('获取顾客订单失败', error);
      }
    },
    applyFilters() {
      let filtered = [...this.orders];

      // 按订单状态筛选
      if (this.selectedStatus !== 'ALL') {
        if (this.selectedStatus === 'INCOMPLETE') {
          // 未完成订单筛选
          filtered = filtered.filter(
            (order) => order.orderStatus !== 'RECEIVED' && order.orderStatus !== 'CANCELED'
          );
        } else {
          filtered = filtered.filter((order) => order.orderStatus === this.selectedStatus);
        }
      }

      // 按时间排序 (使用 orderDate)
      filtered.sort((a, b) => {
        const dateA = new Date(a.orderDate);
        const dateB = new Date(b.orderDate);
        return this.sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
      });

      this.filteredOrders = filtered; // 更新筛选后的订单数据
    },
    async payOrder(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/pay`);
        this.fetchCustomerOrders();
        alert(`订单 ${orderId} 已支付`);
      } catch (error) {
        console.error('支付订单失败', error);
      }
    },
    async confirmReceipt(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/receive`);
        this.fetchCustomerOrders();
        alert(`订单 ${orderId} 已确认收货`);
      } catch (error) {
        console.error('确认收货失败', error);
      }
    },
    async cancelOrder(orderId) {
      try {
        await axios.post(`http://localhost:8080/api/orders/${orderId}/cancel`);
        this.fetchCustomerOrders();
        alert(`订单 ${orderId} 已取消`);
      } catch (error) {
        console.error('取消订单失败', error);
      }
    },
    getOrderStatusDescription(status) {
      const statusDescriptions = {
        PENDING_PAYMENT: '待付款',
        PAID: '已付款',
        SHIPPED: '已发货',
        AWAITING_RECEIPT: '待收货',
        CANCELED: '已取消',
        RECEIVED: '已收货',
      };
      return statusDescriptions[status] || status;
    },
    formatOrderDate(orderDate) {
      // 格式化订单日期
      const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
      return new Date(orderDate).toLocaleDateString('zh-CN', options);
    },
  },
  components: {
    OrderDetailItem,
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
  background-color: #4caf50;
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
