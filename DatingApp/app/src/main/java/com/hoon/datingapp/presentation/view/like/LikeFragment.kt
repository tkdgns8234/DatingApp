package com.hoon.datingapp.presentation.view.like

import android.view.View
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.presentation.adapter.CardItemAdapter
import com.hoon.datingapp.R
import com.hoon.datingapp.databinding.FragmentLikeBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.BaseFragment
import com.hoon.datingapp.presentation.view.login.LoginActivity
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import org.koin.android.viewmodel.ext.android.viewModel

internal class LikeFragment : BaseFragment<LikeViewModel, FragmentLikeBinding>() {

    override fun getViewBinding() =
        FragmentLikeBinding.inflate(layoutInflater)

    override val viewModel by viewModel<LikeViewModel>()

    private val cardStackAdapter = CardItemAdapter()
    private val userProfiles = mutableListOf<UserProfile>()
    private lateinit var cardStackLayoutManager: CardStackLayoutManager

    override fun observeData() {
        viewModel.likeStatusLiveData.observe(this) {
            when (it) {
                is LikeState.UnInitialized -> {
                    initViews()
                    viewModel.fetchData()
                }
                is LikeState.Logout -> {
                    handleLogoutState()
                }
                is LikeState.Callback.NewUser -> {
                    handleNewUserCallback(it.profile)
                }
                is LikeState.Callback.ChangedUser -> {
                    handleChangedUserCallback(it.profile)
                }
            }
        }
    }

    private fun initViews() {
        cardStackLayoutManager = CardStackLayoutManager(context, CardStackListener())
        cardStackLayoutManager.setStackFrom(StackFrom.Top)
        binding.cardStackView.layoutManager = cardStackLayoutManager
        binding.cardStackView.adapter = cardStackAdapter
    }

    inner class CardStackListener : com.yuyakaido.android.cardstackview.CardStackListener {
        override fun onCardDragging(direction: Direction?, ratio: Float) {}
        override fun onCardSwiped(direction: Direction?) {
            when (direction) {
                Direction.Left -> disLike()
                Direction.Right -> like()
            }
        }

        override fun onCardRewound() {}
        override fun onCardCanceled() {}
        override fun onCardAppeared(view: View?, position: Int) {}
        override fun onCardDisappeared(view: View?, position: Int) {}
    }

    private fun like() {
        val otherUserProfile =
            userProfiles[cardStackLayoutManager.topPosition - 1]
        userProfiles.remove(otherUserProfile)

        cardStackAdapter.submitList(userProfiles.toMutableList())

        // 상대방의 userID의 like에 나의 id를 저장
        viewModel.like(otherUserProfile.userID)

        toast("${otherUserProfile.userName}님을 like 하셨습니다.")
    }

    private fun disLike() {
        val otherUserProfile =
            userProfiles[cardStackLayoutManager.topPosition - 1]
        userProfiles.remove(otherUserProfile)

        cardStackAdapter.submitList(userProfiles)

        // 상대방의 userID의 dislike에 나의 id를 저장
        viewModel.disLike(otherUserProfile.userID)

        toast("${otherUserProfile.userName}님을 dis like 하셨습니다.")
    }

    private fun handleLogoutState() {
        toast(getString(R.string.status_not_login))
        startActivity(LoginActivity.newIntent(requireContext()))
    }

    private fun handleNewUserCallback(profile: UserProfile) {
        val findResult = userProfiles.find { it.userID == profile.userID }
        findResult ?: kotlin.run {
            userProfiles.add(profile)
            cardStackAdapter.submitList(userProfiles.toMutableList())
        }
    }

    private fun handleChangedUserCallback(profile: UserProfile) {
        val findResult = userProfiles.find { it.userID == profile.userID }

        findResult?.let {
            it.userName = profile.userID
            it.imageURI = profile.imageURI
        }
        cardStackAdapter.submitList(userProfiles.toMutableList())
    }
}