// 原始文件归档: Bduan/dongtaizhuchi.kt
val filter = IntentFilter().apply {
    addAction(Intent.ACTION_SCREEN_ON)
    addAction(Intent.ACTION_SCREEN_OFF)
    addAction(Intent.ACTION_USER_PRESENT)
}
registerReceiver(receiver, filter)
// 原始文件归档
