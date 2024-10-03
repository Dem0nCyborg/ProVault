package com.example.provault.presentaion.sign_in

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
@Composable
fun LoginScreen(){

    var userEmail by remember {
        mutableStateOf("")
    }

    var userPassword by remember {
        mutableStateOf("")
    }

     Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ){
        OutlinedTextField(value = userEmail, onValueChange = { userEmail = it },
            label = { Text(text = "Email")})

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(value = userPassword, onValueChange = { userPassword = it },
            label = { Text(text = "Password")}
            )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            onClick = { /*TODO*/ }) {

         //How can i find my projects sha-1 key



        }

    }
}
*/


@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ProVault", fontSize = 50.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.fillMaxWidth().padding(16.dp)
            , textAlign = TextAlign.Center,

            )
        Button(onClick = onSignInClick) {
            Text(text = "Sign in")
        }
    }
}