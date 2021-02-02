package com.example.stakasaki.kotlinfirebasemessenger.registerlogin

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.stakasaki.kotlinfirebasemessenger.messages.LatestMessagesActivity
import com.example.stakasaki.kotlinfirebasemessenger.R
import com.example.stakasaki.kotlinfirebasemessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.StringBuilder
import java.util.*

// ユーザー情報の登録
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 新規アカウント登録
        // performRegister()でfirebaseにユーザー情報を登録する
        register_button_register.setOnClickListener {
            performRegister()
        }

        // 既にアカウントを持っている場合
        already_have_account_text_view.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")

            // LoginActivityに遷移
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // プロフィール画像をスマホの画像フォルダから選択する
        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            // 画像データをリクエストコード0で受け取る
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    // プロフィール画像を選択しない場合は人のシルエット画像を用いる
    val profileNotSelectedImageUri: Uri = Uri.parse("android.resource://com.example.stakasaki.kotlinfirebasemessenger/drawable/profile_image_icon")
    // 選択されたプロフィール画像Uriを格納する
    var selectedPhotoUri: Uri? = profileNotSelectedImageUri

    // intentから画像Uriを受け取る
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "Photo was selected")

            // intentから受け取ったデータを格納
            selectedPhotoUri = data.data

            // bitmapに変換
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            // 画像を画面に表示する
            selectphoto_imageview_register.setImageBitmap(bitmap)

            // 「アイコンを選択」の文字を透明にする
            selectphoto_button_register.alpha = 0f
        }
    }

    // 「新規登録」ボタンが押された時に呼び出される
    private fun performRegister() {
        // メールアドレスとパスワードの文字列を取得
        val email = email_edittext_login.text.toString()
        val password = passsword_edittext_register.text.toString()

        // メールアドレスとパスワードが両方入力されていない場合は登録しない
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "メールアドレスとパスワードを入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        // Firebase Authentication
        // メールアドレスとパスワードでユーザーを登録
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result!!.user!!.uid}")

                // 登録が成功した場合プロフィール画像をアップロードする
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "ユーザー登録に失敗しました: ${it.message}", Toast.LENGTH_SHORT).show() }
    }

    // プロフィール画像をfirebaseにアップロードする
    private fun uploadImageToFirebaseStorage()  {
        // 乱数でファイル名を作製
        val filename = UUID.randomUUID().toString()
        // Firebase Storageの/images/にファイルを追加する
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        // ファイルの追加
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully upload image: ${it.metadata?.path}")

                // URLが正しく追加された場合
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity", "File Location: $it")

                    // Firebase Databaseに登録する
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to add downloadUrl ")
            }
    }

    // Firebase Databaseにユーザー情報を登録する
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        // users/にuidとしてユーザーを登録する
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // ユーザー情報として, uid, ユーザーの名前, プロフィール画像のURKをもつ
        val user =
            User(
                uid,
                username_edittext_register.text.toString(),
                profileImageUrl
            )
        // users/uid/にユーザー情報を登録する
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")

                // LatestMessagesActivityに遷移する
                val intent = Intent(this, LatestMessagesActivity::class.java)
                // 登録後に新規登録画面に戻ってこられないようにする
                // スタックに残っていても、新しくタスクを起動させる
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to set value to database: ${it.message}")
            }
    }


}

