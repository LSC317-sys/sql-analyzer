# SQL 执行计划分析器

🔍 基于 Spring Boot 的 SQL 执行计划分析工具，帮助开发者优化 SQL 查询性能。

## 功能特性

- ✅ 输入 SQL 自动生成 EXPLAIN 执行计划
- ✅ 可视化展示执行计划表格
- ✅ 智能分析并给出优化建议
- ✅ 历史记录查询
- ✅ 检测全表扫描、缺失索引等问题

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2 + MySQL 8.0 + JPA |
| 前端 | HTML5 + CSS3 + JavaScript |
| 工具 | Maven + Lombok |

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/你的用户名/sql-analyzer.git

# 2. 创建数据库
mysql -u root -p
CREATE DATABASE sql_analyzer;

# 3. 修改配置
修改 src/main/resources/application.properties 中的数据库密码

# 4. 启动
mvn spring-boot:run

# 5. 访问
http://localhost:8080
使用示例
输入 SQL：

SELECT * FROM users WHERE name = '张三'
输出：

执行计划表格
优化建议（如：建议添加索引）
项目结构
sql-analyzer/
├── src/main/java/
│   ├── controller/    # 接口层
│   ├── service/       # 业务逻辑
│   ├── entity/        # 数据实体
│   ├── repository/    # 数据访问
│   └── dto/           # 数据传输对象
├── src/main/resources/
│   ├── static/        # 前端页面
│   └── application.properties  # 配置文件
└── README.md