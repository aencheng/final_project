package com.example.afinal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SignUpFragment : Fragment() {
    private val TAG = "LoginFragment"
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    // Declare as class-level variables
    private lateinit var auth: FirebaseAuth
    private lateinit var btnSignUp: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        auth = FirebaseAuth.getInstance()
        btnSignUp = view.findViewById(R.id.nextBtn)

        val loginBtn = view.findViewById<TextView>(R.id.textViewSignIn)

        // on click listeners to sign up and navigate login screen
        loginBtn.setOnClickListener {
            goToLoginScreen()
        }

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        profileImageView.setOnClickListener {
            openImageChooser()
        }

        btnSignUp.setOnClickListener {
            btnSignUp.isEnabled = false
            val nameView = view.findViewById<TextView>(R.id.fullName)
            val pfpView = view.findViewById<ImageView>(R.id.profileImageView)
            val etEmail = view.findViewById<TextView>(R.id.emailEt)
            val etPassword = view.findViewById<TextView>(R.id.passEt)
            val verifyEtPassword = view.findViewById<TextView>(R.id.verifyPassEt)
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val verifyPassword = verifyEtPassword.text.toString()
            val name = nameView.text.toString()

            // Check if an image is selected
            if (imageUri != null) {
                // Upload the image to Firebase Cloud Storage
                uploadImageToStorage(email, password, verifyPassword, name)
            } else {
                Toast.makeText(this.context, "Please select a profile image", Toast.LENGTH_SHORT).show()
                btnSignUp.isEnabled = true
            }
        }

        return view
    }

    private fun uploadImageToStorage(email: String, password: String, verifyPassword: String, name: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${auth.currentUser?.uid}.jpg")

        imageUri?.let {
            imageRef.putFile(it)
                .addOnSuccessListener {
                    // Image uploaded successfully, get download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // Continue with user registration logic
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this.context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                // Call the function to initialize user data in Firestore
                                initializeUserInFirestore(task.result?.user?.uid, email, name, imageUrl)
                                goToLoginScreen()
                            } else {
                                Log.e(TAG, "createUser failed", task.exception)
                                Toast.makeText(this.context, "Create User Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Image upload failed", e)
                    Toast.makeText(this.context, "Image upload failed", Toast.LENGTH_SHORT).show()
                    btnSignUp.isEnabled = true
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)
            profileImageView?.setImageURI(imageUri)
        }
    }

    private fun initializeUserInFirestore(userId: String?, email: String, name: String, pfp: String) {
        // Assuming you have a "users" collection in Firestore
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        // Create a user object or a map with user information
        val user = hashMapOf(
            "userId" to userId,
            "email" to email,
            "name" to name,
            "pfp" to pfp
            // Add more user information as needed
        )

        // Add the user data to Firestore
        userId?.let {
            usersCollection.document(it)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "User data added to Firestore successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding user data to Firestore", e)
                }
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    private fun goToLoginScreen() {
        this.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }
}