# BÀI TẬP TUẦN 4 - DATA FLOW NAVIGATION

## 📚 Mục tiêu bài học

1. **Hiểu về Navigation trong Jetpack Compose**
2. **Biết cách truyền dữ liệu giữa các màn hình**
3. **Hiểu về luồng dữ liệu một chiều (One-way Data Flow)**

---

## 🎯 Mô tả bài tập

Xây dựng ứng dụng "Quên mật khẩu" với **4 màn hình**:

### Màn hình 1: Forget Password
- **Chức năng**: Nhập email
- **Dữ liệu output**: email
- **File**: `ForgetPasswordScreen.kt`

### Màn hình 2: Verify Code
- **Chức năng**: Nhập code 4 số (mặc định 1111, có thể thay đổi)
- **Dữ liệu input**: email (từ màn hình 1)
- **Dữ liệu output**: code
- **File**: `VerifyCodeScreen.kt`

### Màn hình 3: Reset Password
- **Chức năng**: Nhập password mới
- **Dữ liệu input**: email, code (từ màn hình 1, 2)
- **Dữ liệu output**: password
- **File**: `ResetPasswordScreen.kt`

### Màn hình 4: Confirm
- **Chức năng**: HIỂN THỊ tất cả thông tin đã nhập
- **Dữ liệu input**: email, code, password (từ 3 màn hình trước)
- **Sau submit**: Reset và quay về màn hình 1
- **File**: `ConfirmScreen.kt`

---

## 🔄 Luồng dữ liệu (Data Flow) - 4 TRANG

```
┌─────────────────────────────┐
│   Forget Password (1)       │
│   - Nhập EMAIL              │
│   - Lưu vào currentEmail    │
└──────────┬──────────────────┘
           │ Navigate
           ▼
┌─────────────────────────────┐
│    Verify Code (2)          │
│   - Nhập CODE (4 số)        │
│   - Mặc định: "1111"        │
│   - Lưu vào currentCode     │
└──────────┬──────────────────┘
           │ Navigate
           ▼
┌─────────────────────────────┐
│   Reset Password (3)        │
│   - Nhập PASSWORD           │
│   - Nhập CONFIRM PASSWORD   │
│   - Lưu vào currentPassword │
└──────────┬──────────────────┘
           │ Navigate
           ▼
┌─────────────────────────────┐
│      Confirm (4)            │
│   - HIỂN THỊ email          │
│   - HIỂN THỊ code           │
│   - HIỂN THỊ password       │
│     (RÕ RÀNG, không ẩn)     │
│   - Submit                  │
└──────────┬──────────────────┘
           │ Reset & Navigate
           ▼
     Về màn hình 1 (làm lại)
```

---

## 📁 Cấu trúc file

```
app/src/main/java/com/example/th2/
├── MainActivity.kt              # Activity chính
├── Screen.kt                    # Định nghĩa routes (4 màn hình)
├── AppNavigation.kt             # Quản lý navigation & shared state (QUAN TRỌNG!)
├── ForgetPasswordScreen.kt      # Trang 1: Nhập email
├── VerifyCodeScreen.kt          # Trang 2: Nhập code 4 số
├── ResetPasswordScreen.kt       # Trang 3: Nhập password
└── ConfirmScreen.kt             # Trang 4: Hiển thị tất cả & Submit
```

---

## 🔑 Các khái niệm quan trọng

### 1. State (Trạng thái)
```kotlin
var email by remember { mutableStateOf("") }
```
- State lưu trữ dữ liệu trong màn hình
- Khi state thay đổi, UI tự động cập nhật (Recomposition)

### 2. Shared State (State dùng chung) ⭐
```kotlin
// Trong AppNavigation
var currentEmail by remember { mutableStateOf("") }
var currentCode by remember { mutableStateOf("") }
var currentPassword by remember { mutableStateOf("") }
```
- **State ở level AppNavigation** - cao hơn các màn hình
- **Tất cả màn hình đều truy cập được**
- Trang 1, 2, 3 GHI dữ liệu vào
- Trang 4 ĐỌC dữ liệu ra

