package com.hoon.datingapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivitySignupAndLoginBinding
import com.hoon.datingapp.util.DBKey

class SignUpAndLoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupAndLoginBinding.inflate(layoutInflater)
    }

    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        initLoginBtn()
        initSignUpBtn()
        initEmailAndPasswordEditText()
    }

    // email, pw 가 비어있으면 Firebase 로그인 시 에러 발생 -> 비어있으면 버튼 비활성화
    private fun initEmailAndPasswordEditText() {
        binding.etEmail.addTextChangedListener {
            val enable =
                (binding.etEmail.text.isNullOrEmpty() && binding.etPassword.text.isNullOrEmpty()).not()
            binding.btnLogin.isEnabled = enable
            binding.btnSignUp.isEnabled = enable
        }

        binding.etPassword.addTextChangedListener {
            val enable =
                (binding.etEmail.text.isNullOrEmpty() && binding.etPassword.text.isNullOrEmpty()).not()
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
                    Toast.makeText(
                        this,
                        getString(R.string.signup_success_click_login_button),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT)
                        .show()
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


        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}