Godeye 项目 — 技术说明与运行/调试指南
=========================================

概述
--
Godeye 是一个用于移动端设备上报位置信息、屏幕/锁屏状态并与服务器通讯的轻量监控模块。该仓库原始代码散落于根目录与 `Aduan`、`Bduan` 子目录中。此次重构将源码组织为标准 Android/Kotlin 模块结构，并抽取了网络、定位、屏幕监听和 UI 管理的核心组件。

主要功能
-
- 实时定位采集并上报到 TCP 服务端（支持心跳与上报协议）
- 屏幕与锁屏状态监听并作为上报字段
- 地图上显示设备位置（Marker 管理）
- 前台 Service 保证进程常驻并持续上报
- 支持将协议与控制指令（JSON）外置为配置文件

重构后目录（关键文件）
- `src/main/kotlin/com/godeye/model/ReportData.kt` — 上报数据模型
- `src/main/kotlin/com/godeye/network/TcpServer.kt` — 服务端接收/解析
- `src/main/kotlin/com/godeye/network/TcpClient.kt` — 客户端发送/心跳
- `src/main/kotlin/com/godeye/location/LocationCollector.kt` — 定位采集封装
- `src/main/kotlin/com/godeye/screen/ScreenStateReceiver.kt` — 屏幕状态监听
- `src/main/kotlin/com/godeye/service/MonitorService.kt` — 前台监控 Service
- `src/main/kotlin/com/godeye/ui/MarkerManager.kt` — 地图 Marker 管理
- `src/main/kotlin/com/godeye/utils/*` — 权限/省电豁免等工具

协议与配置
- `xieyi.json` — 上报数据字段示例（type/report/heartbeat/ack）
- `kongzhizhiling.json` — 示例控制指令（如调整上报间隔）

运行逻辑（摘要）
- 启动 `MonitorService`：创建前台通知、启动 TCP 客户端并连接服务端。
- 启动定位采集：收到位置后构造 `ReportData` 并通过 `TcpClient.sendReport` 上报。
- 屏幕/锁屏变化触发回调，可合并到上报或单独上报一条状态包。
- 服务端（或测试用的 `TcpServer`）按行读取 JSON，解析为 `ReportData` 并处理/存储。

如何使用（开发者）
1. 在 Android Studio 中将此目录作为一个 Module 打开，或将 `src` 内容合并到现有 Android 项目下。
2. 在 `build.gradle` 中添加必要依赖：

```groovy
implementation 'com.google.code.gson:gson:2.8.9'
implementation 'com.google.android.gms:play-services-location:21.0.1'
// 视项目需要添加 AMap/高德地图 SDK 依赖
```

3. 配置 `AndroidManifest.xml`：添加 `MonitorService` 服务声明与必要权限（定位、网络、前台服务）。
4. 启动 `MonitorService`（示例）：

```kotlin
startService(Intent(this, MonitorService::class.java))
```

调试方案
- 本地 TCP 服务测试：使用 `TcpServer` 在 PC 或 Android 上启动监听（9000 端口示例），并观察接收的 JSON。可用 `nc -l 9000` 或写一个小脚本测试。
- 日志与断点：在 `TcpClient.sendReport`、`LocationCollector`、`ScreenStateReceiver` 添加日志打印，使用 Android Studio Logcat 观看运行时信息。
- 权限与省电：确认应用已授予定位与前台服务权限，并在系统设置中允许忽略电池优化（可使用 `BatteryIgnoreHelper` 调用提示）。
- 网络连通：确保设备/模拟器与服务端网络可达；在无法连通时查看重连逻辑与异常捕获。

常见问题与排查
- 未上报：检查定位权限与 GPS 是否可用；查看 `LocationCollector` 的回调是否触发。
- 无心跳/断连：查看 `TcpClient` 的 `connect()`/`reconnect()` 日志；确认服务端是否接受连接。
- 地图不显示：确认 AMap SDK 已集成并在 Activity 中正确初始化 `MapView`。

下一步建议
- 完成 AES 加解密实现（`TcpClient.encrypt` 与 `TcpServer.decrypt`），并添加密钥管理方案。
- 为消息添加帧边界或长度前缀，增强协议的鲁棒性（当前实现以行分隔为简化方案）。
- 编写单元测试与集成测试，以及 CI 校验（静态分析、Kotlin 检查等）。

如需我继续：我可以（选择一项）
- 1) 实现 AES 加密/解密并演示本地端到端测试
- 2) 自动将现有散落的 `.kt` 文件合并并替换为新版模块实现
- 3) 为项目生成 Gradle 配置并提供一键运行指令

