package com.example.provault.TodoList

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.provault.ConnectRoute
import com.example.provault.R
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

data class BottomNavRail(
    val title : String,
    val icon : ImageVector,
    val route: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TodoList(
    navController : NavHostController,
    viewModel: TodoViewModel
) {



    val todolist by viewModel.todolist.observeAsState()

    val selectedIndex = remember { mutableStateOf(3) }
    val items = listOf(
        BottomNavRail("projects", Icons.Default.Lock, "fileUploader"),
        BottomNavRail("VideoCall", Icons.Default.Face, ""),
        BottomNavRail("AI Bot", Icons.Default.Search,"AI"),
        BottomNavRail("AI Bot", Icons.Filled.List,"TODO")
    )

    var inputText by remember { mutableStateOf("") }


    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text("TODO List") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("projects") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigation Icon",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },

        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex.value == index,
                        onClick = {if (index != 1){
                            selectedIndex.value = index
                            navController.navigate(item.route)}
                        else{ navController.navigate(ConnectRoute)
                        }
                        },
                        icon = { Icon(item.icon, contentDescription = item.route) }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxHeight()
        ) {

            Row(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = inputText,
                    onValueChange ={
                        inputText = it
                    }
                )
                Button(onClick = {
                    viewModel.addTODO(inputText)
                    inputText = ""
                }) {
                    Text(text = "Add")
                }
            }
            todolist?.let {
                LazyColumn(
                    modifier = Modifier.padding(bottom = 50.dp),
                    content = {
                        itemsIndexed(it){ index:Int , Item: TODO ->
                            TodoItem(item = Item,
                                onDelete = {viewModel.deleteTODO(Item.id)})
                        }
                    }
                )
            }?: Text(
                modifier = Modifier.fillMaxWidth(),
                text = "There are no TODOs",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )


        }
    }
}

@Composable
fun TodoItem(item : TODO,  onDelete : () -> Unit){
    Row(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceTint)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = SimpleDateFormat("HH:mm:aa, dd/mm/yyyy", Locale.ENGLISH).format(item.createdAt),
                fontSize = 10.sp,
                color = Color.LightGray
            )


            Text(text = item.title,
                fontSize = 20.sp,
                color = Color.Black
                )
        }
        IconButton(onClick = onDelete ) {
            Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = "Delete",
                tint = Color.White
            )
        }
    }

}