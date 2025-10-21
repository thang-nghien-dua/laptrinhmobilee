package com.example.th1.data

import androidx.compose.runtime.mutableStateListOf
import com.example.th1.model.Book
import com.example.th1.model.Student
import com.example.th1.model.BorrowRecord

object LibraryDataManager {
    private val students = mutableStateListOf<Student>()
    private val books = mutableStateListOf<Book>()
    private val borrowRecords = mutableStateListOf<BorrowRecord>()
    
    init {
        // Initialize with sample data
        addSampleData()
    }
    
    private fun addSampleData() {
        // Add sample students
        addStudent("Nguyen Van A", "SV001", "nguyenvana@email.com")
        addStudent("Nguyen Thi B", "SV002", "nguyenthib@email.com")
        addStudent("Nguyen Van C", "SV003", "nguyenvanc@email.com")
        
        // Add sample books
        addBook("Sách 01", "Tác giả A", "ISBN001")
        addBook("Sách 02", "Tác giả B", "ISBN002")
        addBook("Lập trình Android", "Google", "ISBN003")
        addBook("Kotlin Programming", "JetBrains", "ISBN004")
        
        // Add some sample borrow records to demonstrate functionality
        // Nguyen Van A borrows Sách 01 and Sách 02
        val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        borrowRecords.add(BorrowRecord(1, 1, 1, currentDate))
        borrowRecords.add(BorrowRecord(2, 1, 2, currentDate))
        updateBookAvailability(1, false)
        updateBookAvailability(2, false)
        
        // Nguyen Thi B borrows Sách 01 (but it's already borrowed, so let's borrow Sách 03)
        borrowRecords.add(BorrowRecord(3, 2, 3, currentDate))
        updateBookAvailability(3, false)
    }
    
    // Student management
    fun getAllStudents(): List<Student> = students.toList()
    
    fun addStudent(name: String, studentId: String, email: String) {
        val newId = students.maxOfOrNull { it.id }?.plus(1) ?: 1
        students.add(Student(newId, name, studentId, email))
    }
    
    fun getStudentById(id: Int): Student? = students.find { it.id == id }
    
    // Book management
    fun getAllBooks(): List<Book> = books.toList()
    
    fun addBook(title: String, author: String, isbn: String) {
        val newId = books.maxOfOrNull { it.id }?.plus(1) ?: 1
        books.add(Book(newId, title, author, isbn, true))
    }
    
    fun getBookById(id: Int): Book? = books.find { it.id == id }
    
    fun updateBookAvailability(bookId: Int, available: Boolean) {
        val bookIndex = books.indexOfFirst { it.id == bookId }
        if (bookIndex != -1) {
            books[bookIndex] = books[bookIndex].copy(available = available)
        }
    }
    
    // Borrow management
    fun getAllBorrowRecords(): List<BorrowRecord> = borrowRecords.toList()
    
    fun borrowBooks(studentId: Int, bookIds: List<Int>): Boolean {
        val student = getStudentById(studentId) ?: return false
        
        // Check if all books are available
        val unavailableBooks = bookIds.filter { bookId ->
            val book = getBookById(bookId)
            book == null || !book.available
        }
        
        if (unavailableBooks.isNotEmpty()) {
            return false
        }
        
        // Create borrow records
        val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        bookIds.forEach { bookId ->
            val newId = borrowRecords.maxOfOrNull { it.id }?.plus(1) ?: 1
            borrowRecords.add(
                BorrowRecord(
                    id = newId,
                    studentId = studentId,
                    bookId = bookId,
                    borrowDate = currentDate
                )
            )
            updateBookAvailability(bookId, false)
        }
        
        return true
    }
    
    fun getBorrowedBooksByStudent(studentId: Int): List<Book> {
        val borrowedBookIds = borrowRecords
            .filter { it.studentId == studentId && !it.isReturned }
            .map { it.bookId }
        
        return borrowedBookIds.mapNotNull { bookId ->
            getBookById(bookId)
        }
    }
    
    fun returnBook(borrowRecordId: Int): Boolean {
        val recordIndex = borrowRecords.indexOfFirst { it.id == borrowRecordId }
        if (recordIndex != -1) {
            val record = borrowRecords[recordIndex]
            borrowRecords[recordIndex] = record.copy(
                isReturned = true,
                returnDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            )
            updateBookAvailability(record.bookId, true)
            return true
        }
        return false
    }
    
    fun returnBookByStudentAndBook(studentId: Int, bookId: Int): Boolean {
        val record = borrowRecords.find { 
            it.studentId == studentId && it.bookId == bookId && !it.isReturned 
        }
        if (record != null) {
            return returnBook(record.id)
        }
        return false
    }
}
