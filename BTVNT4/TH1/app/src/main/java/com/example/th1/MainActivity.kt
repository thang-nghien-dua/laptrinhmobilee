package com.example.th1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.th1.ui.theme.TH1Theme
import com.example.th1.fragments.BorrowManagementScreen
import com.example.th1.fragments.BookManagementScreen
import com.example.th1.fragments.StudentManagementScreen

/**
 * MainActivity - Activity chính của ứng dụng
 * Đây là điểm khởi đầu khi mở app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bật edge-to-edge để app hiển thị toàn màn hình
        enableEdgeToEdge()
        
        // Thiết lập nội dung UI
        setContent {
            // Áp dụng theme màu sắc cho toàn app
            TH1Theme {
                // Gọi hàm hiển thị UI chính
                LibraryManagementApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryManagementApp() {

    var selectedTab by remember { mutableIntStateOf(0) }
    

    val tabs = listOf(
        TabItem("Quản lý", Icons.Filled.Home),      // Tab 0: Màn hình mượn/trả sách
        TabItem("DS Sách", Icons.Filled.Menu),       // Tab 1: Màn hình quản lý sách
        TabItem("Sinh viên", Icons.Filled.Person)    // Tab 2: Màn hình quản lý sinh viên
    )
    

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hệ thống Quản lý Thư viện",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,  // Nền xanh
                    titleContentColor = MaterialTheme.colorScheme.onPrimary  // Chữ trắng
                )
            )
        },

        bottomBar = {
            NavigationBar {

                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {

                            Icon(tab.icon, contentDescription = tab.title) 
                        },
                        label = { 
                            // Text của tab
                            Text(tab.title) 
                        },
                        // Tab này có đang được chọn không
                        selected = selectedTab == index,
                        onClick = { 
                            // Khi click vào tab, chuyển selectedTab
                            // Navigation đơn giản: chỉ cần đổi số index
                            selectedTab = index 
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Nội dung chính của màn hình
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)  
        ) {
           
            when (selectedTab) {
                0 -> BorrowManagementScreen()   // Tab Quản lý
                1 -> BookManagementScreen()     // Tab DS Sách
                2 -> StudentManagementScreen()  // Tab Sinh viên
            }
        }
    }
}


data class TabItem(
    val title: String,
    val icon: ImageVector
)