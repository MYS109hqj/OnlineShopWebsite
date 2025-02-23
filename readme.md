本项目作为网络应用开发的课程作业


## 整体架构与环境
前端Vue3，后端Springboot，数据库Mysql。
（注：前端有较多Vue2写法，但使用Vue3配置编译）

本机环境（Windows）：

IDE：Visual Studio Code
Node.js 版本：10.2.4
MySQL 版本：9.0.1
Java 版本：22.0.2

服务器环境：

操作系统：CentOS 7 Linux




## 实现具体功能
注：用户个人界面中的“编辑个人信息”功能虽然有按钮，但没实现相关功能。相关按钮定义被注释。（vue-hello/views/UserHomePage.vue）

**商品列表**：显示所有上架的商品。

**注册登录**：需要输入用户名、邮箱（如果是顾客）、密码，不对用户名、密码、邮箱进行检验，没有长度限制，但是用户名和邮箱要求唯一。

**商品管理**：可以上下架商品，更改商品信息，逐个添加商品。对于删除商品，只能删除没有相关订单的商品，否则只能下架。

**购物车与订单**：点击商品项，顾客可以将商品选定数量后加入购物车；商品项可以重复；可以在购物车页面选中删除商品或购买商品。（注：商品的选中状态由后端维护，每次修改状态都调用后端的修改接口）；购买后按商家分类生成订单；顾客能够取消、支付、确认收货；商家可以取消、送货、送达。

**客户管理**：商家可以在客户管理界面（导航选项即“客户管理”CustomerManagement.vue），查看用户对自家所有商品的总观看量和购买量，以及下单记录。如果该用户有正在进行（不位于取消或已收货状态）的订单，会显示绿色的“该用户有正在进行的订单”。

**浏览量与购买量**：商家可以在浏览日志界面（导航选项即“浏览日志”DemoViewsM2.vue），查看店铺的总购买量和浏览量；可以查看所有浏览日志；也可以查看单个商品的浏览购买量、浏览日志。

**报表**：商家可以通过点击导航的“查看报表”进入（SalesReportPage.vue），可以查询指定时间段的购买量；可以以输入日期为一周的开始，对比指定周与前后一周的购买量。

**邮件**会从个人邮箱的SMTP服务发送“订单order_id”完成类似的字样到顾客的邮箱。



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
