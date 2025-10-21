# 📚 HƯỚNG DẪN CODE - HỆ THỐNG QUẢN LÝ THƯ VIỆN

## 📁 CẤU TRÚC THƯ MỤC

```
app/src/main/java/com/example/th1/
├── MainActivity.kt                      ⭐ File chính, điểm khởi đầu app
├── model/                               📦 Chứa các data class
│   ├── Student.kt                       👨‍🎓 Model sinh viên
│   ├── Book.kt                          📖 Model sách
│   └── BorrowRecord.kt                  📝 Model bản ghi mượn sách
├── data/
│   └── dữ liệu (Singleton)
├── fragments/                           📱 Các màn hình UI
│   ├── BorrowManagementScreen.kt        🏠 Màn hình mượn/trả sách
│   ├── BookManagementScreen.kt          📚 Màn hình quản lý sách
│   ├── StudentManagementScreen.kt       👥 MànLibraryDataManager.kt            💾 Quản lý tất cả  hình quản lý sinh viên
│   └── BookSelectionDialog.kt           ✅ Dialog chọn sách để mượn
└── ui/theme/                            🎨 Màu sắc và theme
    ├── Color.kt                         🌈 Định nghĩa màu
    └── Theme.kt                         🎨 Theme của app
```

---

## ⭐ 1. MainActivity.kt

**Vị trí:** `app/src/main/java/com/example/th1/MainActivity.kt`

### Nhiệm vụ:
- File chính khởi động app
- Quản lý navigation giữa 3 màn hình
- Hiển thị TopBar và BottomNavigationBar

### Code quan trọng:

```kotlin
// Biến lưu tab đang được chọn (0, 1, hoặc 2)
var selectedTab by remember { mutableIntStateOf(0) }

// Khi click vào tab, đổi số index
NavigationBarItem(
    onClick = { selectedTab = index }
)

// Hiển thị màn hình tương ứng
when (selectedTab) {
    0 -> BorrowManagementScreen()   // Quản lý mượn/trả
    1 -> BookManagementScreen()     // Quản lý sách
    2 -> StudentManagementScreen()  // Quản lý sinh viên
}
```

### Navigation đơn giản:
- Không dùng NavController phức tạp
- Chỉ dùng `when` để chuyển màn hình
- Khi click tab → đổi số → màn hình tự đổi

---

## 💾 2. LibraryDataManager.kt (QUAN TRỌNG NHẤT!)

**Vị trí:** `app/src/main/java/com/example/th1/data/LibraryDataManager.kt`

### Tại sao quan trọng?
- **Singleton**: Chỉ có 1 instance duy nhất trong toàn app
- **Share data**: Tất cả màn hình dùng chung dữ liệu này
- **Auto-update UI**: Dùng `mutableStateListOf` nên UI tự động cập nhật

### Code quan trọng:

```kotlin
// object = Singleton (chỉ có 1 instance)
object LibraryDataManager {
    // mutableStateListOf tự động update UI khi thay đổi
    private val students = mutableStateListOf<Student>()
    private val books = mutableStateListOf<Book>()
    private val borrowRecords = mutableStateListOf<BorrowRecord>()
    
    // Hàm thêm sinh viên
    fun addStudent(name: String, studentId: String, email: String) {
        val newId = students.maxOfOrNull { it.id }?.plus(1) ?: 1
        students.add(Student(newId, name, studentId, email))
    }
    
    // Hàm mượn sách
    fun borrowBooks(studentId: Int, bookIds: List<Int>): Boolean {
        // ... logic mượn sách
    }
    
    // Hàm trả sách
    fun returnBookByStudentAndBook(studentId: Int, bookId: Int): Boolean {
        // ... logic trả sách
    }
}
```

### Cách sử dụng trong màn hình:

```kotlin
@Composable
fun StudentManagementScreen() {
    // Lấy singleton instance (không cần remember)
    val libraryDataManager = LibraryDataManager
    
    // Lấy dữ liệu
    val students = libraryDataManager.getAllStudents()
    
    // Thêm sinh viên mới
    libraryDataManager.addStudent(name, studentId, email)
}
```

---

## 👥 3. StudentManagementScreen.kt

**Vị trí:** `app/src/main/java/com/example/th1/fragments/StudentManagementScreen.kt`

