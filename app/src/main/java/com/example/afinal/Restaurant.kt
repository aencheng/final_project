package com.example.afinal

import com.google.firebase.firestore.PropertyName

data class Restaurant(
    @get:PropertyName("name") @set:PropertyName("name")
    var restaurantName: String = "",
)
