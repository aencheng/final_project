package com.example.afinal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

class RestaurantViewModel : ViewModel(){
    private var auth: FirebaseAuth = Firebase.auth
    private val tag = "RestaurantViewModel"
    var user: User? = null
    var verifyPassword = ""
    private val _restaurants: MutableLiveData<MutableList<Restaurant>> = MutableLiveData()
    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants as LiveData<List<Restaurant>>

    init {
        initializeStorage()
        _restaurants.value = mutableListOf()
    }

    fun initializeStorage() {
        val firestoreDB = FirebaseFirestore.getInstance()
        val currentUser = getCurrentUser()

        currentUser?.uid?.let { userId ->
            // Directly query the "images" subcollection based on the UID
            val imagesReference = firestoreDB
                .collection("restaurants")
                .document(userId) // Document ID is the user's UID
                .collection("restaurant_name")
                .limit(30)
                .orderBy("creation_time_ms", Query.Direction.DESCENDING)

            imagesReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e(tag, "Exception when querying images", exception)
                    return@addSnapshotListener
                }

                val imageList = snapshot.toObjects<Restaurant>()
                _restaurants.value = imageList.toMutableList()

                for (image in imageList) {
                    Log.i(tag, "Image $image")
                }
            }
        }
    }

    fun clearImages() {
        _restaurants.value = mutableListOf()
    }

    fun signOut() {
        Log.i("Signing Out","Signing Out")
        FirebaseAuth.getInstance().signOut()
        user = null
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}