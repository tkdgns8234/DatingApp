package com.hoon.datingapp.presentation.view.signup

import android.content.Context
import android.content.Intent
import androidx.core.widget.addTextChangedListener
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.ActivitySignupAndLoginBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.BaseActivity
import com.hoon.datingapp.presentation.view.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity<SignUpViewModel, ActivitySignupAndLoginBinding>() {

    companion object {
        fun newIntent(context: Context) =
            Intent(context, SignUpActivity::class.java)
    }

    override fun getViewBinding(): ActivitySignupAndLoginBinding {
        return ActivitySignupAndLoginBinding.inflate(layoutInflater)
    }

    override val viewModel by viewModel<SignUpViewModel>()

    override fun observeData() {
        viewModel.signUpStateLiveData.observe(this) {
            when (it) {
                is SignUpState.Uninitialized -> {
                    initViews()
                }
                is SignUpState.Success -> {
                    handleSuccessState(it)
                }
                is SignUpState.Error -> {
                    toast(it.message)
                }
            }
        }
    }

    private fun handleSuccessState(state: SignUpState.Success) {
        when (state) {
            is SignUpState.Success.SignUp -> {
                toast(getString(R.string.signup_success_click_login_button))
            }
            is SignUpState.Success.Login -> {
                viewModel.putCurrentUserID()
                toast(getString(R.string.login_success))
                startActivity(MainActivity.newIntent(this))
            }
        }
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
    private fun initEmailAndPasswordEditText() = with(binding) {
        etEmail.addTextChangedListener {
            val enable =
                (etEmail.text.isNullOrEmpty() && etPassword.text.isNullOrEmpty()).not()
            btnLogin.isEnabled = enable
            btnSignUp.isEnabled = enable
        }

        etPassword.addTextChangedListener {
            val enable =
                (etEmail.text.isNullOrEmpty() && etPassword.text.isNullOrEmpty()).not()
            btnLogin.isEnabled = enable
            btnSignUp.isEnabled = enable
        }
    }

    // login with firebase
    private fun initLoginBtn() = with(binding) {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pw = etPassword.text.toString()

            viewModel.login(email, pw)
        }
    }

    //signup with firebase
    private fun initSignUpBtn() = with(binding){
        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString()
            val pw = etPassword.text.toString()

            viewModel.signUp(email, pw)
        }
    }
}