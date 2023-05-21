package com.example.final_project_afeka.login.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainActivity
import com.example.final_project_afeka.R
import com.example.final_project_afeka.login.ui.composable.WelcomePage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeView).setContent {
            WelcomePage(viewModel = viewModel) {
                viewModel.connect(
                    runWhenSuccess = {
                        goToMainActivity()
                    },
                    runWhenFailure = {
                        Toast.makeText(requireActivity().baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()                    },
                    context = requireActivity()
                )
            }
        }

    }

    private fun goToMainActivity() {
        activity?.startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()

    }
}