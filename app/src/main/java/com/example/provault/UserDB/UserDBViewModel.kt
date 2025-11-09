package com.example.provault.UserDB

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDBViewModel : ViewModel() {

    private var _projectsList = MutableStateFlow<List<Projects>>(emptyList())
    private val _userProjects = mutableListOf<Projects>()
    val projectsList = _projectsList.asStateFlow()




    private var db = Firebase.firestore
    val userID = Firebase.auth.currentUser?.uid
    val vpin = userID?.take(5).toString()

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


                    db.collection("Users").document(vpin).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val data = document.data

                                data?.forEach { (key, value) ->
                                    // Filter out reserved keys
                                    if (key != "Username" && key != "VPIN") {
                                        _userProjects.add(Projects(pid = key.toInt(), pname = value.toString()))
                                    }
                                }

                                _projectsList.value = _userProjects
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching user projects", e)
                        }
                }

                }
            }

    fun addProject(project: Projects){

        val projectID = project.pid
        val projectName = project.pname

        val vpin = userID?.take(5).toString()

        val project = hashMapOf(
            "pid" to projectID,
            "pname" to projectName
        )

        val owner = hashMapOf(
            projectID.toString() to projectName
        )

        db.collection("Projects").document(projectID.toString()).set(project)

        db.collection("Users").document(vpin).set(owner, SetOptions.merge())



    }


    }


data class SimpleProject(
    val pid: String,
    val pname: String
)