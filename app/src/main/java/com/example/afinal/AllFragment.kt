package com.example.afinal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.afinal.databinding.FragmentAllBinding
import com.google.firebase.firestore.FirebaseFirestore

class AllFragment : Fragment() {

    private lateinit var allAdapter: AllAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAllBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView and adapter
        allAdapter = AllAdapter()
        binding.recentRecyclerView.adapter = allAdapter
        binding.recentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Retrieve data from Firestore and update the adapter
        fetchDataFromFirestore()

        return view
    }

    private fun fetchDataFromFirestore() {
        // Replace "restaurants" with the actual collection name in your Firestore database
        firestore.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->
                val restaurants = mutableListOf<Restaurant>()
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    // Add other fields as needed
                    val restaurant = Restaurant(name)
                    restaurants.add(restaurant)
                }
                allAdapter.submitList(restaurants)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}