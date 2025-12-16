# Implementation Plan

## Phase 1: 依赖升级与基础设施

- [x] 1. 升级项目依赖


  - [x] 1.1 升级 fastjson 到 fastjson2


    - 将 `com.alibaba:fastjson:1.2.83` 替换为 `com.alibaba.fastjson2:fastjson2:2.0.x`
    - 更新代码中的 import 语句和 API 调用
    - _Requirements: 1.1_

  - [x] 1.2 升级 Disruptor 到 4.0+

    - 将 `com.lmax:disruptor:3.3.6` 升级为 `com.lmax:disruptor:4.0.0`
    - 检查并适配 API 变更
    - _Requirements: 1.2_
  - [x] 1.3 清理未使用依赖和统一版本管理


    - 移除 `hutool-http` 依赖
    - 移除显式的 `jackson-databind` 版本，由 Spring Boot BOM 管理
    - _Requirements: 1.3, 1.4_

- [x] 2. 添加测试框架依赖



  - [x] 2.1 添加 jqwik 属性测试框架

    - 在 pom.xml 中添加 jqwik 依赖
    - 配置 JUnit 5 与 jqwik 集成
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

## Phase 2: 重试机制与连接管理

- [x] 3. 实现重试策略



  - [x] 3.1 创建 RetryPolicy 接口

    - 在 `retry` 包下创建 `RetryPolicy` 接口
    - 定义 `getNextRetryDelay` 和 `shouldRetry` 方法
    - _Requirements: 2.1_
  - [x] 3.2 实现 ExponentialBackoffRetryPolicy


    - 实现指数退避算法
    - 支持配置最大重试次数、初始延迟、最大延迟、乘数
    - _Requirements: 2.1, 2.2_
  - [x] 3.3 编写属性测试：指数退避延迟计算


    - **Property 1: 指数退避重试延迟计算**
    - **Validates: Requirements 2.1, 2.2**
  - [x] 3.4 创建 RetryConfig 配置类


    - 使用 @ConfigurationProperties 绑定重试配置
    - 添加配置校验注解
    - _Requirements: 2.1_

- [x] 4. 优化 SessionManager


  - [x] 4.1 修改 getClient 返回 Optional 类型


    - 修改 `SessionManager` 接口
    - 更新 `SessionManagerImpl` 实现
    - _Requirements: 2.3_
  - [x] 4.2 添加 removeClient 方法

    - 在接口和实现中添加删除客户端方法
    - _Requirements: 2.3_
  - [x] 4.3 编写属性测试：SessionManager Optional 返回


    - **Property 2: SessionManager 获取不存在 Client 返回 Optional.empty**
    - **Validates: Requirements 2.3**

  - [x] 4.4 编写属性测试：SessionManager CRUD 一致性
    - **Property 13: SessionManager CRUD 操作一致性**
    - **Validates: Requirements 8.1**


- [x] 5. 优化 ConnectionService


  - [x] 5.1 使用 entrySet 替代 keySet 遍历

    - 修改 `checkConnectionStatus` 方法中的 Map 遍历方式
    - _Requirements: 2.4_

  - [x] 5.2 集成重试策略

    - 在连接失败时使用 ExponentialBackoffRetryPolicy
    - _Requirements: 2.1, 2.2_

- [x] 6. Checkpoint - 确保所有测试通过



  - 确保所有测试通过，如有问题请询问用户

## Phase 3: 资源管理与内存优化

- [x] 7. 实现 SslContext 复用



  - [x] 7.1 创建 SslContextProvider 组件

    - 在 `config/beans` 包下创建 `SslContextProvider`
    - 使用 @PostConstruct 初始化并缓存 SslContext
    - _Requirements: 3.1_

  - [x] 7.2 修改 Client 类使用 SslContextProvider

    - 注入 SslContextProvider
    - 移除 Client 中的 SslContext 创建逻辑
    - _Requirements: 3.1_
  - [x] 7.3 编写属性测试：SslContext 实例复用


    - **Property 3: SslContext 实例复用**
    - **Validates: Requirements 3.1**



