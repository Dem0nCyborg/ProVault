package com.example.provault.TodoList

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.example.provault.PIDGlobal
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.firestore
import java.time.Instant
import java.util.Date

@SuppressLint("StaticFieldLeak")
object TodoManager {
    private var todoList = mutableListOf<TODO>()

    fun generateRandomTodoId(): Int {
        return (100000..999999).random()
    }

    val ref = Firebase.firestore
    private val TAG = "TodoManager"

    private val _todolist = mutableStateOf<List<TODO>?>(null)
    val todolist: State<List<TODO>?> = _todolist

    fun getTODO(onComplete: (List<TODO>) -> Unit) {
        ref.collection("Tasklist")
            .get()
            .addOnSuccessListener { result ->
                val todoList = result.map { doc ->
                    TODO(
                        id = doc.getLong("id")?.toInt() ?: 0,
                        title = doc.getString("title") ?: ""
                    )
                }
                onComplete(todoList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching TODOs", exception)
                onComplete(emptyList()) // return empty list if failed
            }
    }





    @RequiresApi(Build.VERSION_CODES.O)
    fun addTODO(title: String) {
        val todoID = generateRandomTodoId()
        val todo = TODO(id = todoID, title = title)
        todoList.add(todo)
        ref.collection("Tasklist")
            .document(todoID.toString())
            .set(todo)
            .addOnSuccessListener {
                println("Todo uploaded successfully")
            }
            .addOnFailureListener { e ->
                println("Failed to upload todo: ${e.message}")
            }
    }

    fun deleteTODO(id : Int){
        todoList.removeIf { it.id == id }

    }

}