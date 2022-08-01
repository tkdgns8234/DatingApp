
/*
배운점
    - Firebase Auth
        email login
        facebook login
    - Firebase Realtime Database
        - json (key value) 형식으로 데이터 저장
        - DB 데이터 조회 함수 4가지 (리스너를통해 데이터 조회, get() 을 통한 데이터 조회)
            - get() 을 이용하는 경우 캐시영역에서 가져오는게 아니라 서버에서 직접 조회해서 데이터를 가져옴
            -> sync가 무조건 맞아야 하는 경우 사용
            - 유의할 점
                db 조회 시 fragment 의 경우 onViewCreated 생명주기에서 DB 콜백을 등록하면
                리스너가 fragment view가 created 될 떄 마다 등록됨 -> ondestroyView에서 콜백 등록 해제 필요
        
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
	id
		info
		likedby
			match
		chat
			push key
				(ChatRoom 항목들)
				myID
				partnerID
				key
				lastMessage
 chats
	key
		push key
		    (ChatItem 항목들)
			senderid:
			message:
			timestamp:

 */

package com.hoon.datingapp.ui.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.ActivityMainBinding
import com.hoon.datingapp.util.Constants
import com.hoon.datingapp.util.DBKey

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var usersDB: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS)

        setBottomNavigationView()
        setProfileIfNewUser()
        binding.tvLogout.setOnClickListener {
            auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    private fun setBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setProfileIfNewUser() {
        val uid = getCurrentUserId()
        val userDB = usersDB.child(uid)
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 새로 가입한 회원인 경우
                if (snapshot.child(DBKey.USER_NAME).value == null) {
                    // 프로필 설정창으로 이동
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    profileResultLauncher.launch(intent)
                }
                else {
                    // 기존 회원인 경우
                    val uri = snapshot.child(DBKey.USER_IMAGE_URI).value.toString()
                    setUserIcon(uri.toUri())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setUserIcon(uri: Uri) {
        Glide
            .with(this)
            .load(uri)
            .into(binding.imageBtnProfile)
        binding.imageBtnProfile.clipToOutline = true
    }

    private val profileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data

                if (intent != null) {
                    val name = intent.getStringExtra(Constants.INTENT_KEY_PROFILE_NAME).orEmpty()
                    val imageUri =
                        intent.getStringExtra(Constants.INTENT_KEY_PROFILE_IMAGE_URI).orEmpty()

                    saveUserProfile(name, imageUri.toUri())
                }
            }
        }

    private fun saveUserProfile(name: String, imageUri: Uri) {
        uploadPhoto(
            imageUri,
            successHandler = { uri ->
                uploadProfile(name, uri)
                setUserIcon(uri.toUri())
            },
            errorHandler = {
                Toast.makeText(this, "이미지 파일 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun uploadPhoto(
        imageUri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: () -> Unit
    ) {
        val uid = getCurrentUserId()
        val fileName = "${uid}_${System.currentTimeMillis()}.png"
        storage.reference.child(Constants.FIREBASE_STORAGE_PATH_IMAGES).child(fileName)
            .putFile(imageUri)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 저장한 file을 바로 uri로 다운로드 후 DB, 모델클래스에 저장
                    storage.reference.child(Constants.FIREBASE_STORAGE_PATH_IMAGES).child(fileName)
                        .downloadUrl
                        .addOnSuccessListener(this) {
                            successHandler(it.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadProfile(name: String, imageUri: String) {
        val uid = getCurrentUserId()
        val userProfile = UserProfile(uid, name, imageUri)
        usersDB.child(uid).setValue(userProfile)
    }

    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
        return auth.currentUser!!.uid
    }
}