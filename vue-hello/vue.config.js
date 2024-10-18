const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: { //用于配置开发服务器的选项
    host: 'localhost',
    port: 8081, // 将端口号设置为你想要的端口
    proxy: { //定义了请求的代理规则
      '/auth': {
        target: 'http://localhost:8080', // Spring Boot 的地址
        changeOrigin: true,
        // pathRewrite: { '^/auth': '' }, // 可选：重写路径，将该部分省略
      },
      // 其他 API 的代理配置可以在这里添加
    },
  },
});