- [x] 8. 修复 ThreadLocal 内存泄漏
  - [x] 8.1 修改 TimingThreadPool 的 afterExecute 方法

    - 在 finally 块中调用 `startTime.remove()`
    - _Requirements: 3.2_


  - [x] 8.2 编写属性测试：ThreadLocal 清理
    - **Property 4: ThreadLocal 清理**

    - **Validates: Requirements 3.2**
  - [x] 8.3 编写属性测试：TimingThreadPool 计时准确性
    - **Property 14: TimingThreadPool 计时准确性**
    - **Validates: Requirements 8.2**




- [x] 9. 优化 ConcurrentHashMap 初始化

  - [x] 9.1 调整 SessionManagerImpl 中的 Map 初始化参数
    - 设置合理的初始容量和负载因子
    - _Requirements: 3.3_



- [x] 10. 优化优雅关机顺序


  - [x] 10.1 调整 ShutdownHooks 执行顺序
    - 确保业务线程池先于 Netty EventLoopGroup 关闭
    - ConnectionService: VERY_HIGH, SessionManager: HIGH, ClientManager: MEDIUM
    - _Requirements: 3.4_

- [x] 11. Checkpoint - 确保所有测试通过


  - 确保所有测试通过，如有问题请询问用户

## Phase 4: 异常处理增强

- [x] 12. 创建自定义异常体系



  - [x] 12.1 创建 ClientNotFoundException

    - 继承 BusinessException
    - _Requirements: 4.2_

  - [x] 12.2 创建 ConnectionException 及子类

    - 创建 ConnectionException、ConnectionTimeoutException、ConnectionRefusedException
    - _Requirements: 4.1_


  - [x] 12.3 编写属性测试：连接异常转换
    - **Property 5: 连接异常转换为业务异常**

    - **Validates: Requirements 4.1**
  - [x] 12.4 编写属性测试：获取不存在 Client 抛出异常
    - **Property 6: 获取不存在 Client 抛出特定异常**
    - **Validates: Requirements 4.2**

- [x] 13. 实现 Disruptor ExceptionHandler


  - [x] 13.1 创建 DisruptorExceptionHandler

    - 实现 `com.lmax.disruptor.ExceptionHandler` 接口
    - 记录详细的异常信息

    - _Requirements: 4.3_
  - [x] 13.2 配置 Disruptor 使用自定义 ExceptionHandler

    - 在 DisruptorReadQueueImpl 和 DisruptorWriteQueueImpl 中配置
    - _Requirements: 4.3_

- [x] 14. 实现时间轮任务重试



  - [x] 14.1 修改 TimerTask 支持重试
    - 添加重试次数和重试策略配置

    - _Requirements: 4.4_
  - [x] 14.2 编写属性测试：时间轮任务重试

    - **Property 7: 时间轮任务失败重试**
    - **Validates: Requirements 4.4**
  - [x] 14.3 编写属性测试：时间轮任务调度准确性
    - **Property 15: 时间轮任务调度准确性**（在 TimerTask 重试测试中）
    - **Validates: Requirements 8.3**

- [x] 15. 更新全局异常处理器


  - [x] 15.1 添加新异常类型的处理方法

    - 处理 ClientNotFoundException、ConnectionException 等
    - _Requirements: 4.1, 4.2_


- [x] 16. Checkpoint - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

## Phase 5: 代码质量优化

- [x] 17. 重构依赖注入


  - [x] 17.1 修改 ClientManagerImpl 使用接口注入

    - 将 `SessionManagerImpl` 改为 `SessionManager`
    - 将 `ReportQoeServiceImpl` 改为 `ReportQoeService`
    - 将 `TimeWheelServiceImpl` 改为 `TimeWheelService`

    - _Requirements: 5.1_
  - [x] 17.2 修改 ConnectionServiceImpl 使用接口注入
    - 将具体实现类改为接口类型
    - _Requirements: 5.1_

