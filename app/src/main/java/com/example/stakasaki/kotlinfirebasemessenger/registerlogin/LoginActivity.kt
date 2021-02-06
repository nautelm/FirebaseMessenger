package com.example.stakasaki.kotlinfirebasemessenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stakasaki.kotlinfirebasemessenger.R
import com.example.stakasaki.kotlinfirebasemessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 「ログイン」ボタンを押したとき
        // 処理の内容は登録時と同じ
        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Log.d("Login", "Successfully login with uid: ${it.result!!.user!!.uid}")
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Log.d("Login", "Failed to login: ${it.message}")
                    Toast.makeText(this, "ログインに失敗しました: ${it.message}", Toast.LENGTH_SHORT).show() }
        }

        // 登録画面に戻る
        back_to_register_textview.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}







