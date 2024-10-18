本项目作为网络应用开发的课程作业，附上相关信息：
202230441206 黄麒嘉


## 整体架构与环境
前端Vue3，后端Springboot，数据库Mysql。
（注：前端有较多Vue2写法，但使用Vue3配置编译）

本机环境（Windows）：
IDE使用vscode
npm：10.2.4
mysql  Ver 9.0.1 for Win64 on x86_64
java 22.0.2 2024-07-16

服务器环境为（CentOS7 Linux）




## 实现具体功能
注：用户个人界面中的“编辑个人信息”功能虽然有按钮，但没实现相关功能。相关按钮定义被注释。（vue-hello/views/UserHomePage.vue）



## 代码架构
由于逐模块增量开发，项目名创建时命名没设置好。
vue-hello是前端文件夹(frontend)；demo是后端文件夹（backend）。

### 前端文件架构
对于前端，主要文件的文件架构如下

```plaintext
/src
│
├── App.vue                # 主应用组件
├── main.js                # 入口文件
├── eventBus.js            # 事件总线定义（登录与主页面通信）
├── components             # 组件文件夹
│   ├── CartItem.vue         # 购物车-商品单元
│   ├── OrderDetailItem.vue         # 订单-商品单元
│   ├── ProductItem.vue         # 商品列表-商品单元
│   ├── ProductItemSellerPage.vue         # 商家主页-商品单元
│   └── 其余的TestPage只用于中间测试，若要删除需要同步删除/route/index.js的路由导航。
├── views                  # 视图文件夹
│   ├── CartPage.vue           # 主页视图
│   ├── CustomerManagement.vue           # 客户管理视图
│   ├── DemoOrderPageC.vue           # 顾客订单状态视图
│   ├── DemoOrderPageM.vue           # 商家订单管理视图
│   ├── DemoViewsM2.vue           # 商家浏览量/订单量日志视图
│   ├── HomePage.vue           # 主页（商品列表）视图
│   ├── LoginRegisterPage.vue           # 登录注册视图
│   ├── MerchantPage.vue           # 商家页面视图
│   ├── ProductManagementPage.vue           # 商家商品管理视图
│   ├── SalesReportPage.vue           # 商家报表数据视图
│   ├── UserHomePage.vue           # 用户主页视图
│   └── 其它视图只是中间产物
└── router                 # 路由文件夹
    └── index.js          # 路由配置
```

### 后端文件架构
```plaintext
/src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── demo
│   │               ├── config      # 编程过程未涉及
│   │               ├── controller      # 控制器层（定义后端的接口）
│   │               │   ├── AuthController.java  # 认证（用户登录注册）的完整逻辑（版本问题该功能未分层）
│   │               │   ├── BrowseHistoryController.java  # 浏览记录控制器
│   │               │   ├── CartController.java  # 购物车控制器
│   │               │   ├── CustomerController.java  # 顾客类控制器
│   │               │   ├── CustomerManagementController.java  # 商户-顾客管理控制器
│   │               │   ├── MerchantBrowseHistoryController.java  # 商户-浏览记录控制器
│   │               │   ├── MerchantController.java  # 商户类控制器
│   │               │   ├── OrderController.java    # 订单类控制器
│   │               │   ├── ProductController.java  # 商品类控制器
│   │               │   ├── PurchaseQuantityController.java  # 商家-商品购买量控制器
│   │               │   └── SalesReportController.java  # 商家-销售报告控制器
│   │               ├── dto      # 数据传输对象层
│   │               │   ├── BrowseHistoryDTO.java  # 浏览记录数据传输对象
│   │               ├── exception      # 异常处理层
│   │               │   ├── ProductDeletionException.java  # 商品删除异常
│   │               ├── model           # 实体模型层
│   │               │   ├── BrowseHistory.java         # 浏览记录实体类
│   │               │   ├── Cart.java           # 购物车实体类
│   │               │   ├── CartItem.java      # 购物车项实体类
│   │               │   ├── Customer.java        # 顾客实体类
│   │               │   ├── Merchant.java        # 商户实体类
│   │               │   ├── Order.java           # 订单实体类
│   │               │   ├── OrderItem.java       # 订单项实体类
│   │               │   ├── OrderStatus.java     # 订单状态实体类
│   │               │   ├── Product.java         # 商品实体类
│   │               │   └── User.java            # 用户实体类
│   │               ├── repository      # 数据库访问层
│   │               │   ├── BrowseHistoryRepository.java  # 浏览记录仓库接口
│   │               │   ├── CartItemRepository.java  # 购物车项仓库接口
│   │               │   ├── CartRepository.java  # 购物车仓库接口
│   │               │   ├── CustomerRepository.java  # 顾客仓库接口
│   │               │   ├── MerchantRepository.java  # 商户仓库接口
│   │               │   ├── OrderItemRepository.java  # 订单项仓库接口
│   │               │   ├── OrderRepository.java    # 订单仓库接口
│   │               │   ├── ProductRepository.java  # 商品仓库接口
│   │               │   └── UserRepository.java    # 用户仓库接口
│   │               ├── service         # 业务逻辑层
│   │               │   ├── BrowseHistoryService.java  # 浏览记录服务类
│   │               │   ├── CartService.java  # 购物车服务类
│   │               │   ├── CustomerService.java  # 顾客服务类
│   │               │   ├── EmailService.java  # 邮件服务类
│   │               │   ├── MerchantBrowseHistoryService.java  # 商户浏览记录服务类
│   │               │   ├── MerchantService.java  # 商户服务类
│   │               │   ├── OrderItemService.java  # 订单项服务类
│   │               │   ├── OrderService.java     # 订单服务类
│   │               │   ├── ProductService.java   # 商品服务类
│   │               │   ├── SalesReportService.java  # 销售报告服务类
│   │               │   └── UserService.java       # 用户服务类（未使用，用于删除账户信息，该功能未实现）
│   │               ├── scripts      # 脚本文件
│   │               │   ├── BrowseHistoryGenerationScript.java  # 对给定的product_id的商品生成浏览记录（需要有已注册用户）
│   │               │   └── OrderGenerationScript.java    # 对给定的product_id的商品生成已完成的订单记录（需要有已注册用户）
│   │               ├── DemoApplication 2.java  # 执行两个脚本文件的启动类代码，需要将原有启动类更名，将该文件改名为DemoApplication.java才能执行
│   │               └── DemoApplication.java # 启动类
│   └── resources
│       └──  application.properties     # 配置文件（须填写对应配置并更名为该名称才能运行）
└── test的相关逻辑并没增加
```