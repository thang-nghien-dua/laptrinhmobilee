package com.example.th1.model

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val isbn: String,
    val available: Boolean = true
)
