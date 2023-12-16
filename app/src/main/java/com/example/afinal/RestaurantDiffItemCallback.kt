package com.example.afinal

import androidx.recyclerview.widget.DiffUtil

class RestaurantDiffItemCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant)
            = (oldItem.restaurantName == newItem.restaurantName)
    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant) = (oldItem == newItem)
}