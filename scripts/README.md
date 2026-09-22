本地测试说明
----------------

- 启动本地 Python 测试服务器（需安装依赖）：

```bash
pip install pycryptodome
python scripts/test_server.py
```

- 在 Android 设备或模拟器上启动 `MonitorService`（或运行一个 TcpClient 测试脚本）连接到该主机的 9000 端口。
- 如果在同一台机器上测试（连接 localhost），确保模拟器/设备能访问宿主主机（使用 adb reverse 或在物理设备上测试）。
