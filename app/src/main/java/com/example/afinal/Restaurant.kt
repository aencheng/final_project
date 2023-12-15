package com.example.afinal

import com.google.firebase.firestore.PropertyName

data class Restaurant(
    @get:PropertyName("restaurant_name") @set:PropertyName("restaurant_name")
    var restaurantName: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms")
    var creationTimeMs: Long = 0
)
