package com.example.provault.TodoList

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.Date

data class TODO (
    val id: Int,
    val title: String,
    val createdAt : Date


)

@RequiresApi(Build.VERSION_CODES.O)
fun getFakeTodo() : List<TODO> {
    return listOf<TODO>(
        TODO(1,"Task 1 has been created", Date.from(Instant.now())),
        TODO(2,"Task 2 has been created", Date.from(Instant.now())),
        TODO(3,"Task 3 has been created", Date.from(Instant.now())),
        TODO(4,"Task 4 has been created", Date.from(Instant.now())),
        TODO(5,"Task 5 has been created", Date.from(Instant.now())),
        TODO(6,"Task 6 has been created", Date.from(Instant.now())),
        TODO(7,"Task 7 has been created", Date.from(Instant.now())),
        TODO(8,"Task 8 has been created", Date.from(Instant.now())),
    )
}