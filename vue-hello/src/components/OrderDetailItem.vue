<template>
    <div class="order-detail-item">
      <img :src="product.imageUrl || defaultImage" alt="商品图片" class="product-image" />
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-price">{{ product.price }} 元</p>
      <p>数量: {{ quantity }}</p>
      <p class="product-seller">
        <img :src="product.merchant.avatar" alt="商家头像" class="seller-avatar" />
        <a :href="`http://localhost:8081/user-home/${product.merchant.name}?role=SELLER`" class="seller-name" title="进入商家主页">{{ product.merchant.name }}</a>
      </p>
    </div>
  </template>
  
  <script>
  export default {
    name: 'OrderDetailItem',
    props: {
      product: {
        type: Object,
        required: true,
      },
      quantity: {
        type: Number,
        required: true,
      },
    },
    data() {
      return {
        defaultImage: 'https://via.placeholder.com/150',
      };
    },
  };
  </script>
  
  <style scoped>
  .order-detail-item {
    width: 200px;
    padding: 16px;
    border-radius: 10px;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 16px;
    text-align: center;
  }
  
  .product-image {
    width: 100%;
    height: 150px;
    object-fit: cover;
    border-radius: 10px;
  }
  
  .product-name {
    font-size: 18px;
    margin: 10px 0;
    color: #333;
  }
  
  .product-price {
    font-size: 16px;
    color: #ff5722;
    font-weight: bold;
  }
  
  .product-seller {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10px;
  }
  
  .seller-avatar {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    margin-right: 5px;
  }
  
  .seller-name {
    color: #007bff;
    text-decoration: underline;
    cursor: pointer;
  }
  
  .seller-name:hover {
    color: #0056b3;
  }
  </style>
  