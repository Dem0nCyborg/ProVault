package com.example.provault

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun TestDB() {

    val database = Firebase.database
    val myRef = database.getReference("UID")
    val uid = Firebase.auth.currentUser?.uid
    var value1 by remember { mutableStateOf("") }
    val uniqueNumber = 23456
    var uidValuePairs by remember { mutableStateOf(listOf<Pair<String, String>>()) }


    uid?.let {
        myRef.child(it).setValue(uniqueNumber.toString())
    }

    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val newList = mutableListOf<Pair<String, String>>()

            // Loop through all children in "UID/" reference
            for (snapshot in dataSnapshot.children) {
                val key = snapshot.key // UID
                val value = snapshot.getValue(String::class.java) // unique number

                if (key != null && value != null) {
                    newList.add(key to value)
                }
            }
            uidValuePairs = newList // Update the state with the new list
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("TAG", "Failed to read value.", error.toException())
        }
    })

    LazyRow {
        items(uidValuePairs) { pair ->
            Column {
                Text(text = "UID: ${pair.first}") // Display the UID (key)
                Text(text = "Value: ${pair.second}") // Display the unique number (value)
            }
        }
    }

}
