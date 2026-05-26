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
import com.example.bitebook.data.RegisterRequest
import com.example.bitebook.ui.SignUpScreen
import com.example.bitebook.ui.theme.BiteBookTheme

class SignUpFragment : Fragment() {
    private val viewModel: BiteBookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    SignUpScreen(
                        onSignUpSuccess = { username, name, email, password ->
                            viewModel.register(
                                RegisterRequest(
                                    username = username,
                                    email = email,
                                    password = password
                                ),
                                onSuccess = {
                                    findNavController().navigate(R.id.homeFragment)
                                },
                                onError = { error ->
                                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onNavigateToLogin = {
                            findNavController().navigate(R.id.loginFragment)
                        }
                    )
                }
            }
        }
    }
}
