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
import com.example.th1.model.Student

/**
 * Màn hình quản lý sinh viên
 * Chức năng: Xem danh sách sinh viên và thêm sinh viên mới
 */
@Composable
fun StudentManagementScreen() {
    // Lấy singleton instance của LibraryDataManager để quản lý dữ liệu
    val libraryDataManager = LibraryDataManager
    
    // Biến state để điều khiển hiển thị dialog thêm sinh viên
    var showAddDialog by remember { mutableStateOf(false) }
    
    // LazyColumn cho phép cuộn danh sách dài mà không lag
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
                    text = "Quản lý sinh viên",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Nút thêm sinh viên mới (hình tròn màu xanh)
                FloatingActionButton(
                    onClick = { 
                        // Khi click nút + thì hiển thị dialog
                        showAddDialog = true 
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm sinh viên")
                }
            }
        }
        
        // Danh sách tất cả sinh viên
        // items() là hàm đặc biệt của LazyColumn để hiển thị list
        items(libraryDataManager.getAllStudents()) { student ->
            // Mỗi sinh viên được hiển thị trong một Card
            StudentCard(student = student)
        }
    }
    
    // Nếu showAddDialog = true thì hiển thị dialog thêm sinh viên
    if (showAddDialog) {
        AddStudentDialog(
            onDismiss = { 
                // Khi nhấn Hủy thì đóng dialog
                showAddDialog = false 
            },
            onAddStudent = { name, studentId, email ->
                // Khi nhấn Thêm thì gọi hàm thêm sinh viên
                libraryDataManager.addStudent(name, studentId, email)
                // Sau đó đóng dialog
                showAddDialog = false
            }
        )
    }
}

/**
 * Card hiển thị thông tin của một sinh viên
 * @param student: Đối tượng sinh viên cần hiển thị
 */
@Composable
fun StudentCard(student: Student) {
    // Card là một container với viền bo tròn và shadow
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tên sinh viên (chữ lớn, đậm)
            Text(
                text = student.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Mã số sinh viên
            Text(
                text = "MSSV: ${student.studentId}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Email sinh viên
            Text(
                text = "Email: ${student.email}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Dialog để thêm sinh viên mới
 * @param onDismiss: Hàm gọi khi đóng dialog
 * @param onAddStudent: Hàm gọi khi thêm sinh viên (truyền vào name, studentId, email)
 */
@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAddStudent: (String, String, String) -> Unit
) {
    // State để lưu dữ liệu nhập vào từ form
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // State để hiển thị lỗi email nếu không hợp lệ
    var emailError by remember { mutableStateOf("") }
    
    /**
     * Hàm kiểm tra email có hợp lệ không
     * Điều kiện: Email phải có đuôi @gmail.com
     */
    fun validateEmail(email: String): Boolean {
        // Kiểm tra email có kết thúc bằng @gmail.com không
        if (!email.endsWith("@gmail.com")) {
            emailError = "Email phải có đuôi @gmail.com"
            return false
        }
        
        // Kiểm tra email có ít nhất 1 ký tự trước @gmail.com không
        if (email.length <= "@gmail.com".length) {
            emailError = "Email không hợp lệ"
            return false
        }
        
        // Email hợp lệ
        emailError = ""
        return true
    }
    
    // AlertDialog là popup hiển thị form
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Thêm sinh viên mới") 
        },
        text = {
            // Column chứa các ô nhập liệu
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ô nhập Họ và tên
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Ô nhập Mã số sinh viên
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Mã số sinh viên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Ô nhập Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        // Khi người dùng nhập, xóa lỗi cũ
                        if (emailError.isNotEmpty()) {
                            emailError = ""
                        }
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    // Nếu có lỗi thì viền đỏ
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        // Hiển thị message lỗi bên dưới ô input
                        if (emailError.isNotEmpty()) {
                            Text(
                                text = emailError,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            // Hiển thị hướng dẫn
                            Text("Ví dụ: nguyen@gmail.com")
                        }
                    }
                )
            }
        },
        confirmButton = {
            // Nút Thêm
            TextButton(
                onClick = {
                    // Khi nhấn nút Thêm:
                    // 1. Kiểm tra tất cả field đã nhập chưa
                    if (name.isNotBlank() && studentId.isNotBlank() && email.isNotBlank()) {
                        // 2. Validate email
                        if (validateEmail(email)) {
                            // 3. Nếu hợp lệ thì gọi hàm thêm sinh viên
                            onAddStudent(name, studentId, email)
                        }
                        // Nếu email không hợp lệ, emailError sẽ hiển thị và không đóng dialog
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