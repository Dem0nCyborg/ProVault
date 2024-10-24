package com.example.provault

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.provault.AI.PromptUI
import com.example.provault.Biometric.BiometricPromptManager
import com.example.provault.Biometric.BiometricUI
import com.example.provault.Connect.ConnectScreen
import com.example.provault.Connect.ConnectViewModel
import com.example.provault.UserDB.ProjectListScreen
import com.example.provault.Video.CallState
import com.example.provault.Video.VideoCallScreen
import com.example.provault.Video.VideoCallViewModel
import com.example.provault.files.UploadAndRetrieve

import com.example.provault.presentation.profile.ProfileScreen
import com.example.provault.presentation.sign_in.GoogleAuthUiClient
import com.example.provault.presentation.sign_in.SignInScreen
import com.example.provault.presentation.sign_in.SignInViewModel
import com.example.provault.ui.theme.ProVaultTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getstream.video.android.compose.theme.VideoTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiI4YjczZmNmZS01YmYyLTRhODUtOGU4NC1mOTA1YTJhMzE4NDQiLCJlbWFpbCI6ImNoYW5kYW5iaG9waTE2MDdAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiRlJBMSJ9LHsiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiTllDMSJ9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZSwic3RhdHVzIjoiQUNUSVZFIn0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6IjU1OTI2NmJiNWJiODFjZjVlYzFiIiwic2NvcGVkS2V5U2VjcmV0IjoiZWJlYmU4MGNmMjIxM2ZkNDI1ZDYxYmVjZDgwMTgyM2ZjZjUyMWI3NjQ3MWJjMjkxZDUzOTMwMGE1ZWRlNmE4NSIsImV4cCI6MTc2MTI5NjcxOH0.4kKwOxqhioZ5K2Gm67gq5t2SNAmSuW8kIH7_NvBXWjY" // Replace with your actual JWT token
    private val pinataApiUrl = "https://api.pinata.cloud/pinning/pinFileToIPFS"
    private val pinataRetrieveUrl = "https://api.pinata.cloud/data/pinList"

    val db = Firebase.firestore
    var id = UserSession.userId

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }



    private val googleAuthClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            ProVaultTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "projects") {
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthClient.getSignedInUser() != null) {
                                    navController.navigate("projects")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessfull) {
                                if(state.isSignInSuccessfull) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("projects")
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(userData = googleAuthClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthClient.signOut()
                                        navController.navigate("sign_in")
                                    }
                                })
                        }
                        composable("projects") {
                            ProjectListScreen(
                                context = applicationContext,
                                navController = navController,
                                userData = googleAuthClient.getSignedInUser()
                            )
                        }

                        composable("Biometric"){
                            BiometricUI(
                                navController = navController,
                                promptManager = promptManager
                            )
                        }

                        composable("AI"){
                            PromptUI()
                        }


                        composable("fileUploader") {
                            UploadAndRetrieve()
                        }






                        composable<ConnectRoute> {
                            val viewModel = koinViewModel<ConnectViewModel>()
                            val state = viewModel.state

                            LaunchedEffect(key1 = state.isConnected) {
                                if (state.isConnected) {
                                    navController.navigate(VideoCallRoute) {
                                        popUpTo(ConnectRoute) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }

                            ConnectScreen(state = state, onAction = viewModel::onAction)
                        }
                        composable<VideoCallRoute> {
                            val viewModel = koinViewModel<VideoCallViewModel>()
                            val state = viewModel.state

                            LaunchedEffect(key1 = state.callState) {
                                if (state.callState == CallState.ENDED) {
                                    navController.navigate(ConnectRoute) {
                                        popUpTo(VideoCallRoute) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }

                            VideoTheme {
                                VideoCallScreen(state = state, onAction = viewModel::onAction)
                            }


                        }

                    }

                }
            }
        }
    }

}
@Serializable
data object ConnectRoute

@Serializable
data object VideoCallRoute


