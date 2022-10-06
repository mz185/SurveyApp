package com.example.surveyapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.surveyapp.BaseFragment
import com.example.surveyapp.databinding.FragmentQuestionBinding
import com.example.surveyapp.ui.QuestionViewModel
import com.example.surveyapp.util.hideKeyboard
import com.example.surveyapp.util.showSnackBar
import com.example.surveyapp.util.updateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionBinding>() {

    private val vm: QuestionViewModel by viewModels()

    override fun bind() = FragmentQuestionBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.loadQuestions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            vm.apply {
                getMessage().observe(viewLifecycleOwner) {
                    it?.let {
                        root.showSnackBar(it)
                        messageShown()
                    }
                }

                getTotalQuestionsSubmitted().observe(viewLifecycleOwner) {
                    it?.let {
                       questionsSubmitted.text = it
                    }
                }

                getSubmitBtnAvailability().observe(viewLifecycleOwner) {
                    it?.let {
                        submitBtn.isEnabled = it
                    }
                }

                getSubmitButtonText().observe(viewLifecycleOwner) {
                    it?.let {
                        submitBtn.text = it
                    }
                }

                previousBtn.setOnClickListener {
                    previousBtnPressed()
                }

                nextBtn.setOnClickListener {
                    nextBtnPressed()
                }

                submitBtn.setOnClickListener {
                    answerEditText.clearFocus()
                    hideKeyboard()
                    submitBtnPressed(answerEditText.text.toString())
                }

                answerEditText.doAfterTextChanged {
                    answerTextChanged(it)
                }

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        getPagerUiState().collect {
                            counterTextView.text = it.counterText
                            previousBtn.updateAvailability(it.isPreviousEnabled)
                            nextBtn.updateAvailability(it.isNextEnabled)
                            answerEditText.setText(it.currentAnswer)
                            questionTextView.text = it.currentQuestion
                        }
                    }
                }
            }
        }
    }
}