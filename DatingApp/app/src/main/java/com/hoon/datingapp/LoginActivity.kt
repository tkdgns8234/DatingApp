package com.hoon.datingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.databinding.ActivityLoginBinding
import com.hoon.datingapp.util.Constants
import com.hoon.datingapp.util.DBKey


class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var callbackManager: CallbackManager

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        initLoginBtn()
        initSignUpBtn()
        initEmailAndPasswordEditText()
        binding.btnFacebookLogin.setOnClickListener { initFacebookLoginBtn() }
    }

    private fun initFacebookLoginBtn() {
        binding.btnFacebookLogin.setPermissions(Constants.FACEBOOK_INFO_EMAIL, Constants.FACEBOOK_INFO_PUBLIC_PROFILE) // 로그인 후 가져올 정보 기입
        binding.btnFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // 로그인 성공
                    // 로그인 access 토큰을 facebook으로부터 가져와서 Firebase로 넘겨줘야한다.
                    loginWithFacebook(result)
                }

                override fun onCancel() {
                    // 로그인 취소
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_failed_facebook), Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun loginWithFacebook(result: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    handleLoginResult()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed_facebook),
                        Toast.LENGTH_SHORT
                    )
                }
            }
    }


    // email, pw 가 비어있으면 Firebase 로그인 시 에러 발생 -> 비어있으면 버튼 비활성화
    private fun initEmailAndPasswordEditText() {
        binding.etEmail.addTextChangedListener {
            val enable = binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()
            binding.btnLogin.isEnabled = enable
            binding.btnSignUp.isEnabled = enable
        }

        binding.etPassword.addTextChangedListener {
            val enable = binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()
            binding.btnLogin.isEnabled = enable
            binding.btnSignUp.isEnabled = enable
        }
    }

    // login with firebase
    private fun initLoginBtn() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pw = binding.etPassword.text.toString()

            auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    handleLoginResult()
                } else {
                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    //signup with firebase
    private fun initSignUpBtn() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pw = binding.etPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.signup_success_click_login_button), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLoginResult() {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            return
        }
        val uid = auth.currentUser?.uid.orEmpty()
        val currentUserDB =
            Firebase.database.reference.child(DBKey.DB_NAME)
                .child(DBKey.USERS).child(uid)
        val user = mutableMapOf<String, Any>()
        user[DBKey.USER_ID] = uid
        currentUserDB.updateChildren(user)

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}