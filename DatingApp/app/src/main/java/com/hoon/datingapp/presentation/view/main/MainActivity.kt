/**
배운점
- Firebase Auth
email login
facebook login
- Firebase Realtime Database
- json (key value) 형식으로 데이터 저장
- DB 데이터 조회 함수 4가지 (리스너를통해 데이터 조회, get() 을 통한 데이터 조회)
get() 을 이용하는 경우 캐시영역에서 가져오는게 아니라 서버에서 직접 조회해서 데이터를 가져옴
-> sync가 무조건 맞아야 하는 경우 사용

- 데이터 추가 방법 3가지 setValue, updateChildren (map 형태로 데이터 추가), data class를 이용해 추가하는 방법

- opensource library yuyakaido/CardStackView 사용하기
- 애니메이션 사용 시 open library 사용하는것이 시간 절감됨 -> github에서 찾아서 사용
- Recyclerview 사용하기 (with Diffutil)
-> listadapter 를 사용하면 diffutill 클래스를 통해 n^2 시간복잡도를 N + D^2 까지 낮출 수 있다.
-> 힘들었던 점 !!;;
-> notifyDataSetChanged() 를 사용하는 경우 모든 item을 새로 생성하는것과 같기때문에 심한 비효율 초래
-> submitList() api를 이용해 데이터를 갱신하는데 정상적으로 갱신되지 않는 문제
-> -> submitList 구현 내부를 살펴보면
if (newList == mList) { return 처럼 기존 리스트와 동일한 객체인 경우
submitList에대한 처리를 하지 않고 return 시킨다.
-> submitList 시킬 때 새로운 list를 생성해 파라미터로 넘긴다.

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
//push를 해서 random key 값을 넣으면 나중에 데이터 순차 탐색 시 용이함

db
users
uid
UserProfile 항목들
likedby
like
dislike
chat
push key
(ChatRoom 항목들)
key
lastMessage
myID
partnerID

chats
key
push key
(ChatItem 항목들)
message:
senderid:
timestamp:

 */

package com.hoon.datingapp.presentation.view.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.ActivityMainBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.BaseActivity
import com.hoon.datingapp.presentation.view.login.LoginActivity
import com.hoon.datingapp.presentation.view.profile.ProfileActivity
import org.koin.android.viewmodel.ext.android.viewModel

internal class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    companion object {

        private const val INTENT_KEY_PROFILE_NAME = "profile_name"
        private const val INTENT_KEY_PROFILE_IMAGE_URI = "profile_image_uri"

        fun newIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        fun newIntent(context: Context, profileName: String, imageUri: String) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(INTENT_KEY_PROFILE_NAME, profileName)
                putExtra(INTENT_KEY_PROFILE_IMAGE_URI, imageUri)
            }
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override val viewModel by viewModel<MainViewModel>()

    override fun observeData() {
        viewModel.mainStateLiveData.observe(this) {
            when (it) {
                is MainState.UnInitialized -> {
                    initViews()
                }
                is MainState.Loading -> {
                    handleLoadingState()
                }
                is MainState.Login -> {
                    handleLoginState()
                }
                is MainState.Logout -> {
                    handleLogoutState()
                }
                is MainState.SuccessLogin.NewProfile -> {
                    handleNewProfileState() // 프로필 설정창으로 이동
                }
                is MainState.SuccessLogin.ExistingProfile -> {
                    handleExistingProfileState(it.userProfile) // 기존 회원인 경우
                }
                is MainState.Error -> {
                    handleErrorState(it.message)
                }
                is MainState.SuccessUpload -> {
                    handleSuccessUploadState(it.name, it.imageUri)
                }
            }
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.visibility = View.VISIBLE
    }

    private fun handleErrorState(errorMsg: String) {
        toast(errorMsg)
        viewModel.signOut()
    }

    private fun initViews() = with(binding) {
        // set bottom navigation view
        val navHostFragment =
            supportFragmentManager.findFragmentById(navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.findNavController()
        bottomNavigationView.setupWithNavController(navController)

        // binding logout btn
        tvLogout.setOnClickListener { viewModel.signOut() }
    }

    private fun handleLogoutState() {
        toast("로그아웃 되었습니다. 로그인 페이지로 이동합니다.")
        startActivity(LoginActivity.newIntent(this))
    }

    private fun handleLoginState() {
        val uid = getCurrentUserId()
        viewModel.checkProfileIfNewUser(uid)
    }

    private fun handleExistingProfileState(userProfile: UserProfile) {
        val uri = userProfile.imageURI
        setUserIcon(uri.toUri())
        binding.progressBar.visibility = View.GONE
    }

    private fun handleNewProfileState() {
        toast(getString(R.string.need_create_profile))
        val intent = ProfileActivity.newIntent(this@MainActivity)
        profileResultLauncher.launch(intent)
    }

    private val profileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data

                if (intent != null) {
                    val name = intent.getStringExtra(INTENT_KEY_PROFILE_NAME).orEmpty()
                    val imageUri = intent.getStringExtra(INTENT_KEY_PROFILE_IMAGE_URI).orEmpty()

                    // saveUserProfile
                    viewModel.uploadPhoto(getCurrentUserId(), imageUri.toUri(), name)
                }
            }
        }

    private fun handleSuccessUploadState(name: String, imageUri: Uri) {
        viewModel.updateUserProfile(getCurrentUserId(), name, imageUri)
        setUserIcon(imageUri)
        binding.progressBar.visibility = View.GONE
    }

    private fun setUserIcon(uri: Uri) {
        Glide
            .with(this)
            .load(uri)
            .into(binding.imageBtnProfile)
        binding.imageBtnProfile.clipToOutline = true
    }

    private fun getCurrentUserId(): String {
        val uid = viewModel.getCurrentUserID()

        if (uid == null) {
            toast(getString(R.string.status_not_login))
            startActivity(LoginActivity.newIntent(this))
        }
        return uid!!
    }

}