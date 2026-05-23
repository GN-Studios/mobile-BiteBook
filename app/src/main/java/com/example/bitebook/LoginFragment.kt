package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                    LoginScreen(
                        onLoginSuccess = { username, password ->
                            viewModel.login(
                                LoginRequest(username, password),
                                onSuccess = {
                                    findNavController().navigate(R.id.homeFragment)
                                },
                                onError = { error ->
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onNavigateToSignUp = {
                            findNavController().navigate(R.id.signUpFragment)
                        }
                    )
                }
            }
        }
    }
}
