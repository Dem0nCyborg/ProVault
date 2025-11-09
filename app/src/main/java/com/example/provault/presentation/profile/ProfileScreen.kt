package com.example.provault.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.provault.presentation.sign_in.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavController,
    userData: UserData?,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var uidResponse by remember { mutableStateOf("") }
    val database = Firebase.database
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
        val myRef = database.getReference("UID").child(uid)
        val uniqueIDRef = database.getReference("UniqueID")

        DisposableEffect(uid) {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        val uniqueNumber = uid.take(5)
                        myRef.setValue(uniqueNumber)
                    }
                    val value = snapshot.getValue(Int::class.java)
                    if (value != null) {
                        uidResponse = value.toString()
                        uniqueIDRef.child(uidResponse).setValue(uid)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }

            myRef.addValueEventListener(listener)
            onDispose { myRef.removeEventListener(listener) }
        }
    }

    // Background gradient (same as SignInScreen)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {
        // Top bar with Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        // Smooth navigation back to projects
                        navController.navigate("projects") {
                            popUpTo("projects") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
            )
        }

        // Main profile content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated fade-in for smoother entry
            AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.fadeIn(animationSpec = tween(600)),
                exit = androidx.compose.animation.fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = userData?.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = userData?.username ?: "Anonymous User",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF203A43),
                            textAlign = TextAlign.Center
                        )

                        if (!userData?.email.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = userData?.email ?: "",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "User ID: ${uid?.take(5) ?: "—"}",
                            fontSize = 14.sp,
                            color = Color(0xFF607D8B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign Out Button
            Button(
                onClick = {
                    onSignOut()
                    // Navigate smoothly to sign_in
                    navController.navigate("sign_in") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Sign out",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedSignOutButton(
    onAnimationComplete: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    // Animate the expansion and color
    val animatedHeight by animateDpAsState(
        targetValue = if (clicked) 800.dp else 52.dp,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
    )
    val animatedWidth by animateDpAsState(
        targetValue = if (clicked) 800.dp else 320.dp,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
    )

    val animatedColor by animateColorAsState(
        targetValue = if (clicked) Color(0xFF0F2027) else Color(0xFFEF5350),
        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
    )

    // When animation ends, navigate away
    LaunchedEffect(clicked) {
        if (clicked) {
            delay(650L)
            onAnimationComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(animatedWidth)
                .height(animatedHeight)
                .clip(RoundedCornerShape(if (clicked) 0.dp else 12.dp))
                .background(animatedColor)
                .clickable {
                    if (!clicked) clicked = true
                },
            contentAlignment = Alignment.Center
        ) {
            if (!clicked) {
                Text(
                    text = "Sign out",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}