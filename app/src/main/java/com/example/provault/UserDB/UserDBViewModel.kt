package com.example.provault.UserDB

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDBViewModel : ViewModel() {

    private var _projectsList = MutableStateFlow<List<Projects>>(emptyList())
    val projectsList = _projectsList.asStateFlow()

    private var db = Firebase.firestore

    init {
        getProjectsList()
    }

    fun getProjectsList() {


        db.collection("Users/124453/Projects")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if(value != null){
                   _projectsList.value = value.toObjects()
                    }

                }
            }

    fun addProject(project: Projects){
        db.collection("Users/124453/Projects")
            .add(project)
    }


    }

