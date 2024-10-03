package com.example.provault.presentaion.sign_in

data class SignInState(
    val isSignInSuccessfull: Boolean = false,
    val signInError : String? = null
)
