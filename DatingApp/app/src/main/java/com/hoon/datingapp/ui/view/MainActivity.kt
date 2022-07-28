package com.hoon.datingapp.ui.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivityMainBinding
import com.hoon.datingapp.util.Constants
import com.hoon.datingapp.util.DBKey

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var usersDB: DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    private lateinit var userName: String
    private lateinit var userImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS)

        setBottomNavigationView()
        checkIfUserIdExistInDB()
    }

    private fun setBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun checkIfUserIdExistInDB() {
        val uid = auth.currentUser?.uid.orEmpty()
        val userDB = usersDB.child(uid).child(DBKey.USER_NAME)
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // user name이 null인 경우 지금 가입한 회원
                if (snapshot.value == null) {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    profileResultLauncher.launch(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private val profileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data

                if (intent != null) {
                    userName = intent.getStringExtra(Constants.INTENT_KEY_PROFILE_NAME).orEmpty()
                    userImageUri = intent.getStringExtra(Constants.INTENT_KEY_PROFILE_IMAGE_URI).orEmpty()
                }
            }
        }

//    private fun setUserProfile() {
//        setUserName()
//        setUserImage()
//    }
//
//    private fun setUserImage() {
//
//    }
//
//    private fun setUserName() {
//        val currentUserDB = usersDB.child(getCurrentUserId())
//        // realtime db는 listener 를 통해 데이터를 가져옴
//        // addListenerForSingleValueEvent 의 경우 한번 호출되고 콜백이 즉시 삭제됨
//        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.child(DBKey.USER_NAME).value == null) {
//                    showNameInputDialog()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }
//
//    // user name을 입력받는 dialog
//    private fun showNameInputDialog() {
//        val editText = EditText(this)
//
//        AlertDialog.Builder(this)
//            .setTitle("이름을 입력해 주세요")
//            .setView(editText)
//            .setPositiveButton("확인") { _, _ ->
//                if (editText.text.isEmpty()) {
//                    Toast.makeText(this, "이름을 다시 입력해주세요", Toast.LENGTH_SHORT).show()
//                    showNameInputDialog() // positive 버튼 클릭 시 종료되기에 다시 open
//                } else {
//                    saveUserName(editText.text.toString())
//                }
//            }
//            .setCancelable(false)
//            .show()
//    }
//
//    // user name 추가해서 db에 저장
//    private fun saveUserName(name: String) {
//        // 기존 데이터는 유지하고 데이터 추가하는방법이 없는거같네,, 아쉽네
//        val uid = getCurrentUserId()
//        val currentUserDB = usersDB.child(uid)
//        val user = mutableMapOf<String, Any>()
//        user[DBKey.USER_ID] = uid
//        user[DBKey.USER_NAME] = name
//        currentUserDB.updateChildren(user)
//    }
//
//    private fun getCurrentUserId(): String {
//        if (auth.currentUser == null) {
//            Toast.makeText(this, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()
//
//            startActivity(
//                Intent(this, LoginActivity::class.java)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            )
//        }
//
//        return auth.currentUser!!.uid
//    }
}