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
        json (key value) 형식으로 데이터 저장
        DB 데이터 조회 함수 3가지 (리스너를통해 데이터 조회)
        데이터 추가 방법 3가지 setValue, updateChildren (map 형태로 데이터 추가), data class를 직접 추가
    - opensource library yuyakaido/CardStackView 사용하기
        - 애니메이션 사용 시 open library 사용하는것이 시간 절감됨 -> github에서 찾아서 사용
    - Recyclerview 사용하기 (with Diffutil)
    - Glide
        - android 내부 데이터를 uri 를 통해 접근 시 Glide 사용이 필요 없음
        - 외부 서버에 저장된 uri를 http 프로토콜을 통해 접근할 때 거쳐야할 여러 과정들을 간단히 구현하도록 도와준다.

TODO:
 0. MVVM 등 아키텍처 패턴 적용해보기 
 1. 디자인 변경하기
    -> 진행 중
    material editText 색상 변경, bottom nav 색상 변경 -> 완료, 프로필 버튼 둥글게 -> 완료
 2. 사진 불러오기 기능 추가하기
    -> 완료, contentsProvider, image crop 라이브러리 사용
 3. matchedListActivity 변경하기
    -> 완료
 4. bottom navigation 추가 및 activity -> fragment 로 변경하기, androidx navigation 사용하기
    -> 완료
 5. FB DB에 데이터 추가 시, data class 를 이용해 추가하도록 변경하기
    -> 완료
 7. like 화면 만들기
    -> 완료
 8. 채팅 화면 만들기
    -> 진행중
 9. 매칭 리스트 화면 만들기
    -> 완료
 10. string 변경 (Toastmessage, xml)

 DB 구조

 db
 users
	id
		info
		likedby
			match
		chat
			push.setvalue
				chatroom클래스 항목들
 chats
	key
		push.setvalue   //push를 해서 넣으면 나중에 데이터 순차 탐색 시 용이함
			senderid:
			message:
			timestamp:

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