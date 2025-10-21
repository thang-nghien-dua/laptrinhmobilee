package com.example.th2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.th2.ui.theme.Th2Theme

/**
 * MainActivity - Activity chính của app
 * 
 * Đây là điểm khởi đầu của ứng dụng
 * Chúng ta sử dụng Jetpack Compose để xây dựng UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bật chế độ edge-to-edge (toàn màn hình)
        enableEdgeToEdge()
        
        // setContent là hàm đặt nội dung UI cho Activity
        setContent {
            // Theme của app
            Th2Theme {
                // AppNavigation quản lý toàn bộ navigation và data flow
                // Tất cả logic chuyển màn hình và truyền dữ liệu đều ở đây
                AppNavigation()
            }
        }
    }
}