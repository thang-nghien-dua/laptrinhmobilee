package com.example.th2

/**
 * File này định nghĩa các routes (đường dẫn) cho navigation
 * 4 màn hình theo thứ tự:
 * 1. Forget Password - Nhập email
 * 2. Verify Code - Nhập code 4 số
 * 3. Reset Password - Nhập password mới
 * 4. Confirm - Hiển thị tất cả thông tin
 */
sealed class Screen(val route: String) {
    // Trang 1: Nhập email
    object ForgetPassword : Screen("forget_password")
    
    // Trang 2: Nhập code
    object VerifyCode : Screen("verify_code")
    
    // Trang 3: Nhập password
    object ResetPassword : Screen("reset_password")
    
    // Trang 4: Hiển thị kết quả
    object Confirm : Screen("confirm")
}


