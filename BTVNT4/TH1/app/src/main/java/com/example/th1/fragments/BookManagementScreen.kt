package com.example.th1.fragments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th1.data.LibraryDataManager
import com.example.th1.model.Book

/**
 * Màn hình quản lý sách
 * Chức năng: Xem danh sách sách và thêm sách mới
 */
@Composable
fun BookManagementScreen() {
    // Lấy singleton instance để quản lý dữ liệu sách
    val libraryDataManager = LibraryDataManager
    
    // State để điều khiển hiển thị dialog thêm sách
    var showAddDialog by remember { mutableStateOf(false) }
    

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header với tiêu đề và nút thêm
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tiêu đề màn hình
                Text(
                    text = "Quản lý sách",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Nút thêm sách mới (nút + tròn màu xanh)
                FloatingActionButton(
                    onClick = { 
                        // Click nút thì hiển thị dialog
                        showAddDialog = true 
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm sách")
                }
            }
        }
        
        // Danh sách tất cả các sách
        // items() tự động tạo UI cho từng sách trong list
        items(libraryDataManager.getAllBooks()) { book ->
            // Mỗi cuốn sách hiển thị trong một Card
            BookCard(book = book)
        }
    }
    
    // Hiển thị dialog thêm sách nếu showAddDialog = true
    if (showAddDialog) {
        AddBookDialog(
            onDismiss = { 
                // Đóng dialog khi nhấn Hủy
                showAddDialog = false 
            },
            onAddBook = { title, author, isbn ->
                // Thêm sách mới vào database
                libraryDataManager.addBook(title, author, isbn)
                // Đóng dialog sau khi thêm
                showAddDialog = false
            }
        )
    }
}

/**
 * Card hiển thị thông tin một cuốn sách
 * @param book: Đối tượng sách cần hiển thị
 */
@Composable
fun BookCard(book: Book) {
    // Card tạo viền bo tròn và shadow cho đẹp
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tên sách (chữ lớn, đậm)
            Text(
                text = book.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Tác giả
            Text(
                text = "Tác giả: ${book.author}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Mã ISBN
            Text(
                text = "ISBN: ${book.isbn}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Trạng thái sách (Có sẵn hoặc Đã mượn)
            Text(
                text = if (book.available) "✓ Có sẵn" else "✗ Đã mượn",
                fontSize = 14.sp,
                // Màu xanh nếu có sẵn, màu đỏ nếu đã mượn
                color = if (book.available) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Dialog để thêm sách mới
 * @param onDismiss: Hàm gọi khi đóng dialog
 * @param onAddBook: Hàm gọi khi thêm sách (truyền title, author, isbn)
 */
@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onAddBook: (String, String, String) -> Unit
) {
    // State lưu dữ liệu nhập vào từ form
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    
    // AlertDialog là popup form
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Thêm sách mới") 
        },
        text = {
            // Column chứa các ô nhập liệu
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ô nhập Tên sách
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên sách") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ví dụ: Lập trình Android") }
                )
                
                // Ô nhập Tác giả
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Tác giả") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ví dụ: Nguyễn Văn A") }
                )
                
                // Ô nhập Mã ISBN
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ví dụ: ISBN-12345") }
                )
            }
        },
        confirmButton = {
            // Nút Thêm
            TextButton(
                onClick = {
                    // Kiểm tra tất cả field đã điền chưa
                    if (title.isNotBlank() && author.isNotBlank() && isbn.isNotBlank()) {
                        // Gọi hàm thêm sách
                        onAddBook(title, author, isbn)
                    }
                }
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            // Nút Hủy
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}