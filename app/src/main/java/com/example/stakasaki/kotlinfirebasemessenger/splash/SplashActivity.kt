package com.example.stakasaki.kotlinfirebasemessenger.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stakasaki.kotlinfirebasemessenger.R
import com.example.stakasaki.kotlinfirebasemessenger.messages.LatestMessagesActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// スプラッシュ画面を生成
class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // kotlin coroutineを使う
        CoroutineScope(Dispatchers.Main).launch {
            // 3秒間だけ画面を表示する
            delay(3000)
            val intent = Intent(this@SplashActivity, LatestMessagesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}