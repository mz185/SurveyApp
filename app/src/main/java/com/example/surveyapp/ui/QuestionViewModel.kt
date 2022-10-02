package com.example.surveyapp.ui

import android.text.Editable
import androidx.lifecycle.*
import com.example.surveyapp.data.models.Answer
import com.example.surveyapp.data.models.Message
import com.example.surveyapp.data.models.Question
import com.example.surveyapp.data.repos.QuestionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionsRepository: QuestionsRepository
) : ViewModel() {

    private val _message = MutableLiveData<Message?>()
    private val _submitBtnText = MutableLiveData<String>()
    private val _submittedAnswers = MutableLiveData(listOf<Answer>())
    private val _answerInput = MutableLiveData<String?>()
    private val _pagerUiState = MutableStateFlow(PagerUiState())
    private var _currentQuestion = Question()

    private var _questions: List<Question>? = null
        set(value) {
            field = value
            _counter = 1
        }

    private var _counter: Int = 0
        set(value) {
            field = value
            _questions?.run {
                _currentQuestion = get(value - 1)

                updateSubmitBtnText()

                _pagerUiState.update { uiState ->
                    uiState.copy(
                        isPreviousEnabled = value != 1,
                        isNextEnabled = value != size,
                        counterText = "$value/${size}",
                        currentQuestion = _currentQuestion.question,
                        currentAnswer = getCurrentAnswerOrEmpty()
                    )
                }
            }
        }

    init {
        loadQuestions()
    }

    private fun getCurrentAnswerOrEmpty(): String {
        return _submittedAnswers.value?.firstOrNull {
            it.id == _currentQuestion.id
        }?.answer ?: ""
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            questionsRepository.loadQuestions().onSuccess {
                if (it.isNotEmpty())
                    _questions = it
            }.onFailure {
                _message.postValue(
                    Message(false, it.message ?: "Could not load questions")
                )
            }
        }
    }

    private fun updateSubmitBtnText() {
        _submitBtnText.value =
            if (questionHasAnswer())
                "Already submitted"
            else "Submit"
    }

    private fun questionHasAnswer(questionId: Int?  = null): Boolean {
        return _submittedAnswers.value?.firstOrNull {
            it.id == (questionId ?: _currentQuestion.id)
        } != null
    }

    private fun updateSubmittedAnswers(answer: Answer) {
        _submittedAnswers.value?.let { submittedAnswers ->
            if (answer in submittedAnswers)
                _submittedAnswers.value = submittedAnswers - answer
            else _submittedAnswers.value = submittedAnswers + answer

            submittedAnswers.firstOrNull {
                it.id == _currentQuestion.id
            }?.answer?.let {
                _answerInput.value = it
            }
        }
    }

    private fun submitAnswer(answer: Answer) {
        viewModelScope.launch {
            questionsRepository.submitAnswer(answer).onSuccess {
                updateSubmitBtnText()
                _pagerUiState.update {
                    it.copy(currentAnswer = getCurrentAnswerOrEmpty())
                }
                _message.postValue(Message(true, "Success"))
            }.onFailure {
                updateSubmittedAnswers(answer)
                _message.postValue(Message(false, "Failure!")  {
                    if (!questionHasAnswer(answer.id))
                        submitBtnPressed(answer.answer, answer.id)
                })
            }
        }
    }

    fun submitBtnPressed(answerText: String, questionId: Int? = null) {
        if (!questionHasAnswer()) {
            val answer = Answer(questionId ?: _currentQuestion.id, answerText)
            updateSubmittedAnswers(answer)
            submitAnswer(answer)
        }
    }

    fun previousBtnPressed() {
        if (_counter > 1)
            _counter -= 1
    }

    fun nextBtnPressed() {
        _questions?.size?.let {
            if (_counter < it)
                _counter += 1
        }
    }

    fun answerTextChanged(text: Editable?) {
        _answerInput.value = text?.toString()
    }

    fun getSubmitBtnAvailability(): LiveData<Boolean> {
        return Transformations.map(_answerInput) {
            !it.isNullOrEmpty() &&
                    !questionHasAnswer()
        }
    }

    fun getSubmitButtonText(): LiveData<String> {
        return _submitBtnText
    }

    fun getPagerUiState(): StateFlow<PagerUiState> {
        return _pagerUiState
    }

    fun getTotalQuestionsSubmitted(): LiveData<String> {
        return Transformations.map(_submittedAnswers) {
            it.size.toString()
        }
    }

    fun getMessage(): LiveData<Message?> {
        return _message
    }

    fun messageShown() {
        _message.postValue(null)
    }
}