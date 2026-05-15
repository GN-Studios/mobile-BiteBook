package com.example.bitebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bitebook.ui.theme.BiteBookTheme

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BiteBookTheme {
                    HomeScreen(
                        onRecipeClick = { recipeId ->
                            val bundle = Bundle().apply { putString("recipeId", recipeId) }
                            findNavController().navigate(R.id.recipeDetailFragment, bundle)
                        }
                    )
                }
            }
        }
    }
}
