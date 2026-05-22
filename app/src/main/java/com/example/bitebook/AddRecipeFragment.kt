package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.example.bitebook.ui.theme.BiteBookTheme

class AddRecipeFragment : Fragment() {
    private val args: AddRecipeFragmentArgs by navArgs()
    private val viewModel: BiteBookViewModel by activityViewModels()

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
                        viewModel = viewModel,
                        onCancel = { findNavController().navigateUp() },
                        onError = { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        },
                        onCreate = {
                            Toast.makeText(requireContext(), "Recipe Created!", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    )
                }
            }
        }
    }
}
