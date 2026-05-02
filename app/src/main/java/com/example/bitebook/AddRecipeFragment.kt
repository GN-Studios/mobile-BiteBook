package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bitebook.ui.theme.BiteBookTheme

import androidx.navigation.fragment.navArgs

class AddRecipeFragment : Fragment() {
    private val args: AddRecipeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    AddRecipeScreen(
                        recipeId = args.recipeId,
                        onCancel = { findNavController().navigateUp() },
                        onCreate = { recipe ->
                            findNavController().navigateUp()
                        }
                    )
                }
            }
        }
    }
}
