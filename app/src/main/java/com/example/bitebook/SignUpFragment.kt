package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bitebook.ui.SignUpScreen
import com.example.bitebook.ui.theme.BiteBookTheme

class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    SignUpScreen(
                        onSignUpSuccess = {
                            findNavController().navigate(R.id.homeFragment)
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