### 3. Callback (Hàm gọi ngược)
```kotlin
onNextClick: (String) -> Unit
```
- Hàm truyền từ parent xuống child
- Child gọi callback để truyền dữ liệu lên parent
- Parent nhận và cập nhật shared state

### 4. Navigation
```kotlin
navController.navigate(Screen.VerifyCode.route)
navController.popBackStack() // Quay lại
```
- Di chuyển giữa các màn hình
- **KHÔNG truyền dữ liệu qua route** (dùng shared state)

---

## 🛠️ Cách chạy project

1. **Mở project trong Android Studio**
2. **Sync Gradle** (đợi tải dependencies)
3. **Chạy app** trên emulator hoặc thiết bị thật

---

## 📝 Giải thích chi tiết từng phần

### Screen.kt
Định nghĩa routes cho navigation. Mỗi màn hình có một route riêng.

```kotlin
sealed class Screen(val route: String) {
    object ForgetPassword : Screen("forget_password")
    object VerifyCode : Screen("verify_code/{email}") {
        fun createRoute(email: String) = "verify_code/$email"
    }
}
```

### AppNavigation.kt
Quản lý toàn bộ navigation. Đây là file quan trọng nhất!

```kotlin
NavHost(navController, startDestination = Screen.ForgetPassword.route) {
    composable(Screen.ForgetPassword.route) {
        ForgetPasswordScreen(
            onNextClick = { email ->
                // Truyền email sang màn hình tiếp theo
                navController.navigate(Screen.VerifyCode.createRoute(email))
            }
        )
    }
}
```

### Từng màn hình
- Nhận dữ liệu từ arguments hoặc parameters
- Có state riêng để lưu input của user
- Có callback để truyền dữ liệu sang màn hình tiếp theo

---

## 💡 Điểm cần lưu ý

1. **Shared State**: State lưu ở `AppNavigation`, tất cả màn hình đều truy cập được
2. **4 Trang riêng biệt**: Mỗi trang 1 chức năng rõ ràng
3. **Trang 2**: Code mặc định "1111", có thể thay đổi
4. **Trang 3**: Nhập password + confirm password, có validation
5. **Trang 4**: HIỂN THỊ tất cả thông tin (password hiển thị rõ ràng)
6. **Submit**: Reset state và quay về trang 1 để làm lại

---

## 🎓 Bài tập mở rộng

1. Thêm validation cho email (phải đúng format email)
2. Thêm timer cho mã xác thực (60 giây)
3. Thêm nút "Resend Code" ở màn hình Verify Code
4. Lưu dữ liệu vào SharedPreferences hoặc Database
5. Thêm animation khi chuyển màn hình

---

## 📚 Tài liệu tham khảo

- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- [State in Compose](https://developer.android.com/jetpack/compose/state)
- [Passing Arguments](https://developer.android.com/guide/navigation/navigation-pass-data)

---

## 👨‍💻 Tác giả

Bài tập thực hành - Lập trình Mobile
Trường Đại học Giao thông Vận tải TP.HCM (UTH)

---

## ✅ Checklist hoàn thành

- [x] Tạo 4 màn hình đầy đủ
- [x] Setup Navigation với routes đơn giản
- [x] Dùng Shared State thay vì navigation arguments
- [x] Trang 1: Nhập email
- [x] Trang 2: Nhập code 4 số (mặc định 1111)
- [x] Trang 3: Nhập password + validation
- [x] Trang 4: Hiển thị tất cả thông tin (password rõ ràng)
- [x] Nút Submit: Reset và quay về trang 1
- [x] Thêm nút Back ở mỗi trang
- [x] Validation đầy đủ
- [x] Comment code chi tiết bằng Tiếng Việt
- [x] UI đẹp, đơn giản, dễ hiểu

**Chúc bạn học tốt! 🎉**


