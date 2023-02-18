package com.example.final_project_afeka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R


class MapFragment :  Fragment(R.layout.fragment_map){

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewMap).setContent {
            Box(Modifier.fillMaxSize().background(Color.Blue)){

            }
        }

    }
}