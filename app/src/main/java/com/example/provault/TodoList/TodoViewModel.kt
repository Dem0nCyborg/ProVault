package com.example.provault.TodoList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TodoViewModel: ViewModel() {

    private var _todolist = MutableLiveData<List<TODO>>()
    val todolist : LiveData<List<TODO>> = _todolist
    val db = Firebase.database
    val ref = db.getReference("TODO")

    fun getTODO() {
        _todolist.value = TodoManager.getTODO()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTODO(title : String) {
        TodoManager.addTODO(title)
        _todolist.value = TodoManager.getTODO()

    }

    fun deleteTODO(id : Int) {
        TodoManager.deleteTODO(id)
        _todolist.value = TodoManager.getTODO()

    }

}