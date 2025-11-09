package com.example.provault.TodoList

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.provault.PIDGlobal
import java.time.Instant
import java.util.Date

data class TODO (

    val done: Boolean = false,
    val id: Int,
    val pid : String = PIDGlobal.selectedProjectId.toString(),
    val title: String = "",

    )
