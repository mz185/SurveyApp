package com.example.surveyapp.ui

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.collections.set

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionsRepository: QuestionsRepository
) : ViewModel() {

    private val _message = MutableLiveData<Message?>()

    private val _buttonUiState = MutableStateFlow(ButtonUiState())
    private val _pagerUiState = MutableStateFlow(PagerUiState())

    private val _submittedAnswers = mutableMapOf<Int, String>()

    private var _currentQuestion = Question()
        set(value) {
            field = value
            updateSubmitButtonState()
            _pagerUiState.update { uiState ->
                uiState.copy(
                    isPreviousEnabled = _counter != 1,
                    isNextEnabled = _counter != _questions.size,
                    counterText = "$_counter/${_questions.size}",
                    currentQuestion = value.question,
                    currentAnswer = getCurrentAnswerOrEmpty()
                )
            }
        }

    private var _questions: Map<Question, Answer> = mapOf()
        set(value) {
            field = value
            _counter = 1
        }

    private var _counter: Int = 0
        set(value) {
            field = value
            _currentQuestion =
                _questions.keys.elementAt(value - 1)
        }

    private fun updateSubmitButtonState(questionId: Int? = null) {
        _buttonUiState.update {
            it.copy(
                text =
                if (questionHasAnswer())
                    "Already submitted"
                else "Submit",
                isEnabled = !questionHasAnswer(questionId)
            )
        }
    }

    private fun questionHasAnswer(questionId: Int? = null): Boolean {
        return _submittedAnswers[questionId ?: _currentQuestion.id] != null
    }

    private fun getCurrentAnswerOrEmpty(): String {
        return _submittedAnswers[_currentQuestion.id] ?: ""
    }

    fun loadQuestions() {
        viewModelScope.launch {
            questionsRepository.loadQuestions().onSuccess { questions ->
                if (questions.isNotEmpty())
                    _questions =
                        questions.associateBy({Question(it.id, it.question)}, { Answer(it.id) })
            }.onFailure {
                _message.postValue(
                    Message(false, it.message ?: "Could not load questions")
                )
            }
        }
    }

    private fun submitAnswer(answer: Answer) {
        viewModelScope.launch {
            questionsRepository.submitAnswer(answer).onSuccess {
                _submittedAnswers[answer.id] = answer.answer

                _pagerUiState.update {
                    it.copy(
                        submittedNoText = _submittedAnswers.size.toString(),
                        currentAnswer = getCurrentAnswerOrEmpty()
                    )
                }

                updateSubmitButtonState(answer.id)
                _message.postValue(Message(true, "Success"))
            }.onFailure {
                if (_currentQuestion.id == answer.id)
                    updateSubmitButtonState()
                _message.postValue(Message(false, "Failure!")  {
                    submitAnswer(Answer(answer.id, answer.answer))
                })
            }
        }
    }

    fun submitBtnPressed(answerText: String) {
        _buttonUiState.update {
            it.copy(isEnabled = false)
        }

        submitAnswer(Answer(_currentQuestion.id, answerText))
    }

    fun previousBtnPressed() {
        if (_counter > 1)
            _counter -= 1
    }

    fun nextBtnPressed() {
        if (_counter < _questions.size)
            _counter += 1
    }

    fun answerTextChanged(text: Editable?) {
        _buttonUiState.update {
            it.copy(
                isEnabled = !text.isNullOrEmpty() &&
                        !questionHasAnswer()
            )
        }
    }

    fun getButtonUiState(): StateFlow<ButtonUiState> {
        return _buttonUiState
    }

    fun getPagerUiState(): StateFlow<PagerUiState> {
        return _pagerUiState
    }

    fun getMessage(): LiveData<Message?> {
        return _message
    }

    fun messageShown() {
        _message.postValue(null)
    }
}