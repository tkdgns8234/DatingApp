package com.hoon.datingapp.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hoon.datingapp.databinding.ActivitySplashBinding
import com.hoon.datingapp.presentation.view.login.LoginActivity
import com.hoon.datingapp.presentation.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // 로그인 되어있지 않으면 login activity로 이동
        if (auth.currentUser == null) {
            startActivity(LoginActivity.newIntent(this))
        } else {
            startActivity(MainActivity.newIntent(this))
        }
    }
}