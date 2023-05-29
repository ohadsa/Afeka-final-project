package com.example.final_project_afeka.fragments

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.pages.DrivePage


class DriveFragment : Fragment(R.layout.fragment_drive) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.composeViewDrive).setContent {
            DrivePage(viewModel) {
                viewModel.stopDriving()
                viewModel.backToHome = true
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
}


