# HƯỚNG DẪN CODE CHI TIẾT - DATA FLOW NAVIGATION (4 TRANG)

## 📖 Mục lục
1. [Tổng quan luồng 4 trang](#1-tổng-quan-luồng-4-trang)
2. [Khái niệm Shared State](#2-khái-niệm-shared-state)
3. [Giải thích từng file](#3-giải-thích-từng-file)
4. [Flow hoàn chỉnh](#4-flow-hoàn-chỉnh)

---

## 1. Tổng quan luồng 4 trang

### Luồng hoạt động:

```
TRANG 1: Forget Password
   └─> User nhập EMAIL
   └─> Click Next
   └─> Lưu vào currentEmail
   └─> Navigate sang Trang 2

TRANG 2: Verify Code
   └─> User nhập CODE (4 số)
   └─> Click Next
   └─> Lưu vào currentCode
   └─> Navigate sang Trang 3

TRANG 3: Reset Password
   └─> User nhập PASSWORD
   └─> User nhập CONFIRM PASSWORD
   └─> Kiểm tra 2 password có khớp không
   └─> Click Next
   └─> Lưu vào currentPassword
   └─> Navigate sang Trang 4

TRANG 4: Confirm
   └─> HIỂN THỊ:
       - Email (từ currentEmail)
       - Code (từ currentCode)
       - Password RÕ RÀNG (từ currentPassword)
   └─> Click Submit
   └─> Lưu dữ liệu (có thể gọi API)
   └─> Reset state về rỗng
   └─> Quay về Trang 1 để làm lại
```

---

## 2. Khái niệm Shared State

### State thông thường vs Shared State

#### State Local (chỉ dùng trong 1 màn hình):
```kotlin
@Composable
fun ForgetPasswordScreen() {
    // State này CHỈ tồn tại trong ForgetPasswordScreen
    var email by remember { mutableStateOf("") }
    
    // Khi rời khỏi màn hình -> state MẤT
}
```

#### Shared State (dùng chung cho nhiều màn hình):
```kotlin
@Composable
fun AppNavigation() {
    // State này tồn tại ở level AppNavigation
    // TẤT CẢ màn hình đều có thể truy cập
    var currentEmail by remember { mutableStateOf("") }
    
    // Trang 1: Lưu vào state
    ForgetPasswordScreen(onNextClick = { email ->
        currentEmail = email
    })
    
    // Trang 4: Đọc từ state
    ConfirmScreen(email = currentEmail)
}
```

### Tại sao dùng Shared State?

**VẤN ĐỀ**: Làm sao trang 4 biết được email mà user nhập ở trang 1?

**GIẢI PHÁP 1** (SAI - Phức tạp):
```
Trang 1 -> Trang 2 (truyền email qua URL)
Trang 2 -> Trang 3 (truyền email + code qua URL)
Trang 3 -> Trang 4 (truyền email + code + password qua URL)
```
❌ Phức tạp, dễ bị lỗi khi có ký tự đặc biệt (@, #, ...)

**GIẢI PHÁP 2** (ĐÚNG - Đơn giản):
```kotlin
// Lưu trong Shared State
var currentEmail = ""    // ← State chung
var currentCode = ""     // ← State chung
var currentPassword = "" // ← State chung

// Tất cả trang đều đọc/ghi từ đây
```
✅ Đơn giản, an toàn, dễ hiểu!

---

## 3. Giải thích từng file

### 📄 Screen.kt

```kotlin
sealed class Screen(val route: String) {
    object ForgetPassword : Screen("forget_password")
    object VerifyCode : Screen("verify_code")
    object ResetPassword : Screen("reset_password")
    object Confirm : Screen("confirm")
}
```

**Giải thích**:
- `sealed class`: Class đặc biệt, chỉ có số lượng subclass cố định
- `object`: Singleton, chỉ có 1 instance duy nhất
- `route`: Đường dẫn định danh cho màn hình

**Sử dụng**:
```kotlin
navController.navigate(Screen.VerifyCode.route) 
// -> Navigate đến "verify_code"
```

---

### 📄 ForgetPasswordScreen.kt (TRANG 1)

#### Khai báo:
```kotlin
@Composable
fun ForgetPasswordScreen(
    onNextClick: (String) -> Unit  // Callback nhận 1 String (email)
) {
    var email by remember { mutableStateOf("") }  // State local
```

**Phân tích**:
- `onNextClick: (String) -> Unit`: 
  - `(String)`: Nhận 1 tham số kiểu String
  - `-> Unit`: Không trả về gì
  - Là 1 hàm callback để truyền email lên parent

- `var email by remember { mutableStateOf("") }`:
  - `var`: Biến có thể thay đổi
  - `by`: Property delegate
  - `remember`: Giữ giá trị khi recompose
  - `mutableStateOf("")`: State có giá trị ban đầu rỗng

#### TextField:
```kotlin
OutlinedTextField(
    value = email,              // Giá trị từ state
    onValueChange = { email = it },  // Cập nhật state khi user nhập
    label = { Text("Your Email") }
)
```

**Cách hoạt động**:
1. User nhập "u" → `onValueChange` được gọi với `it = "u"`
2. `email = "u"` → State cập nhật
3. Compose recompose
4. TextField hiển thị "u"
5. User nhập tiếp "ser" → Lặp lại...

#### Button Next:
```kotlin
Button(onClick = {
    if (email.isNotEmpty()) {
        onNextClick(email)  // Gọi callback và truyền email
    }
})
```

**Khi click**:
1. Kiểm tra email không rỗng
2. Gọi `onNextClick(email)`
3. Trong `AppNavigation` sẽ nhận email này

---

### 📄 VerifyCodeScreen.kt (TRANG 2)

#### Khai báo:
```kotlin
@Composable
fun VerifyCodeScreen(
    onBackClick: () -> Unit,      // Callback không tham số
    onNextClick: (String) -> Unit // Callback nhận code
) {
    // 4 state riêng cho 4 ô nhập
    var code1 by remember { mutableStateOf("1") }  // Mặc định "1"
    var code2 by remember { mutableStateOf("1") }
    var code3 by remember { mutableStateOf("1") }
    var code4 by remember { mutableStateOf("1") }
```

**Tại sao 4 state?**
Vì có 4 ô TextField riêng biệt, mỗi ô quản lý 1 số.

#### 4 ô nhập code:
```kotlin
Row {
    CodeInputBox(
        value = code1,
        onValueChange = { if (it.length <= 1) code1 = it }
    )
    // Tương tự cho code2, code3, code4
}
```

**Validation**: `if (it.length <= 1)` 
- Chỉ cho nhập tối đa 1 ký tự

#### Button Next:
```kotlin
Button(onClick = {
    val fullCode = code1 + code2 + code3 + code4  // "1" + "2" + "3" + "4" = "1234"
    if (fullCode.length == 4) {
        onNextClick(fullCode)
    }
})
```

**Ghép code**:
- `code1 = "1"`, `code2 = "2"`, `code3 = "3"`, `code4 = "4"`
- `fullCode = "1234"`

---

### 📄 ResetPasswordScreen.kt (TRANG 3)

#### Khai báo:
```kotlin
@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit  // Nhận password
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
```

**3 state**:
- `password`: Mật khẩu mới
- `confirmPassword`: Nhập lại mật khẩu
- `errorMessage`: Thông báo lỗi (nếu có)

#### Validation khi click Next:
```kotlin
Button(onClick = {
    if (password.isEmpty() || confirmPassword.isEmpty()) {
        errorMessage = "Vui lòng nhập đầy đủ thông tin"
    } else if (password != confirmPassword) {
        errorMessage = "Mật khẩu không khớp"
    } else {
        onNextClick(password)  // OK -> Truyền password
    }
})
```

**Luồng kiểm tra**:
1. Kiểm tra rỗng → Hiển thị lỗi
2. Kiểm tra khớp → Hiển thị lỗi
3. OK → Gọi callback

---

### 📄 ConfirmScreen.kt (TRANG 4)

#### Khai báo:
```kotlin
@Composable
fun ConfirmScreen(
    email: String,       // Nhận từ shared state
    code: String,        // Nhận từ shared state
    password: String,    // Nhận từ shared state
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit  // Không tham số
)
```

**Điểm quan trọng**: 
- NHẬN dữ liệu từ shared state
- KHÔNG tự nhập nữa

#### Hiển thị email:
```kotlin
OutlinedTextField(
    value = email,          // Email từ trang 1
    onValueChange = { },    // Không làm gì
    enabled = false         // Không cho sửa
)
```

**enabled = false**: TextField bị disable, chỉ hiển thị, không edit được

#### Hiển thị password RÕ RÀNG:
```kotlin
OutlinedTextField(
    value = password,  // Hiển thị password THẬT, không ẩn
    enabled = false
)
```

**KHÔNG dùng** `PasswordVisualTransformation()` → Hiển thị text gốc!

#### Button Submit:
```kotlin
Button(onClick = onSubmitClick)
```

Đơn giản, chỉ gọi callback!

---

### 📄 AppNavigation.kt (QUAN TRỌNG NHẤT!)

#### Khai báo Shared State:
```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // ===== SHARED STATE =====
    var currentEmail by remember { mutableStateOf("") }
    var currentCode by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
```

**Vị trí khai báo**: Ở `AppNavigation`, KHÔNG ở màn hình con!

#### Trang 1:
```kotlin
composable(route = Screen.ForgetPassword.route) {
    ForgetPasswordScreen(
        onNextClick = { email ->
            currentEmail = email           // LƯU vào shared state
            navController.navigate(...)    // Navigate
        }
    )
}
```

**Luồng**:
1. User nhập email ở ForgetPasswordScreen
2. Click Next → gọi `onNextClick(email)`
3. AppNavigation nhận `email`
4. Lưu vào `currentEmail`
5. Navigate sang trang 2

#### Trang 2:
```kotlin
composable(route = Screen.VerifyCode.route) {
    VerifyCodeScreen(
        onBackClick = { navController.popBackStack() },
        onNextClick = { code ->
            currentCode = code             // LƯU vào shared state
            navController.navigate(...)
        }
    )
}
```

Tương tự trang 1!

#### Trang 3:
```kotlin
composable(route = Screen.ResetPassword.route) {
    ResetPasswordScreen(
        onBackClick = { navController.popBackStack() },
        onNextClick = { password ->
            currentPassword = password     // LƯU vào shared state
            navController.navigate(...)
        }
    )
}
```

#### Trang 4:
```kotlin
composable(route = Screen.Confirm.route) {
    ConfirmScreen(
        email = currentEmail,         // ĐỌC từ shared state
        code = currentCode,           // ĐỌC từ shared state
        password = currentPassword,   // ĐỌC từ shared state
        onBackClick = { navController.popBackStack() },
        onSubmitClick = {
            // Lưu dữ liệu (gọi API...)
            
            // Reset state
            currentEmail = ""
            currentCode = ""
            currentPassword = ""
            
            // Quay về trang 1
            navController.popBackStack(Screen.ForgetPassword.route, false)
        }
    )
}
```

**Khi Submit**:
1. Lưu dữ liệu (có thể gọi API)
2. Reset tất cả state về rỗng
3. Quay về trang 1
4. User có thể làm lại

---

## 4. Flow hoàn chỉnh

### Ví dụ cụ thể:

#### BƯỚC 1: Trang 1
```
User mở app
↓
ForgetPasswordScreen hiển thị
↓
User nhập email: "test@gmail.com"
↓
State local: email = "test@gmail.com"
↓
User click Next
↓
onNextClick("test@gmail.com") được gọi
↓
AppNavigation nhận "test@gmail.com"
↓
currentEmail = "test@gmail.com"  ← LƯU VÀO SHARED STATE
↓
navigate(Screen.VerifyCode.route)
↓
Chuyển sang Trang 2
```

#### BƯỚC 2: Trang 2
```
VerifyCodeScreen hiển thị
↓
4 ô code hiển thị mặc định "1111"
↓
User thay đổi thành: "5", "6", "7", "8"
↓
State local: code1="5", code2="6", code3="7", code4="8"
↓
User click Next
↓
fullCode = "5678"
↓
onNextClick("5678") được gọi
↓
AppNavigation nhận "5678"
↓
currentCode = "5678"  ← LƯU VÀO SHARED STATE
↓
navigate(Screen.ResetPassword.route)
↓
Chuyển sang Trang 3
```

#### BƯỚC 3: Trang 3
```
ResetPasswordScreen hiển thị
↓
User nhập password: "MyPass123"
↓
User nhập confirm: "MyPass123"
↓
State local: password="MyPass123", confirmPassword="MyPass123"
↓
User click Next
↓
Kiểm tra: password == confirmPassword? → OK!
↓
onNextClick("MyPass123") được gọi
↓
AppNavigation nhận "MyPass123"
↓
currentPassword = "MyPass123"  ← LƯU VÀO SHARED STATE
↓
navigate(Screen.Confirm.route)
↓
Chuyển sang Trang 4
```

#### BƯỚC 4: Trang 4
```
ConfirmScreen hiển thị
↓
Nhận từ AppNavigation:
  - email = "test@gmail.com"
  - code = "5678"
  - password = "MyPass123"
↓
Hiển thị trong 3 TextField:
  Email: test@gmail.com (disabled)
  Code: 5678 (disabled)
  Password: MyPass123 (disabled, RÕ RÀNG)
↓
User kiểm tra thông tin → OK!
↓
User click Submit
↓
onSubmitClick() được gọi
↓
AppNavigation xử lý:
  1. Lưu dữ liệu (gọi API...)
  2. currentEmail = ""
  3. currentCode = ""
  4. currentPassword = ""
  5. popBackStack về ForgetPassword
↓
Về lại Trang 1 (rỗng, sẵn sàng làm lại)
```

---

## 📊 Sơ đồ Data Flow

```
┌─────────────────────────────────────────┐
│         AppNavigation                   │
│    (Parent - Quản lý Shared State)      │
│                                         │
│  var currentEmail = ""                  │
│  var currentCode = ""                   │
│  var currentPassword = ""               │
└───┬──────────┬──────────┬──────────┬───┘
    │          │          │          │
    │ GHI      │ GHI      │ GHI      │ ĐỌC
    ▼          ▼          ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│ Trang 1│ │ Trang 2│ │ Trang 3│ │ Trang 4│
│        │ │        │ │        │ │        │
│ Nhập   │ │ Nhập   │ │ Nhập   │ │ Hiển   │
│ Email  │ │ Code   │ │Password│ │ thị    │
│   ↓    │ │   ↓    │ │   ↓    │ │ tất cả │
│ Lưu   │ │ Lưu   │ │ Lưu   │ │        │
│ email  │ │ code   │ │password│ │        │
└────────┘ └────────┘ └────────┘ └────────┘
```

---

## 💡 Tóm tắt các khái niệm

### 1. State
```kotlin
var email by remember { mutableStateOf("") }
```
- Lưu trữ dữ liệu
- Khi thay đổi → UI tự động cập nhật
- `remember`: Giữ giá trị khi recompose

### 2. Shared State
```kotlin
// Khai báo ở AppNavigation
var currentEmail by remember { mutableStateOf("") }

// Trang con GHI
onNextClick = { email -> currentEmail = email }

// Trang con ĐỌC
ConfirmScreen(email = currentEmail)
```
- State ở level cao (parent)
- Nhiều màn hình truy cập được

### 3. Callback
```kotlin
// Khai báo
onNextClick: (String) -> Unit

// Gọi
onNextClick("data")
```
- Hàm truyền từ parent xuống child
- Child gọi để truyền data lên parent

### 4. Navigation
```kotlin
// Chuyển màn hình
navController.navigate(Screen.VerifyCode.route)

// Quay lại
navController.popBackStack()

// Quay về màn hình cụ thể
navController.popBackStack(Screen.ForgetPassword.route, false)
```

---

## ⚠️ Lưu ý quan trọng

### 1. Vị trí khai báo State
```kotlin
// ĐÚNG - Shared State ở AppNavigation
@Composable
fun AppNavigation() {
    var currentEmail by remember { mutableStateOf("") }
}

// SAI - State ở màn hình con (sẽ mất khi chuyển trang)
@Composable
fun ForgetPasswordScreen() {
    var currentEmail by remember { mutableStateOf("") }
}
```

### 2. Cập nhật Shared State
```kotlin
// ĐÚNG - Cập nhật qua callback
onNextClick = { email ->
    currentEmail = email  // Parent cập nhật
}

// SAI - Màn hình con không thể trực tiếp cập nhật
@Composable
fun ForgetPasswordScreen(currentEmail: String) {
    currentEmail = "new"  // LỖI!
}
```

### 3. Hiển thị password ở trang 4
```kotlin
// ĐÚNG - Hiển thị rõ ràng
OutlinedTextField(value = password, ...)

// SAI - Hiển thị ẩn (không đúng yêu cầu)
OutlinedTextField(
    value = password, 
    visualTransformation = PasswordVisualTransformation()
)
```

---

## 🚀 Tips học tốt

1. **Đọc code theo thứ tự**: 
   - AppNavigation (hiểu shared state)
   - Trang 1 → 2 → 3 → 4

2. **Chạy app và quan sát**:
   - Nhập dữ liệu ở mỗi trang
   - Xem dữ liệu hiển thị ở trang 4

3. **Debug bằng Log**:
```kotlin
Log.d("TAG", "Email: $currentEmail")
Log.d("TAG", "Code: $currentCode")
```

4. **Vẽ sơ đồ**: Vẽ flow trên giấy để hiểu rõ hơn

---

## 📝 Câu hỏi ôn tập

1. **Shared State là gì? Khác gì với State thông thường?**
2. **Tại sao phải khai báo state ở AppNavigation?**
3. **Làm sao trang 4 nhận được email từ trang 1?**
4. **Tại sao trang 4 hiển thị password rõ ràng, không ẩn?**
5. **Khi click Submit, điều gì xảy ra?**

---

## ✅ Checklist tự kiểm tra

- [ ] Hiểu Shared State hoạt động như thế nào
- [ ] Biết cách khai báo state ở parent
- [ ] Biết cách truyền state xuống child
- [ ] Biết cách cập nhật state qua callback
- [ ] Hiểu luồng dữ liệu từ trang 1 → 4
- [ ] Hiểu tại sao trang 4 hiển thị tất cả thông tin
- [ ] Hiểu cách Submit reset state và quay về trang 1

---

**Chúc bạn học tốt! Đây là luồng chuẩn cho Data Flow Navigation! 💪**
