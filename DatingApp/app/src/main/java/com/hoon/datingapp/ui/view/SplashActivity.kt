package com.hoon.datingapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hoon.datingapp.databinding.ActivitySplashBinding

/*
배운점
    - Firebase Auth
        email login
        facebook login
    - Firebase Realtime Database
        json 과 유사하게 key value 형식으로 데이터 저장
        DB 데이터 조회 함수 3가지 (리스너를통해 데이터 조회)
        데이터 추가 방법 3가지 setValue, updateChildren (map 형태로 데이터 추가), data class를 직접 추가
    - opensource library yuyakaido/CardStackView 사용하기
        - 애니메이션 사용 시 open library 사용하는것이 시간 절감됨 -> github에서 찾아서 사용
    - Recyclerview 사용하기 (with Diffutil)

TODO:
 1. 디자인 변경하기
 2. 사진 불러오기 기능 추가하기
 3. matchedListActivity 변경하기
 4. bottom navigation 추가 및 activity -> fragment 로 변경하기, androidx navigation 사용하기
 5. FB DB에 데이터 추가 시, data class 를 이용해 추가하도록 변경하기
 */

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
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}