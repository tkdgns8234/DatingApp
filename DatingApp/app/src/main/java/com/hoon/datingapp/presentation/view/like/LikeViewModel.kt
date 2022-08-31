package com.hoon.datingapp.presentation.view.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.Job

internal class LikeViewModel(
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    private var _likeStatusLiveData = MutableLiveData<LikeState>(LikeState.UnInitialized)
    val likeStatusLiveData: LiveData<LikeState> = _likeStatusLiveData

    override fun fetchData(): Job {
        TODO("Not yet implemented")
    }

    fun getCurrentUserID(): String? {
        return preferenceManager.getCurrentUserID()
    }

    private fun setState(state: LikeState) {
        _likeStatusLiveData.postValue(state)
    }
}