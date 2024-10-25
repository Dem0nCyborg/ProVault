
package com.example.provault.UserDB

import android.content.Context
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.provault.presentation.sign_in.UserData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(viewModel : UserDBViewModel = viewModel(),
                      context: Context,
                      navController: NavController,
                      userData: UserData?
){

    val projects by viewModel.projectsList.collectAsStateWithLifecycle()
    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var projectId : String by remember {
        mutableStateOf("")
    }

    var projectName : String by remember {
        mutableStateOf("")
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Projects", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("profile")
                    },
                        content = {
                            AsyncImage(
                                model = userData!!.profilePictureUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.LightGray)
            )
        }
    ) {paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Bottom,

            ) {
            items(projects){projects->
                ProjectItem(project = projects){
                    val id = projects.pid
                    Toast.makeText(context, "Project ID: ${id}", Toast.LENGTH_SHORT).show()
                    navController.navigate("fileUploader/$id")
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
                Toast.makeText(context, "Add Project", Toast.LENGTH_SHORT).show()
            }){
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
            }
            if (showAlertDialog){
                AlertDialog(
                    onDismissRequest = {
                        showAlertDialog = false
                    },
                    title = {
                        Text(text = "Add Project")
                    },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = projectName,
                                onValueChange = {
                                    projectName = it
                                },
                                label = { Text(text = "Project Name") }
                            )
                            OutlinedTextField(
                                value = projectId.toString(),
                                onValueChange = {
                                    projectId = it
                                },
                                label = { Text(text = "Project ID") },
                                keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Number)
                            )
                        }


                    },
                    confirmButton = {
                        Button(onClick = {
                            if (projectId != "" && projectName != ""){
                                viewModel.addProject(Projects(projectId.toInt(),projectName))
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

}


@Composable
fun ProjectItem(project: Projects,onClick : () -> Unit){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(text = project.pname, fontWeight = FontWeight.ExtraBold)
            Text(text = project.pid.toString())

        }
    }

}
