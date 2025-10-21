package com.example.th1.model

data class BorrowRecord(
    val id: Int,
    val studentId: Int,
    val bookId: Int,
    val borrowDate: String,
    val returnDate: String? = null,
    val isReturned: Boolean = false
)
