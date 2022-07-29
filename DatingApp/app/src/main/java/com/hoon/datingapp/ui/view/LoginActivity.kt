package com.hoon.datingapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivityLoginBinding
import com.hoon.datingapp.util.Constants
import com.hoon.datingapp.util.DBKey

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    lateinit var auth: FirebaseAuth
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        binding.btnFacebookLogin.setOnClickListener { initFacebookLoginBtn() }
        binding.btnCreateAuth.setOnClickListener {
            startActivity(Intent(this, SignUpAndLoginActivity::class.java))
        }
    }

    private fun initFacebookLoginBtn() {
        val loginManager = LoginManager.getInstance()

        loginManager.logInWithReadPermissions(
            this,
            mutableListOf(Constants.FACEBOOK_INFO_EMAIL, Constants.FACEBOOK_INFO_PUBLIC_PROFILE)
        )

        loginManager.registerCallback(
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
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed_facebook),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loginWithFacebook(result: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
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

    private fun handleLoginResult() {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            return
        }
        val uid = auth.currentUser?.uid.orEmpty()
        val currentUserDB =
            Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS).child(uid)
        currentUserDB.child(DBKey.USER_ID).setValue(uid)

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}