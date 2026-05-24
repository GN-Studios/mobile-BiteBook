package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bitebook.data.LoginRequest
import com.example.bitebook.ui.LoginScreen
import com.example.bitebook.ui.theme.BiteBookTheme

class LoginFragment : Fragment() {
    private val viewModel: BiteBookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    val authError by viewModel.authError.collectAsState()
                    
                    LoginScreen(
                        onLoginSuccess = { username, password ->
                            viewModel.login(
                                LoginRequest(username, password),
                                onSuccess = {
                                    findNavController().navigate(R.id.homeFragment)
                                },
                                onError = { _ ->
                                    // Error is handled via authError state collection
                                }
                            )
                        },
                        onNavigateToSignUp = {
                            viewModel.clearAuthError()
                            findNavController().navigate(R.id.signUpFragment)
                        },
                        errorMessage = authError,
                        onClearError = {
                            viewModel.clearAuthError()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearAuthError()
    }
}
