// FavoriteFragment.kt
package com.example.pa_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pa_mobile.databinding.FavoritePageBinding

class FavoriteFragment : Fragment() {
    private var _binding: FavoritePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FavoritePageBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewFavorites
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        adapter = FavoriteAdapter(listOf(), favoriteViewModel)
        recyclerView.adapter = adapter

        favoriteViewModel.allFavorites.observe(viewLifecycleOwner, Observer { favorites ->
            favorites?.let {
                adapter.updateFavorites(it)
            }
        })

        binding.buttonFavToKatalog.setOnClickListener {
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToKatalogFragment(0)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
