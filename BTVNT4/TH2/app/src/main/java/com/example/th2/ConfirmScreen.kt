package com.example.th2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ==========================================
 * TRANG 4: CONFIRM
 * ==========================================
 * 
 * MỤC ĐÍCH:
 * - HIỂN THỊ tất cả thông tin user đã nhập từ 3 trang trước
 * - Cho user xác nhận thông tin
 * - Submit để hoàn thành và làm lại
 * 
 * DỮ LIỆU:
 * - Input: 
 *   + email (từ trang 1, lưu trong shared state)
 *   + code (từ trang 2, lưu trong shared state)
 *   + password (từ trang 3, lưu trong shared state)
 * - Output: Không có (trang cuối)
 * 
 * LUỒNG HOẠT ĐỘNG:
 * 1. Nhận email, code, password từ AppNavigation
 * 2. Hiển thị trong 3 TextField (disabled - không cho sửa)
 * 3. Password hiển thị RÕ RÀNG (không ẩn)
 * 4. Click Submit:
 *    - Lưu dữ liệu (có thể gọi API ở đây)
 *    - Reset shared state về rỗng
 *    - Quay về trang 1 để làm lại
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmScreen(
    email: String, // Nhận từ shared state (trang 1)
    code: String, // Nhận từ shared state (trang 2)
    password: String, // Nhận từ shared state (trang 3)
    onBackClick: () -> Unit, // Callback quay lại trang 3
    onSubmitClick: () -> Unit // Callback: Submit và về trang 1
) {
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
                text = "Confirm",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "We are here to help you!",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // HIỂN THỊ email (không cho sửa)
            OutlinedTextField(
                value = email, // Email từ trang 1
                onValueChange = { }, // Không làm gì (vì disabled)
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Không cho edit
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // HIỂN THỊ code (không cho sửa)
            OutlinedTextField(
                value = code,
                onValueChange = { },
                label = { Text("Verification Code") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Code Icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Không cho edit
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // HIỂN THỊ password RÕ RÀNG (không ẩn ••••, hiển thị text gốc)
            OutlinedTextField(
                value = password, // Password từ trang 3 - HIỂN THỊ RÕ RÀNG
                onValueChange = { }, // Không làm gì (vì disabled)
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Không cho edit
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Nút Submit - Lưu dữ liệu và quay về trang 1
            Button(
                onClick = onSubmitClick, // Gọi callback lên AppNavigation
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03A9F4)
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


