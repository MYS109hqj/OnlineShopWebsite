<template>
    <div>
      <h1>销售统计报表</h1>
  
      <!-- 商家ID展示 -->
      <p><strong>商家 ID:</strong> {{ merchantId }}</p>
  
      <!-- 日期范围选择 -->
      <div class="date-range-section">
        <label for="startDate">选择开始日期：</label>
        <input type="datetime-local" v-model="startDate" id="startDate" />
  
        <label for="endDate">选择结束日期：</label>
        <input type="datetime-local" v-model="endDate" id="endDate" />
  
        <button @click="fetchSalesReport">生成销售报表</button>
      </div>
  
      <!-- 周度报表选择 -->
      <div class="week-section">
        <label for="weekStart">选择周的开始日期：</label>
        <input type="datetime-local" v-model="weekStart" id="weekStart" />
        
        <button @click="fetchWeeklyReport">生成周度报表</button>
      </div>
  
      <!-- 显示总销售报表 -->
      <div v-if="salesReport">
        <h2>总销售报表</h2>
        <p><strong>总销售额：</strong>{{ salesReport.totalSales }}</p>
        <p><strong>总订单数：</strong>{{ salesReport.totalOrders }}</p>
  
        <h3>按产品销售统计</h3>
        <ul>
          <li v-for="(sales, productId) in salesReport.productSales" :key="productId">
            产品ID: {{ productId }} - 销售额: {{ sales }}
          </li>
        </ul>
  
        <h3>顾客统计</h3>
        <ul>
          <li v-for="(count, customerId) in salesReport.customerPurchaseCount" :key="customerId">
            顾客ID: {{ customerId }} - 购买次数: {{ count }}
          </li>
        </ul>
      </div>
  
      <!-- 显示周度报表 -->
      <div v-if="weeklyReport">
        <h2>周度销售报表</h2>
  
        <!-- 当前周 -->
        <h3>当前周</h3>
        <p><strong>总销售额：</strong>{{ weeklyReport.currentWeek.totalSales }}</p>
        <p><strong>总订单数：</strong>{{ weeklyReport.currentWeek.totalOrders }}</p>
  
        <!-- 上周 -->
        <h3>上一周</h3>
        <p><strong>总销售额：</strong>{{ weeklyReport.previousWeek.totalSales }}</p>
        <p><strong>总订单数：</strong>{{ weeklyReport.previousWeek.totalOrders }}</p>
  
        <!-- 下周 -->
        <h3>下一周</h3>
        <p><strong>总销售额：</strong>{{ weeklyReport.nextWeek.totalSales }}</p>
        <p><strong>总订单数：</strong>{{ weeklyReport.nextWeek.totalOrders }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        merchantId: '',           // 当前商家的ID
        startDate: '',            // 报表的开始日期
        endDate: '',              // 报表的结束日期
        weekStart: '',            // 周度报表的开始日期
        salesReport: null,        // 总销售报表
        weeklyReport: null,       // 周度销售报表
      };
    },
  
    async created() {
      await this.fetchMerchantId();  // 获取商家ID
    },
  
    methods: {
      // 获取商家ID
      async fetchMerchantId() {
        try {
          const userId = localStorage.getItem('userId');
          if (!userId) {
            console.error('用户ID未找到');
            return;
          }
          const response = await axios.get(`http://localhost:8080/api/merchants/user/${userId}`);
          this.merchantId = response.data;
        } catch (error) {
          console.error('获取商家ID失败', error);
        }
      },
  
      // 获取总销售报表
      async fetchSalesReport() {
        if (!this.merchantId || !this.startDate || !this.endDate) {
          console.error('商家ID、开始日期或结束日期未设置');
          return;
        }
        try {
          const response = await axios.get('http://localhost:8080/api/sales-report', {
            params: {
              merchantId: this.merchantId,
              startDate: this.startDate,
              endDate: this.endDate,
            },
          });
          this.salesReport = response.data;
        } catch (error) {
          console.error('获取销售报表失败', error);
        }
      },
  
      // 获取周度报表
      async fetchWeeklyReport() {
        if (!this.merchantId || !this.weekStart) {
          console.error('商家ID或周的开始日期未设置');
          return;
        }
        try {
          const response = await axios.get('http://localhost:8080/api/sales-report/weekly', {
            params: {
              merchantId: this.merchantId,
              weekStart: this.weekStart,
            },
          });
          this.weeklyReport = response.data;
        } catch (error) {
          console.error('获取周度报表失败', error);
        }
      },
    },
  };
  </script>
  