### Tính năng:
1. ✅ Hiển thị danh sách sinh viên (dùng LazyColumn)
2. ✅ Thêm sinh viên mới
3. ✅ **Validation email phải có @gmail.com**

### Validation Email:

```kotlin
// Hàm kiểm tra email
fun validateEmail(email: String): Boolean {
    // Kiểm tra có đuôi @gmail.com không
    if (!email.endsWith("@gmail.com")) {
        emailError = "Email phải có đuôi @gmail.com"
        return false
    }
    
    // Kiểm tra có ít nhất 1 ký tự trước @gmail.com
    if (email.length <= "@gmail.com".length) {
        emailError = "Email không hợp lệ"
        return false
    }
    
    // Email hợp lệ
    emailError = ""
    return true
}

// Khi nhấn nút Thêm
TextButton(
    onClick = {
        if (name.isNotBlank() && studentId.isNotBlank() && email.isNotBlank()) {
            // Validate email trước khi thêm
            if (validateEmail(email)) {
                onAddStudent(name, studentId, email)
            }
        }
    }
)
```

### LazyColumn để cuộn:

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    // Header
    item {
        Row { /* Tiêu đề và nút + */ }
    }
    
    // Danh sách sinh viên
    items(libraryDataManager.getAllStudents()) { student ->
        StudentCard(student = student)
    }
}
```

---

## 📚 4. BookManagementScreen.kt

**Vị trí:** `app/src/main/java/com/example/th1/fragments/BookManagementScreen.kt`

### Tính năng:
1. ✅ Hiển thị danh sách sách (dùng LazyColumn)
2. ✅ Thêm sách mới
3. ✅ Hiển thị trạng thái: "Có sẵn" (xanh) hoặc "Đã mượn" (đỏ)

### Code hiển thị trạng thái:

```kotlin
// Hiển thị trạng thái với màu khác nhau
Text(
    text = if (book.available) "✓ Có sẵn" else "✗ Đã mượn",
    fontSize = 14.sp,
    // Màu xanh nếu có sẵn, màu đỏ nếu đã mượn
    color = if (book.available) 
        MaterialTheme.colorScheme.primary   // Xanh
    else 
        MaterialTheme.colorScheme.error,    // Đỏ
    fontWeight = FontWeight.Bold
)
```

---

## 🏠 5. BorrowManagementScreen.kt (MÀN HÌNH CHÍNH)

**Vị trí:** `app/src/main/java/com/example/th1/fragments/BorrowManagementScreen.kt`

### Tính năng:
1. ✅ Chọn sinh viên
2. ✅ Xem sách sinh viên đã mượn
3. ✅ Mượn sách mới
4. ✅ **TRẢ SÁCH** (nút đỏ)

### Flow mượn sách:

```
1. Chọn sinh viên (nhấn "Thay đổi")
   ↓
2. Nhấn nút "Thêm"
   ↓
3. Dialog hiển thị danh sách sách có sẵn
   ↓
4. Chọn sách (checkbox)
   ↓
5. Nhấn "Mượn sách"
   ↓
6. Sách được thêm vào danh sách đã mượn
```

### Flow trả sách:

```
1. Chọn sinh viên
   ↓
2. Xem danh sách sách đã mượn
   ↓
3. Nhấn nút "Trả sách" (đỏ) trên sách muốn trả
   ↓
4. Sách biến mất khỏi danh sách
   ↓
5. Sách quay lại trạng thái "Có sẵn"
```

### Code trả sách:

```kotlin
BorrowedBookItem(
    book = book,
    onReturnBook = {
        // Gọi hàm trả sách
        libraryDataManager.returnBookByStudentAndBook(
            selectedStudent!!.id, 
            book.id
        )
        // Tăng refreshTrigger để UI cập nhật
        refreshTrigger++
    }
)

// Nút Trả sách màu đỏ
Button(
    onClick = onReturnBook,
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error  // Màu đỏ
    )
) {
    Text("Trả sách")
}
```

---

## 🎨 6. Theme và Màu sắc

**Vị trí:** 
- `app/src/main/java/com/example/th1/ui/theme/Color.kt`
- `app/src/main/java/com/example/th1/ui/theme/Theme.kt`

### Màu sắc:

```kotlin
// Color.kt
val Blue700 = Color(0xFF1976D2)  // Xanh đậm cho nút
val Blue500 = Color(0xFF2196F3)  // Xanh nhạt
val White = Color(0xFFFFFFFF)    // Trắng cho nền

// Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = Blue700,           // Màu chính (nút, TopBar)
    background = White,          // Nền trắng
    surface = White,             // Card trắng
    onPrimary = White,          // Chữ trên nút (trắng)
)
```

---

## 🔄 LUỒNG DỮ LIỆU

```
UI (Screen)
    ↓ gọi hàm
LibraryDataManager
    ↓ cập nhật
mutableStateListOf
    ↓ tự động
UI tự động refresh
```

### Ví dụ cụ thể:

```kotlin
// 1. User nhấn nút "Thêm sinh viên"
onAddStudent(name, studentId, email)

// 2. Gọi hàm trong LibraryDataManager
libraryDataManager.addStudent(name, studentId, email)

// 3. Thêm vào mutableStateListOf
students.add(Student(newId, name, studentId, email))

// 4. UI tự động hiển thị sinh viên mới (không cần làm gì thêm!)
```

---

## 📝 TIPS QUAN TRỌNG

### 1. Singleton Pattern
```kotlin
// ❌ SAI - Tạo instance mới mỗi lần
val manager = remember { LibraryDataManager() }

// ✅ ĐÚNG - Dùng singleton
val manager = LibraryDataManager
```

### 2. LazyColumn vs Column
```kotlin
// LazyColumn: Cho danh sách dài, cuộn mượt
LazyColumn {
    items(students) { student ->
        StudentCard(student)
    }
}

// Column: Cho danh sách ngắn
Column {
    students.forEach { student ->
        StudentCard(student)
    }
}
```

### 3. State Management
```kotlin
// remember: Lưu state trong Composable
var showDialog by remember { mutableStateOf(false) }

// mutableStateListOf: Tự động update UI khi thay đổi list
private val students = mutableStateListOf<Student>()
```

### 4. Navigation đơn giản
```kotlin
// Không cần NavController phức tạp
// Chỉ cần when + index
when (selectedTab) {
    0 -> Screen1()
    1 -> Screen2()
    2 -> Screen3()
}
```

---

## 🐛 GIẢI QUYẾT VẤN ĐỀ THƯỜNG GẶP

### 1. UI không cập nhật sau khi thêm dữ liệu
**Nguyên nhân:** Dùng `mutableListOf` thay vì `mutableStateListOf`

**Giải pháp:**
```kotlin
// ❌ SAI
private val students = mutableListOf<Student>()

// ✅ ĐÚNG
private val students = mutableStateListOf<Student>()
```

### 2. Dữ liệu bị mất khi chuyển tab
**Nguyên nhân:** Mỗi màn hình tạo instance riêng

**Giải pháp:** Dùng Singleton
```kotlin
// ❌ SAI
class LibraryDataManager { ... }

// ✅ ĐÚNG
object LibraryDataManager { ... }
```

### 3. Email validation không hoạt động
**Kiểm tra:**
```kotlin
// Đảm bảo gọi validateEmail trước khi thêm
if (validateEmail(email)) {
    onAddStudent(name, studentId, email)
}
```

---

## 🎯 CHECKLIST HOÀN THÀNH

- [x] ✅ Màu nền trắng, nút xanh dương
- [x] ✅ Thêm sinh viên (với validation email @gmail.com)
- [x] ✅ Thêm sách
- [x] ✅ Mượn sách
- [x] ✅ **TRẢ SÁCH** (nút đỏ)
- [x] ✅ LazyColumn ở cả 3 màn hình
- [x] ✅ Navigation đơn giản với tabs
- [x] ✅ Comments chi tiết trong code
- [x] ✅ Code dễ hiểu như sinh viên viết

---

## 📞 TÓM TẮT

1. **MainActivity.kt**: Điểm khởi đầu, quản lý navigation
2. **LibraryDataManager.kt**: Singleton quản lý tất cả dữ liệu
3. **StudentManagementScreen.kt**: Quản lý sinh viên + validation email
4. **BookManagementScreen.kt**: Quản lý sách
5. **BorrowManagementScreen.kt**: Mượn/trả sách
6. **LazyColumn**: Dùng ở cả 3 màn hình để cuộn danh sách
7. **Navigation**: Đơn giản với `when` + tabs

Tất cả code đã có comments chi tiết bằng tiếng Việt! 🎉
