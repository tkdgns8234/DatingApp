package com.hoon.datingapp.presentation.view.login

import android.content.Context
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivityLoginBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.BaseActivity
import com.hoon.datingapp.presentation.view.main.MainActivity
import com.hoon.datingapp.presentation.view.signup.SignUpActivity
import com.hoon.datingapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

internal class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    companion object {
        fun newIntent(context: Context) =
            Intent(context, LoginActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                ) // task clear
            }
    }

    override fun getViewBinding() =
        ActivityLoginBinding.inflate(layoutInflater)

    override val viewModel by viewModel<LoginViewModel>()

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun observeData() {
        viewModel.loginStatusLiveData.observe(this) {
            when (it) {
                is LoginState.Uninitialized -> {
                    initViews()
                }
                is LoginState.Success -> {
                    toast(getString(R.string.login_success))
                    startActivity(MainActivity.newIntent(this))
                }
                is LoginState.Error -> {
                    toast(getString(R.string.login_failed))
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        btnFacebookLogin.setOnClickListener { initFacebookLoginBtn() }
        btnCreateAuth.setOnClickListener {
            startActivity(SignUpActivity.newIntent(this@LoginActivity))
        }
    }

    private fun initFacebookLoginBtn() {
        val loginManager = LoginManager.getInstance()
        val callbackManager = CallbackManager.Factory.create()

        loginManager.logInWithReadPermissions(
            this,
            callbackManager,
            mutableListOf(Constants.FACEBOOK_INFO_EMAIL, Constants.FACEBOOK_INFO_PUBLIC_PROFILE)
        )

        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                // 로그인 성공
                override fun onSuccess(result: LoginResult) {
                    // 로그인 access 토큰을 facebook으로부터 가져와서 Firebase로 넘겨줘야한다.
                    loginWithFacebook(result)
                }

                // 로그인 취소
                override fun onCancel() {
                    toast(getString(R.string.login_canceled))
                }

                override fun onError(error: FacebookException) {
                    toast(getString(R.string.login_failed_facebook))
                }
            })
    }

    private fun loginWithFacebook(result: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    viewModel.putCurrentUserID(auth.currentUser?.uid)
                } else {
                    toast(getString(R.string.login_failed_facebook))
                }
            }
    }
}