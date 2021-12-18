package yapp.android1.delibuddy.ui.partyInformation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yapp.android1.delibuddy.base.BaseViewModel
import yapp.android1.delibuddy.base.RetryAction
import yapp.android1.delibuddy.model.Comment
import yapp.android1.delibuddy.model.Event
import yapp.android1.delibuddy.model.Party
import yapp.android1.delibuddy.ui.partyInformation.PartyInformationViewModel.PartyInformationEvent
import yapp.android1.domain.NetworkResult
import yapp.android1.domain.entity.NetworkError
import yapp.android1.domain.interactor.usecase.FetchPartyCommentsUseCase
import yapp.android1.domain.interactor.usecase.FetchPartyInformationUseCase
import javax.inject.Inject

@HiltViewModel
class PartyInformationViewModel @Inject constructor(
    private val fetchPartyInformationUseCase: FetchPartyInformationUseCase,
    private val fetchPartyCommentsUseCase: FetchPartyCommentsUseCase
) : BaseViewModel<PartyInformationEvent>() {

    private val _isOwner = MutableStateFlow<Boolean>(false)
    val isOwner = _isOwner.asStateFlow()

    private val _party = MutableStateFlow<Party>(Party.EMPTY)
    val party = _party.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    sealed class PartyInformationEvent : Event {
        class OnIntent(val data: Party) : PartyInformationEvent()
        class OnCommentSend(val body: String) : PartyInformationEvent()
        class OnStatusChanged(val status: String) : PartyInformationEvent()
    }

    override suspend fun handleEvent(event: PartyInformationEvent) {
        when(event) {
            is PartyInformationEvent.OnIntent -> {
                _party.value = event.data
                fetchPartyInformation(_party.value.id)
                fetchPartyComments(_party.value.id)
            }
        }
    }

    private fun fetchPartyInformation(partyId: Int) = viewModelScope.launch {
        val result = fetchPartyInformationUseCase.invoke(partyId)
        when(result) {
            is NetworkResult.Success -> {
                val party = Party.toParty(result.data)
                _party.value = party
            }
            is NetworkResult.Error -> {
               handleError(result, null)
            }
        }
    }

    private fun fetchPartyComments(partyId: Int) = viewModelScope.launch {
        val result = fetchPartyCommentsUseCase.invoke(partyId)
        when(result) {
            is NetworkResult.Success -> {
                val comments = result.data.map { comment ->
                    Comment.fromCommentEntity(comment)
                }
                _comments.value = comments
            }
            is NetworkResult.Error -> {
                handleError(result, null)
            }
        }
    }

    override suspend fun handleError(result: NetworkResult.Error, retryAction: RetryAction?) {
        when(result.errorType) {
            is NetworkError.Unknown -> {
                showToast("알 수 없는 에러가 발생했습니다.")
            }
            is NetworkError.Timeout -> {
                showToast("타임 아웃 에러가 발생했습니다.")
            }
            is NetworkError.InternalServer -> {
                showToast("내부 서버에서 오류가 발생했습니다.")
            }
            is NetworkError.Unknown -> {
                showToast("알수없는 에러 발생")
            }
        }
    }
}