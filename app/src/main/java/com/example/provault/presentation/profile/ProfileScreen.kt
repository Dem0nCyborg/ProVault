package com.example.provault.presentation.profile


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.provault.R
import com.example.provault.presentation.sign_in.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    modifier: Modifier
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bg))
    LottieAnimation(composition = composition, modifier.fillMaxHeight(),isPlaying = true, iterations = 10)

    var uidResponse by remember {
        mutableStateOf("")
    }

    val database = Firebase.database
    val uid = Firebase.auth.currentUser?.uid
    val myRef = database.getReference("UID").child(uid.toString())

    val uniqueIDRef = database.getReference("UniqueID")

    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (!dataSnapshot.exists()) {
                // Generate and store a unique number
                val uniqueNumber = uid?.take(5)
                myRef.setValue(uniqueNumber)
            }
            val value = dataSnapshot.getValue(Int::class.java)
            if (value != null) {
                uidResponse = value.toString()
                uniqueIDRef.child(uidResponse).setValue(uid.toString())
            }
        }


        override fun onCancelled(error: DatabaseError) {
            // Failed to read value

        }
    })






    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        uidResponse.let {
            Text(
                text = "Unique ID: ${uid?.take(5)}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }



        Button(onClick = onSignOut) {
            Text(text = "Sign out")
        }
    }
}