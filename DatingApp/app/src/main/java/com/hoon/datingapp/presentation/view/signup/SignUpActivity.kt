package com.hoon.datingapp.presentation.view.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivitySignupAndLoginBinding
import com.hoon.datingapp.presentation.view.main.MainActivity

class SignUpActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) =
            Intent(context, SignUpActivity::class.java)
    }

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
        initBackBtn()
        initEmailAndPasswordEditText()
    }

    private fun initBackBtn() {
        binding.btnBack.setOnClickListener { finish() }
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
                    ).show()
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
//        val uid = auth.currentUser?.uid.orEmpty()
//        val currentUserDB =
//            Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS).child(uid)
//        currentUserDB.child(DBKey.USER_ID).setValue(uid)
// TODO pref 에 로그인 id 추가하는 로직 추가

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}