- [x] 18. 重构匿名内部类为 Lambda

  - [x] 18.1 重构 ClientManagerImpl 中的匿名类
    - 将 Runnable 匿名类改为 Lambda 表达式

    - _Requirements: 5.2_

  - [x] 18.2 重构 Client 中的 ChannelFutureListener
    - 将匿名类改为 Lambda 表达式
    - _Requirements: 5.2_


- [x] 19. 优化日志语句
  - [x] 19.1 检查并修复字符串拼接日志
    - 已检查，未发现字符串拼接日志，所有日志已使用参数化格式
    - _Requirements: 5.4_

## Phase 6: 监控与可观测性

- [x] 20. 实现连接监控指标
  - [x] 20.1 创建 ConnectionMetrics 组件


    - 暴露 total connections 和 active connections 指标
    - _Requirements: 6.1_
  - [x] 20.2 集成到 SessionManager
    - 在添加/移除客户端时更新指标
    - _Requirements: 6.1_
  - [x] 20.3 编写属性测试：连接指标准确性
    - **Property 8: 连接指标准确性**
    - **Validates: Requirements 6.1**

- [x] 21. 实现队列监控指标
  - [x] 21.1 增强 QueueMetric
    - 添加队列剩余容量指标（已存在）
    - _Requirements: 6.2_
  - [x] 21.2 编写属性测试：队列指标准确性
    - **Property 9: 队列指标准确性**
    - **Validates: Requirements 6.2**

- [x] 22. 实现任务监控指标

  - [x] 22.1 创建 TaskMetrics 组件
    - 记录时间轮任务执行延迟和成功率
    - _Requirements: 6.3_
  - [x] 22.2 编写属性测试：任务指标准确性
    - **Property 10: 任务指标准确性**
    - **Validates: Requirements 6.3**

- [x] 23. 增强线程池监控
  - [x] 23.1 扩展 ThreadPoolMetric
    - 添加活跃线程数、队列大小、拒绝任务数指标
    - _Requirements: 6.4_
  - [x] 23.2 编写属性测试：线程池指标准确性
    - **Property 11: 线程池指标准确性**（在 TimingThreadPoolPropertyTest 中）
    - **Validates: Requirements 6.4**


- [x] 24. Checkpoint - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

## Phase 7: 配置管理优化

- [x] 25. 增强配置校验
  - [x] 25.1 为 ClientConfig 添加校验注解
    - 添加 @NotBlank、@Min、@Max 等校验注解
    - _Requirements: 7.1_
  - [x] 25.2 为其他配置类添加校验
    - NettyConfig、ThreadPoolConfig 等
    - _Requirements: 7.1_
  - [x] 25.3 编写属性测试：配置校验有效性
    - **Property 12: 配置校验有效性**
    - **Validates: Requirements 7.1**

- [x] 26. 实现智能线程数配置
  - [x] 26.1 修改 NettyConfig 支持自动计算线程数
    - 当配置为 0 时，自动设置为 CPU 核心数 * 2
    - _Requirements: 7.2_

- [x] 27. 添加超时配置支持
  - [x] 27.1 扩展 ClientConfig 添加超时配置
    - 添加 connectTimeoutMs、readTimeoutMs、writeTimeoutMs
    - _Requirements: 7.3_
  - [x] 27.2 在 Client 中应用超时配置
    - 配置 Netty Channel 的超时选项
    - _Requirements: 7.3_

- [x] 28. 添加 SSL 证书配置支持
  - [x] 28.1 扩展 ClientConfig 添加 SSL 配置
    - 添加 sslCertPath、sslKeyPath、sslPassword
    - _Requirements: 7.4_
  - [x] 28.2 修改 SslContextProvider 支持自定义证书
    - 根据配置加载自定义证书
    - _Requirements: 7.4_

- [x] 29. 编写配置属性绑定测试
  - **Property: 配置属性绑定正确性**（在 ClientConfigPropertyTest 中）
  - **Validates: Requirements 8.4**



- [x] 30. Final Checkpoint - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户
