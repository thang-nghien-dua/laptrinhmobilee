package com.example.th1.fragments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th1.data.LibraryDataManager
import com.example.th1.model.Book
import com.example.th1.model.Student

/**
 * Màn hình quản lý mượn sách (Màn hình chính)
 * Chức năng: 
 * - Chọn sinh viên
 * - Xem sách sinh viên đã mượn
 * - Mượn sách mới
 * - Trả sách
 */
@Composable
fun BorrowManagementScreen() {
    // Lấy singleton instance để quản lý dữ liệu
    val libraryDataManager = LibraryDataManager
    
    // State lưu sinh viên đang được chọn
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    
    // State điều khiển hiển thị dialog chọn sinh viên
    var showStudentDialog by remember { mutableStateOf(false) }
    
    // State điều khiển hiển thị dialog chọn sách để mượn
    var showBookSelectionDialog by remember { mutableStateOf(false) }
    
    // Trigger để refresh lại UI khi có thay đổi (mượn/trả sách)
    var refreshTrigger by remember { mutableIntStateOf(0) }
    
    // Sử dụng LazyColumn để có thể cuộn nội dung
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phần 1: Chọn sinh viên
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tiêu đề
                    Text(
                        text = "Sinh viên",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Hàng chứa tên sinh viên và nút Thay đổi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hiển thị tên sinh viên đã chọn, hoặc "Chọn sinh viên" nếu chưa chọn
                        Text(
                            text = selectedStudent?.name ?: "Chọn sinh viên",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Nút Thay đổi (màu xanh)
                        Button(
                            onClick = { 
                                // Click thì hiển thị dialog chọn sinh viên
                                showStudentDialog = true 
                            }
                        ) {
                            Text("Thay đổi")
                        }
                    }
                }
            }
        }
        
        // Phần 2: Danh sách sách đã mượn
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tiêu đề
                    Text(
                        text = "Danh sách sách",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Kiểm tra đã chọn sinh viên chưa
                    if (selectedStudent != null) {
                        // Lấy danh sách sách sinh viên đã mượn
                        val borrowedBooks = libraryDataManager.getBorrowedBooksByStudent(selectedStudent!!.id)
                        
                        // Kiểm tra sinh viên có mượn sách nào không
                        if (borrowedBooks.isEmpty()) {
                            // Chưa mượn sách nào -> Hiển thị thông báo
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "Bạn chưa mượn quyển sách nào\nNhấn 'Thêm' để bắt đầu hành trình đọc sách!",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            // Đã mượn sách -> Hiển thị danh sách
                            // Sử dụng Column thường cho danh sách ngắn (không cần LazyColumn lồng)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                // Duyệt qua từng sách đã mượn
                                borrowedBooks.forEach { book ->
                                    BorrowedBookItem(
                                        book = book,
                                        onReturnBook = {
                                            // Khi nhấn nút Trả sách
                                            // Gọi hàm trả sách
                                            libraryDataManager.returnBookByStudentAndBook(
                                                selectedStudent!!.id, 
                                                book.id
                                            )
                                            // Tăng refreshTrigger để UI cập nhật
                                            refreshTrigger++
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Chưa chọn sinh viên -> Hiển thị thông báo
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Vui lòng chọn sinh viên để xem sách đã mượn",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Phần 3: Nút Thêm (để mượn sách mới)
        item {
            Button(
                onClick = {
                    // Khi nhấn nút Thêm
                    if (selectedStudent != null) {
                        // Nếu đã chọn sinh viên thì hiển thị dialog chọn sách
                        showBookSelectionDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // Chỉ enable nút khi đã chọn sinh viên
                enabled = selectedStudent != null
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm")
            }
        }
    }
    
    // Dialog chọn sinh viên
    if (showStudentDialog) {
        StudentSelectionDialog(
            students = libraryDataManager.getAllStudents(),
            onStudentSelected = { student ->
                // Khi chọn sinh viên
                selectedStudent = student
                // Đóng dialog
                showStudentDialog = false
            },
            onDismiss = { 
                // Khi nhấn Hủy
                showStudentDialog = false 
            }
        )
    }
    
    // Dialog chọn sách để mượn
    if (showBookSelectionDialog && selectedStudent != null) {
        // Lấy danh sách sách có sẵn (chưa được mượn)
        val availableBooks = libraryDataManager.getAllBooks().filter { it.available }
        
        BookSelectionDialog(
            student = selectedStudent!!,
            availableBooks = availableBooks,
            onBooksSelected = { bookIds ->
                // Khi chọn xong sách và nhấn Mượn
                // Gọi hàm mượn sách
                val success = libraryDataManager.borrowBooks(selectedStudent!!.id, bookIds)
                if (success) {
                    // Nếu mượn thành công thì refresh UI
                    refreshTrigger++
                }
                // Đóng dialog
                showBookSelectionDialog = false
            },
            onDismiss = { 
                // Khi nhấn Hủy
                showBookSelectionDialog = false 
            }
        )
    }
}

/**
 * Item hiển thị một cuốn sách đã mượn
 * @param book: Cuốn sách
 * @param onReturnBook: Hàm gọi khi nhấn nút Trả sách
 */
@Composable
fun BorrowedBookItem(book: Book, onReturnBook: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thông tin sách
            Column(modifier = Modifier.weight(1f)) {
                // Tên sách
                Text(
                    text = book.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                // Tác giả
                Text(
                    text = book.author,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Nút Trả sách (màu đỏ)
            Button(
                onClick = onReturnBook,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Trả sách")
            }
        }
    }
}

/**
 * Dialog chọn sinh viên
 * @param students: Danh sách tất cả sinh viên
 * @param onStudentSelected: Hàm gọi khi chọn một sinh viên
 * @param onDismiss: Hàm gọi khi đóng dialog
 */
@Composable
fun StudentSelectionDialog(
    students: List<Student>,
    onStudentSelected: (Student) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn sinh viên") },
        text = {
            // LazyColumn để hiển thị danh sách sinh viên
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    // Mỗi sinh viên là một Card có thể click
                    Card(
                        onClick = { 
                            // Khi click vào sinh viên
                            onStudentSelected(student) 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Tên sinh viên
                            Text(
                                text = student.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // MSSV
                            Text(
                                text = "MSSV: ${student.studentId}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}