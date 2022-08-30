package com.hoon.datingapp.di

import android.net.Uri
import com.hoon.datingapp.data.db.FirebaseRealtimeDB
import com.hoon.datingapp.data.db.FirebaseStorage
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.data.repository.FirebaseRepository
import com.hoon.datingapp.data.repository.FirebaseRepositoryImpl
import com.hoon.datingapp.domain.CheckIsNewProfileUseCase
import com.hoon.datingapp.domain.GetUserProfileUseCase
import com.hoon.datingapp.domain.UploadPhotoUseCase
import com.hoon.datingapp.domain.UpdateUserProfileUseCase
import com.hoon.datingapp.presentation.view.login.LoginViewModel
import com.hoon.datingapp.presentation.view.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.IO }

    // viewmodel
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }

    // domain
    single { CheckIsNewProfileUseCase(get()) }
    single { GetUserProfileUseCase(get()) }
    single { UpdateUserProfileUseCase(get()) }
    single { UploadPhotoUseCase(get()) }

    // db
    single { PreferenceManager(androidApplication()) }

    single<FirebaseRepository> { FirebaseRepositoryImpl(get(), get(), get()) }
    single { FirebaseRealtimeDB() }
    single { FirebaseStorage() }

//    suspend fun updateUserProfile(uid: String, name: String, imageUri: Uri)
//    suspend fun uploadPhotoUri(uid: String, imageUri: Uri) : Uri)
}