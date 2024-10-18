import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import SubmitPage from '../views/SubmitPage.vue';
import LoginRegisterPage from '../views/LoginRegisterPage.vue'
import ProductManagementPage from '../views/ProductManagementPage.vue';
import MerchantPage from '../views/MerchantPage.vue';
import UserHomePage from '../views/UserHomePage.vue';
import TestPage from '../components/TestPage.vue';
import CartPage from '../views/CartPage.vue';
import OrderPageC from '../views/DemoOrderPageC.vue';
import OrderPageM from '../views/DemoOrderPageM.vue';
import DemoViewsM from '../views/DemoViewsM.vue';
import DemoViewsM2 from '../views/DemoViewsM2.vue';
import SalesReportPage from '../views/SalesReportPage.vue';
import CustomerManagement from '../views/CustomerManagement.vue';



const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/submit',
    name: 'Submit',
    component: SubmitPage
  },
  {
    path: '/login',
    name: "Login",
    component: LoginRegisterPage 
  },
  {
    path: '/user-home/:username',
    name: 'UserHome',
    component: UserHomePage,
    props: true // 启用 props
  },
  { 
    path: '/merchant/:username', 
    name: 'MerchantPage', 
    component: MerchantPage 
  },
  {
    path: '/product-management',
    name: 'ProductManagement',
    component: ProductManagementPage
  },
  {
    path: '/test',
    name: 'test',
    component: TestPage
  },
  {
    path: '/cart/:username',
    name: 'CartPage',
    component: CartPage
  },
  {
    path: '/orderC/:username',
    name: 'OrderPageC',
    component: OrderPageC
  },
  {
    path: '/orderM/:username',
    name: 'OrderPageM',
    component: OrderPageM
  },
  {
    path: '/DemoViewsM',
    name: 'DemoViewsM',
    component: DemoViewsM
  },
  {
    path: '/DemoViewsM2',
    name: 'DemoViewsM2',
    component: DemoViewsM2
  },
  {
    path: '/SalesReport',
    name: SalesReportPage,
    component: SalesReportPage
  },
  {
    path: '/CustomerManagement',
    name: CustomerManagement,
    component: CustomerManagement
  }

  
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
