package com.example.th2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ==========================================
 * TRANG 1: FORGET PASSWORD
 * ==========================================
 * 
 * MỤC ĐÍCH:
 * - Cho user nhập email để bắt đầu quá trình quên mật khẩu
 * 
 * DỮ LIỆU:
 * - Input: Không có (trang đầu tiên)
 * - Output: email (truyền sang trang 2 qua callback onNextClick)
 * 
 * LUỒNG HOẠT ĐỘNG:
 * 1. User nhập email vào TextField
 * 2. Email được lưu vào state local (email)
 * 3. Click Next -> gọi onNextClick(email)
 * 4. AppNavigation nhận email và lưu vào shared state
 * 5. Navigate sang trang 2
 */
@Composable
fun ForgetPasswordScreen(
    onNextClick: (String) -> Unit // Callback: truyền email lên AppNavigation
) {
    // State local: lưu email mà user đang nhập
    // remember: giữ giá trị khi recompose
    // mutableStateOf: tạo state có thể thay đổi
    var email by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo UTH
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "UTH Logo",
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tiêu đề ứng dụng
        Text(
            text = "SmartTasks",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF03A9F4)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Tiêu đề
        Text(
            text = "Forget Password?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mô tả
        Text(
            text = "Enter your Email, we will send you a verification code.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // TextField nhập email
        OutlinedTextField(
            value = email, // Giá trị hiện tại từ state
            onValueChange = { email = it }, // Khi user nhập -> cập nhật state
            label = { Text("Your Email") }, // Label hiển thị
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Nút Next - truyền email sang trang 2
        Button(
            onClick = {
                // Kiểm tra email không rỗng
                if (email.isNotEmpty()) {
                    // Gọi callback và truyền email lên AppNavigation
                    // AppNavigation sẽ lưu vào currentEmail và navigate sang trang 2
                    onNextClick(email)
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


