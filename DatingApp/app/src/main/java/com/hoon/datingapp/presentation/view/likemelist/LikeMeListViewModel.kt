package com.hoon.datingapp.presentation.view.likemelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.Job

internal class LikeMeListViewModel(
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    private var _likeMeListStatusLiveData = MutableLiveData<LikeMeListState>(LikeMeListState.UnInitialized)
    val likeMeListStatusLiveData: LiveData<LikeMeListState> = _likeMeListStatusLiveData

    override fun fetchData(): Job {
        TODO("Not yet implemented")
    }

    fun getCurrentUserID(): String? {
        return preferenceManager.getCurrentUserID()
    }

    private fun setState(state: LikeMeListState) {
        _likeMeListStatusLiveData.postValue(state)
    }
}