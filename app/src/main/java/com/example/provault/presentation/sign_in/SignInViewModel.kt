package com.example.provault.presentation.sign_in

import androidx.lifecycle.ViewModel
import com.example.provault.UserSession
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel(){

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessfull = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }

     fun addUser() {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            UserSession.userId = firebaseUser.uid
            val db = Firebase.firestore

            val username = firebaseUser.displayName ?: "Unknown"
            val vpin = firebaseUser.uid.take(5)

            val userData = hashMapOf(
                "Username" to username,
                "VPIN" to vpin
            )

            try {
                db.collection("Users").document(vpin).set(userData)
                println("User added to Firestore")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}