package com.hoon.datingapp.ui.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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