package com.example.memory_ohad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import com.example.memory_ohad.MainViewModel
import com.example.memory_ohad.R
import com.example.memory_ohad.ui.pages.GamePage
import com.example.memory_ohad.ui.pages.WelcomePage


class GameFragment : Fragment(R.layout.fragment_game) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewGame).setContent {
            GamePage()
        }

    }
}