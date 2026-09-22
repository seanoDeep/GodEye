package com.godeye.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.KeyguardManager

class ScreenStateReceiver(private val onStateChange: (Boolean, Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                val keyguard = context?.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                val locked = keyguard?.isKeyguardLocked ?: false
                onStateChange(true, locked)
            }
            Intent.ACTION_SCREEN_OFF -> {
                onStateChange(false, true)
            }
            Intent.ACTION_USER_PRESENT -> {
                onStateChange(true, false)
            }
        }
    }
}
