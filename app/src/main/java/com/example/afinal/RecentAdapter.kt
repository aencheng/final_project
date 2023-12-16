package com.example.afinal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.databinding.RestaurantItemBinding

class RecentAdapter : ListAdapter<Restaurant, RecentAdapter.RestaurantItemViewHolder>(RestaurantDiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantItemViewHolder {
        return RestaurantItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: RestaurantItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class RestaurantItemViewHolder(private val binding: RestaurantItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): RestaurantItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RestaurantItemBinding.inflate(layoutInflater, parent, false)
                return RestaurantItemViewHolder(binding)
            }
        }

        // Update the bind method to accept a Restaurant object
        fun bind(restaurant: Restaurant) {
            binding.restaurantTitle.text = restaurant.restaurantName
            // If imageUrl is a property of Restaurant, you can set it here
            // binding.restaurantImage.loadImage(restaurant.imageUrl)
        }
    }
}
