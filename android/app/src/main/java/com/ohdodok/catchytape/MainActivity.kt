package com.ohdodok.catchytape

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ohdodok.catchytape.feature.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO : 자동로그인 할 때 수정 필요
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}