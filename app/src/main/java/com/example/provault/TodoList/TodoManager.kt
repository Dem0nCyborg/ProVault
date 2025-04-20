package com.example.provault.TodoList

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import java.time.Instant
import java.util.Date

object TodoManager {
    private var todoList = mutableListOf<TODO>()

    var db = com.google.firebase.ktx.Firebase.database
    var myRef = db.getReference("TODO")

    fun getTODO() : List<TODO>{
        return todoList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTODO(title : String){
        todoList.add(TODO(System.currentTimeMillis().toInt(), title, Date.from(Instant.now())))
        myRef.child("items").setValue(TODO(System.currentTimeMillis().toInt(), title, Date.from(Instant.now())))


    }

    fun deleteTODO(id : Int){
        todoList.removeIf { it.id == id }

    }

}