package com.example.th2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ==========================================
 * TRANG 2: VERIFY CODE
 * ==========================================
 * 
 * MỤC ĐÍCH:
 * - Cho user nhập mã xác thực 4 số (hoặc để mặc định 1111)
 * 
 * DỮ LIỆU:
 * - Input: Không cần (email đã lưu trong shared state)
 * - Output: code (4 số, truyền sang trang 3)
 * 
 * LUỒNG HOẠT ĐỘNG:
 * 1. Màn hình hiển thị 4 ô nhập số, mặc định "1111"
 * 2. User có thể giữ nguyên hoặc thay đổi
 * 3. Click Next -> ghép 4 số thành string (vd: "1234")
 * 4. Gọi onNextClick(code)
 * 5. AppNavigation nhận code và lưu vào shared state
 * 6. Navigate sang trang 3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    onBackClick: () -> Unit, // Callback quay lại trang 1
    onNextClick: (String) -> Unit // Callback: truyền code lên AppNavigation
) {
    // 4 State riêng cho 4 ô nhập số (mặc định "1111")
    // Tại sao 4 state? Vì mỗi ô TextField quản lý riêng 1 số
    var code1 by remember { mutableStateOf("1") }
    var code2 by remember { mutableStateOf("1") }
    var code3 by remember { mutableStateOf("1") }
    var code4 by remember { mutableStateOf("1") }
    
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
                text = "Verify Code",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter the the code\nwe just sent you on your registered Email",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 4 ô nhập code (mặc định 1111, có thể thay đổi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CodeInputBox(
                    value = code1,
                    onValueChange = { if (it.length <= 1) code1 = it }
                )
                CodeInputBox(
                    value = code2,
                    onValueChange = { if (it.length <= 1) code2 = it }
                )
                CodeInputBox(
                    value = code3,
                    onValueChange = { if (it.length <= 1) code3 = it }
                )
                CodeInputBox(
                    value = code4,
                    onValueChange = { if (it.length <= 1) code4 = it }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Nút Next - truyền code sang trang 3
            Button(
                onClick = {
                    // Ghép 4 số lại thành 1 string
                    // Ví dụ: "1" + "2" + "3" + "4" = "1234"
                    val fullCode = code1 + code2 + code3 + code4
                    
                    // Kiểm tra đủ 4 số
                    if (fullCode.length == 4) {
                        // Gọi callback và truyền code lên AppNavigation
                        onNextClick(fullCode)
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

/**
 * Component nhỏ để hiển thị 1 ô nhập số
 */
@Composable
fun CodeInputBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (value.isNotEmpty()) Color(0xFF03A9F4) else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


