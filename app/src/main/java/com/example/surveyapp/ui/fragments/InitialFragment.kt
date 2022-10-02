package com.example.surveyapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.surveyapp.BaseFragment
import com.example.surveyapp.databinding.FragmentInitialBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

@AndroidEntryPoint
class InitialFragment : BaseFragment<FragmentInitialBinding>() {

    override fun bind() = FragmentInitialBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.startButton?.setOnClickListener {
            findNavController().navigate(
                InitialFragmentDirections.actionInitialToQuestion()
            )
        }
    }
}