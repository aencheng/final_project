package com.example.afinal
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.afinal.databinding.FragmentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    val viewModel : RestaurantViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fragmentA: RecentFragment
    private lateinit var fragmentB: AllFragment

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser == null) {
            // User is signed out, clear the data
            viewModel.clearImages()
        } else {
            // User is signed in, re-initialize the data
            viewModel.initializeStorage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Get the authenticated user's UID
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        drawerLayout = view.findViewById(R.id.drawerLayout)
        //val bottomNavigationView = view.findViewById<NavigationView>(R.id.navigationView)
        //bottomNavigationView.set

        // Open navigation drawer when the button is clicked
        val navDrawerButton = view.findViewById<ImageView>(R.id.menu_icon)
        navDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val logoutButton = view.findViewById<ImageView>(R.id.signout_icon)
        logoutButton.setOnClickListener{
            viewModel.signOut()
            goToLoginScreen()
        }

        // Load user information from Firestore
        loadUserInfo(view)

        fragmentA = RecentFragment()
        fragmentB = AllFragment()

        swapFragments()

        return view
    }


    // HERE IS THE CODE THAT DOESN'T WANNA WORK AND WON'T DO ANYTHING AT ALL
    // LITERALLY HERE TO LOOK PRETTY
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.i("OptionsMenu", "onCreateOptionsMenu called")
        inflater.inflate(R.menu.navigation_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(item.itemId.toString(), "ID")
        return when (item.itemId) {
            R.id.signout -> {
                // Handle the click event for the "Sign Out" item
                Log.i("Test Button","Signing Out")
                viewModel.signOut()
                goToLoginScreen()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserInfo(view: View) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        // Query the user data using the user's UID
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Retrieve user data
                    val userName = document.getString("name")
                    val userEmail = document.getString("email")
                    val userProfileImageUrl = document.getString("pfp")

                    // Update UI with user information
                    updateNavDrawerHeader(view, userName, userEmail, userProfileImageUrl)
                }
            }
            .addOnFailureListener {
                // Handle errors
            }
    }

    private fun updateNavDrawerHeader(view: View, userName: String?, userEmail: String?, userProfileImageUrl: String?) {
        // Reference to Navigation Drawer header views
        val navHeaderProfileImage = view.findViewById<ImageView>(R.id.navHeaderProfileImage)
        val navHeaderUserName = view.findViewById<TextView>(R.id.navHeaderUserName)
        val navHeaderUserEmail = view.findViewById<TextView>(R.id.navHeaderUserEmail)

        // Update Navigation Drawer header views with user information
        userName?.let { navHeaderUserName.text = it }
        userEmail?.let { navHeaderUserEmail.text = it }

        // Load user profile image using Glide in Navigation Drawer header
        userProfileImageUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.pfp) // Placeholder image while loading
                .into(navHeaderProfileImage)
        }
    }

    private fun swapFragments() {
        val fragmentManager = childFragmentManager // Use childFragmentManager for fragments inside fragments

        // To replace the fragment in recentView
        fragmentManager.beginTransaction()
            .replace(R.id.recentView, fragmentA)
            .addToBackStack(null) // Optional, to add the transaction to the back stack
            .commit()

        // To replace the fragment in allView
        fragmentManager.beginTransaction()
            .replace(R.id.allView, fragmentB)
            .addToBackStack(null) // Optional, to add the transaction to the back stack
            .commit()
    }

    private fun goToLoginScreen(){
        this.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }
}
