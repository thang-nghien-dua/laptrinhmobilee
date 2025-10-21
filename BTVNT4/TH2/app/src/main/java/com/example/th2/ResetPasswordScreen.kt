package com.example.th2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ==========================================
 * TRANG 3: RESET PASSWORD
 * ==========================================
 * 
 * MỤC ĐÍCH:
 * - Cho user nhập mật khẩu mới
 * - Xác nhận mật khẩu (nhập 2 lần)
 * 
 * DỮ LIỆU:
 * - Input: Không cần (email và code đã lưu trong shared state)
 * - Output: password (truyền sang trang 4)
 * 
 * LUỒNG HOẠT ĐỘNG:
 * 1. User nhập password vào TextField 1
 * 2. User nhập lại password vào TextField 2 (confirm)
 * 3. Click Next -> kiểm tra 2 password có khớp không
 * 4. Nếu khớp -> gọi onNextClick(password)
 * 5. Nếu không khớp -> hiển thị lỗi
 * 6. AppNavigation nhận password và lưu vào shared state
 * 7. Navigate sang trang 4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit, // Callback quay lại trang 2
    onNextClick: (String) -> Unit // Callback: truyền password lên AppNavigation
) {
    // State lưu password user nhập
    var password by remember { mutableStateOf("") }
    // State lưu password confirm (nhập lại để xác nhận)
    var confirmPassword by remember { mutableStateOf("") }
    // State lưu thông báo lỗi (nếu có)
    var errorMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Nút back
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF03A9F4)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "UTH Logo",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "SmartTasks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF03A9F4)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "Create new password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your new password must be different form\npreviously used password",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // TextField nhập password
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TextField confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    errorMessage = ""
                },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage.isNotEmpty()
            )
            
            // Hiển thị lỗi
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Nút Next - truyền password sang trang 4
            Button(
                onClick = {
                    // VALIDATION: Kiểm tra dữ liệu trước khi chuyển trang
                    
                    // Bước 1: Kiểm tra có nhập đủ không
                    if (password.isEmpty() || confirmPassword.isEmpty()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin"
                    } 
                    // Bước 2: Kiểm tra 2 password có khớp không
                    else if (password != confirmPassword) {
                        errorMessage = "Mật khẩu không khớp"
                    } 
                    // Bước 3: OK -> truyền password sang trang 4
                    else {
                        onNextClick(password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03A9F4)
                )
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

