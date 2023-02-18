package com.example.final_project_afeka.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.login.LoginActivity
import com.example.final_project_afeka.ui.pages.HomePage


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewHome).setContent {
            HomePage(
                viewModel = viewModel,
                onBack = {
                    activity?.startActivity(Intent(requireActivity(),
                        LoginActivity::class.java))
                    activity?.finish()
                },
                goToMap = {
                    findNavController().navigate(R.id.action_to_mapFragment)
                },
                startDriving = {
                    findNavController().navigate(R.id.action_to_driveFragment)
                }
            )
        }

    }
}