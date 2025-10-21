package com.example.th2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * ==========================================
 * APP NAVIGATION - QUẢN LÝ NAVIGATION VÀ DATA FLOW
 * ==========================================
 * 
 * FILE NÀY LÀ TÂM ĐIỂM CỦA ỨNG DỤNG!
 * Quản lý:
 * 1. Navigation: Di chuyển giữa các màn hình
 * 2. Data Flow: Luồng dữ liệu giữa các màn hình
 * 
 * LUỒNG 4 TRANG:
 * Trang 1 (Forget Password): Nhập email -> Next
 *    ↓ (truyền email)
 * Trang 2 (Verify Code): Nhập code 4 số -> Next
 *    ↓ (truyền code)
 * Trang 3 (Reset Password): Nhập password -> Next
 *    ↓ (truyền password)
 * Trang 4 (Confirm): HIỂN THỊ tất cả -> Submit
 *    ↓ (reset và quay lại)
 * Trang 1 (Làm lại)
 * 
 * SHARED STATE - KHÁI NIỆM QUAN TRỌNG:
 * - State được khai báo ở đây (AppNavigation)
 * - Tất cả các màn hình đều có thể đọc/ghi state này
 * - Khi state thay đổi -> màn hình tự động cập nhật
 */
@Composable
fun AppNavigation() {
    // NavController: Điều khiển navigation (chuyển màn hình)
    val navController = rememberNavController()
    
    // ===== SHARED STATE - Dữ liệu dùng chung =====
    // Lưu ở đây để tất cả màn hình đều truy cập được
    var currentEmail by remember { mutableStateOf("") } // Email từ trang 1
    var currentCode by remember { mutableStateOf("") } // Code từ trang 2
    var currentPassword by remember { mutableStateOf("") } // Password từ trang 3
    
    // NavHost chứa tất cả các màn hình
    NavHost(
        navController = navController,
        startDestination = Screen.ForgetPassword.route // Màn hình đầu tiên
    ) {
        
        // ===== TRANG 1: FORGET PASSWORD - Nhập email =====
        composable(route = Screen.ForgetPassword.route) {
            ForgetPasswordScreen(
                onNextClick = { email ->
                    // BƯỚC 1: Nhận email từ ForgetPasswordScreen
                    // BƯỚC 2: Lưu email vào shared state
                    currentEmail = email
                    // BƯỚC 3: Navigate sang trang 2
                    navController.navigate(Screen.VerifyCode.route)
                }
            )
        }
        
        // ===== TRANG 2: VERIFY CODE - Nhập code =====
        composable(route = Screen.VerifyCode.route) {
            VerifyCodeScreen(
                onBackClick = {
                    // Quay lại trang trước (trang 1)
                    navController.popBackStack()
                },
                onNextClick = { code ->
                    // BƯỚC 1: Nhận code từ VerifyCodeScreen
                    // BƯỚC 2: Lưu code vào shared state
                    currentCode = code
                    // BƯỚC 3: Navigate sang trang 3
                    navController.navigate(Screen.ResetPassword.route)
                }
            )
        }
        
        // ===== TRANG 3: RESET PASSWORD - Nhập password =====
        composable(route = Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClick = {
                    // Quay lại trang trước (trang 2)
                    navController.popBackStack()
                },
                onNextClick = { password ->
                    // BƯỚC 1: Nhận password từ ResetPasswordScreen
                    // BƯỚC 2: Lưu password vào shared state
                    currentPassword = password
                    // BƯỚC 3: Navigate sang trang 4 (Confirm)
                    navController.navigate(Screen.Confirm.route)
                }
            )
        }
        
        // ===== TRANG 4: CONFIRM - Hiển thị tất cả =====
        composable(route = Screen.Confirm.route) { 
            ConfirmScreen(
                // Truyền TẤT CẢ dữ liệu từ shared state xuống ConfirmScreen
                email = currentEmail, // Từ trang 1 (đã lưu trong shared state)
                code = currentCode, // Từ trang 2 (đã lưu trong shared state)
                password = currentPassword, // Từ trang 3 (đã lưu trong shared state)
                
                onBackClick = {
                    // Quay lại trang trước (trang 3)
                    navController.popBackStack()
                },
                
                onSubmitClick = {
                    // ===== KHI USER CLICK SUBMIT =====
                    
                    // BƯỚC 1: Lưu dữ liệu
                    // Đây là nơi bạn có thể:
                    // - Gọi API để gửi lên server
                    // - Lưu vào database local
                    // - Xử lý logic khác
                    // Ví dụ: apiService.resetPassword(currentEmail, currentCode, currentPassword)
                    
                    // BƯỚC 2: Reset shared state về rỗng
                    // Để user có thể làm lại từ đầu
                    currentEmail = ""
                    currentCode = ""
                    currentPassword = ""
                    
                    // BƯỚC 3: Quay về trang 1 để làm lại
                    // popBackStack: xóa tất cả màn hình khỏi stack
                    // inclusive = false: giữ lại trang ForgetPassword
                    navController.popBackStack(Screen.ForgetPassword.route, inclusive = false)
                }
            )
        }
    }
}

/**
 * ==========================================
 * TÓM TẮT DATA FLOW - LUỒNG DỮ LIỆU
 * ==========================================
 * 
 * Trang 1 (Forget Password):
 *   User nhập email: "user@gmail.com"
 *   -> onNextClick("user@gmail.com")
 *   -> currentEmail = "user@gmail.com"
 *   -> Navigate trang 2
 * 
 * Trang 2 (Verify Code):
 *   User nhập code: "1234"
 *   -> onNextClick("1234")
 *   -> currentCode = "1234"
 *   -> Navigate trang 3
 * 
 * Trang 3 (Reset Password):
 *   User nhập password: "Pass123"
 *   -> onNextClick("Pass123")
 *   -> currentPassword = "Pass123"
 *   -> Navigate trang 4
 * 
 * Trang 4 (Confirm):
 *   Nhận: currentEmail, currentCode, currentPassword
 *   Hiển thị:
 *     - Email: "user@gmail.com"
 *     - Code: "1234"
 *     - Password: "Pass123" (RÕ RÀNG, không ẩn)
 *   
 *   User click Submit:
 *     -> Lưu dữ liệu (gọi API...)
 *     -> currentEmail = ""
 *     -> currentCode = ""
 *     -> currentPassword = ""
 *     -> popBackStack về trang 1
 *     -> User có thể làm lại
 * 
 * ĐIỂM QUAN TRỌNG:
 * - Dùng SHARED STATE thay vì truyền qua navigation arguments
 * - An toàn, không crash khi có ký tự đặc biệt
 * - Đơn giản, dễ hiểu, dễ bảo trì
 */


