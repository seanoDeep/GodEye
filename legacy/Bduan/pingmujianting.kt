// 原始文件归档: Bduan/pingmujianting.kt
class ScreenStateReceiver(private val onStateChange: (Boolean, Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                // 屏幕亮起
                val keyguard = context?.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                val locked = keyguard?.isKeyguardLocked ?: false
                onStateChange(true, locked)
            }
            Intent.ACTION_SCREEN_OFF -> {
                // 屏幕息屏
                onStateChange(false, true) // 息屏可认为是锁屏状态
            }
            Intent.ACTION_USER_PRESENT -> {
                // 解锁
                onStateChange(true, false)
            }
        }
    }
}
// 原始文件归档
