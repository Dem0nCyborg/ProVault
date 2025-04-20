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


        db.collection("Projects")
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

        val projectID = project.pid
        val projectName = project.pname

        val project = hashMapOf(
            "pid" to projectID,
            "pname" to projectName
        )
        db.collection("Projects").document(projectID.toString()).set(project)

    }


    }

