package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bitebook.ui.theme.BiteBookTheme

class ProfileFragment : Fragment() {
    private val viewModel: BiteBookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    ProfileScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipeId ->
                            val bundle = Bundle().apply { putString("recipeId", recipeId) }
                            findNavController().navigate(R.id.recipeDetailFragment, bundle)
                        },
                        onEditClick = { recipeId ->
                            val bundle = Bundle().apply { putString("recipeId", recipeId) }
                            findNavController().navigate(R.id.addRecipeFragment, bundle)
                        },
                        onDeleteClick = { recipeId ->
                            viewModel.deleteRecipe(recipeId)
                        }
                    )
                }
            }
        }
    }
}
