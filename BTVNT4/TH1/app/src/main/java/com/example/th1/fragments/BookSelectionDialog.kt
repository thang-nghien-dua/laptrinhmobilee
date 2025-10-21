package com.example.th1.fragments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th1.data.LibraryDataManager
import com.example.th1.model.Book
import com.example.th1.model.Student

/**
 * Dialog để chọn sách muốn mượn
 * @param student: Sinh viên đang mượn sách
 * @param availableBooks: Danh sách sách có sẵn (chưa ai mượn)
 * @param onBooksSelected: Hàm gọi khi nhấn Mượn sách (truyền vào list ID sách đã chọn)
 * @param onDismiss: Hàm gọi khi đóng dialog
 */
@Composable
fun BookSelectionDialog(
    student: Student,
    availableBooks: List<Book>,
    onBooksSelected: (List<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    // State lưu danh sách ID sách đã được chọn (dùng Set để tránh trùng)
    var selectedBooks by remember { mutableStateOf(setOf<Int>()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Chọn sách để mượn")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hiển thị tên sinh viên đang mượn
                Text(
                    text = "Sinh viên: ${student.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Danh sách sách có sẵn:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                // LazyColumn hiển thị danh sách sách có sẵn
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(availableBooks) { book ->
                        // Mỗi sách hiển thị với checkbox
                        BookSelectionItem(
                            book = book,
                            // Kiểm tra sách này đã được chọn chưa
                            isSelected = selectedBooks.contains(book.id),
                            onSelectionChanged = { bookId ->
                                // Khi click vào checkbox
                                selectedBooks = if (selectedBooks.contains(bookId)) {
                                    // Nếu đã chọn rồi thì bỏ chọn
                                    selectedBooks - bookId
                                } else {
                                    // Nếu chưa chọn thì thêm vào
                                    selectedBooks + bookId
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Nút Mượn sách
            TextButton(
                onClick = {
                    // Chỉ cho phép mượn nếu đã chọn ít nhất 1 cuốn
                    if (selectedBooks.isNotEmpty()) {
                        // Gọi hàm mượn sách, truyền vào list ID
                        onBooksSelected(selectedBooks.toList())
                    }
                },
                // Chỉ enable nút khi đã chọn sách
                enabled = selectedBooks.isNotEmpty()
            ) {
                Text("Mượn sách")
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

/**
 * Item hiển thị một cuốn sách với checkbox
 * @param book: Cuốn sách
 * @param isSelected: Sách này đã được chọn chưa
 * @param onSelectionChanged: Hàm gọi khi click checkbox (truyền vào ID sách)
 */
@Composable
fun BookSelectionItem(
    book: Book,
    isSelected: Boolean,
    onSelectionChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thông tin sách
        Column(modifier = Modifier.weight(1f)) {
            // Tên sách
            Text(
                text = book.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            // Tác giả
            Text(
                text = "Tác giả: ${book.author}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Checkbox để chọn/bỏ chọn sách
        Checkbox(
            checked = isSelected,
            onCheckedChange = { 
                // Khi click checkbox, gọi hàm với ID sách
                onSelectionChanged(book.id) 
            }
        )
    }
}