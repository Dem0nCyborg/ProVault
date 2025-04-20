package com.example.provault.Files

data class ApiResponse(
    val data: Data
)

data class Data(
    val groups: List<Group>
)

data class Group(
    val id: String,
    val name: String,
    val is_public: Boolean,
    val created_at: String
)