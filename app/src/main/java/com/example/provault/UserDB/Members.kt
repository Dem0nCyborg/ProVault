package com.example.provault.UserDB

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.provault.PIDGlobal
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


data class ProjectMember(
    val vpin: String,
    val value: String
)

data class MemberData(
    val username: String,
    val vpin: String
)

@Composable
fun MemberList(pid: String,navController: NavController) {

    val projectName = PIDGlobal.selectedProjectName ?: "Unknown Project"

    val db = Firebase.firestore

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var memberID : String by remember {
        mutableStateOf("")
    }

    val memberList = remember { mutableStateListOf<ProjectMember>() }

    LaunchedEffect(key1 = pid) {
        val db = Firebase.firestore
        val docRef = db.collection("Projects").document(pid)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    data?.forEach { (key, value) ->
                        if (key != "pid" && key != "pname") {
                            memberList.add(ProjectMember(vpin = key, value = value.toString()))
                        }
                        else if(key == "pid" && key == "pname"){
                            memberList.add(ProjectMember(vpin = "No Members", value = ""))
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching members: ", e)
            }
    }



    LazyColumn {
        if (memberList.isNotEmpty()) {
            items(memberList) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "VPIN: ${member.vpin}", fontWeight = FontWeight.Bold)
                        Text(text = "Value: ${member.value}")
                    }
                }
            }
        }
        else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "No Members Found", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End

    ) {
        FloatingActionButton(onClick = {
            showAlertDialog = true
        }){
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
        }
        if (showAlertDialog){
            AlertDialog(
                onDismissRequest = {
                    showAlertDialog = false
                },
                title = {
                    Text(text = "Add Member")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = memberID,
                            onValueChange = {
                                memberID = it
                            },
                            label = { Text(text = "Member ID") }
                        )


                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (memberID.isNotBlank()){
                            db.collection("Users").document(memberID).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val username = document.getString("Username") ?: "Unknown"
                                        val userVpin = document.getString("VPIN") ?: "N/A"
                                        val userData = MemberData(username, userVpin)

                                        db.collection("Projects").document(pid)
                                            .update(userData.vpin, userData.username)
                                            .addOnSuccessListener {
                                                Log.d("Firestore", "Member added successfully")
                                                Toast.makeText(
                                                    navController.context,
                                                    "Member added successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firestore", "Error adding member", e)
                                            }

                                        val project = hashMapOf(
                                            pid.toString() to projectName
                                        )

                                        db.collection("Users").document(memberID)
                                            .set(project, SetOptions.merge())

                                        navController.navigate("members"){
                                            popUpTo("members") {
                                                inclusive = true
                                            }
                                        }

                                        Log.i("UserData", "Username: ${userData.username}, VPIN: ${userData.vpin}")

                                    } else {
                                        Log.d("Firestore", "No such document")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error fetching document", e)
                                }
                        }
                        showAlertDialog = false
                    }) {
                        Text(text = "Add")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showAlertDialog = false
                    }) {
                        Text(text = "Cancel")
                    }
                },
            )
        }

    }

}