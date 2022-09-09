package com.hoon.datingapp.presentation.view.likemelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.domain.TraceUsersLikeMeUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class LikeMeListViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl,
    val traceUsersLikeMeUseCase: TraceUsersLikeMeUseCase
) : BaseViewModel() {

    private var _likeMeListStatusLiveData =
        MutableLiveData<LikeMeListState>(LikeMeListState.UnInitialized)
    val likeMeListStatusLiveData: LiveData<LikeMeListState> = _likeMeListStatusLiveData

    fun fetchData(): Job = viewModelScope.launch {
        traceUsersLikeMe()
    }

    fun getCurrentUserID(): String? {
        return preferenceRepositoryImpl.getCurrentUserID()
    }

    fun traceUsersLikeMe() = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            traceUsersLikeMeUseCase(it) {
                setState(LikeMeListState.UpdateUserLikeMe(it))
            }
        } ?: kotlin.run {
            setState(LikeMeListState.Logout)
        }
    }

    private fun setState(state: LikeMeListState) {
        _likeMeListStatusLiveData.value = state
    }

}