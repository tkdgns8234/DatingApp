package com.hoon.datingapp.di

import com.hoon.datingapp.data.db.FirebaseRealtimeDB
import com.hoon.datingapp.data.db.FirebaseStorage
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.data.repository.FirebaseRepository
import com.hoon.datingapp.data.repository.FirebaseRepositoryImpl
import com.hoon.datingapp.data.repository.PreferenceRepository
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.domain.*
import com.hoon.datingapp.domain.CheckIsNewProfileUseCase
import com.hoon.datingapp.domain.GetUserProfileUseCase
import com.hoon.datingapp.domain.SendMessageUseCase
import com.hoon.datingapp.domain.UpdateUserProfileUseCase
import com.hoon.datingapp.domain.UploadPhotoUseCase
import com.hoon.datingapp.presentation.view.chat.ChatViewModel
import com.hoon.datingapp.presentation.view.chatlist.ChatListViewModel
import com.hoon.datingapp.presentation.view.like.LikeViewModel
import com.hoon.datingapp.presentation.view.likemelist.LikeMeListViewModel
import com.hoon.datingapp.presentation.view.login.LoginViewModel
import com.hoon.datingapp.presentation.view.main.MainViewModel
import com.hoon.datingapp.presentation.view.signup.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.IO }

    // viewmodel
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ChatViewModel(get(), get(), get(), get()) }
    viewModel { ChatListViewModel(get(), get(), get()) }
    viewModel { LikeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LikeMeListViewModel(get(), get()) }

    // domain
    single { CheckIsNewProfileUseCase(get()) }
    single { GetUserProfileUseCase(get()) }
    single { UpdateUserProfileUseCase(get()) }
    single { UploadPhotoUseCase(get()) }
    single { SendMessageUseCase(get()) }
    single { TraceChatHistoryUseCase(get()) }
    single { GetMatchedUsersUseCase(get()) }
    single { TraceUsersLikeMeUseCase(get()) }
    single { TraceNewUserAndChangedUserUseCase(get()) }
    single { LikeUserUseCase(get()) }
    single { DisLikeUserUseCase(get()) }
    single { MakeChatRoomIfLikeEachOtherUseCase(get()) }

    // db
    single<FirebaseRepository> { FirebaseRepositoryImpl(get(), get(), get()) }
    single<PreferenceRepository> {PreferenceRepositoryImpl(get())}

    single { PreferenceManager(androidApplication()) }
    single { FirebaseRealtimeDB() }
    single { FirebaseStorage() }

}