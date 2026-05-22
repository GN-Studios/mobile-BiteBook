package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bitebook.ui.LoginScreen
import com.example.bitebook.ui.theme.BiteBookTheme

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    LoginScreen(
                        onLoginSuccess = {
                            findNavController().navigate(R.id.homeFragment)
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
