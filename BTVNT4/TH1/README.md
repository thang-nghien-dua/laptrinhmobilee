# Hệ thống Quản lý Thư viện

Ứng dụng Android được phát triển bằng Kotlin và Jetpack Compose để quản lý thư viện với các tính năng:

## Tính năng chính

### 1. Quản lý Sinh viên
- Xem danh sách sinh viên
- Thêm sinh viên mới
- Thông tin sinh viên: Họ tên, MSSV, Email

### 2. Quản lý Sách
- Xem danh sách sách
- Thêm sách mới
- Thông tin sách: Tên sách, Tác giả, ISBN, Trạng thái có sẵn

### 3. Quản lý Mượn sách
- Chọn sinh viên để xem sách đã mượn
- Hiển thị sách đã mượn với dấu tick đỏ
- Cho phép sinh viên mượn sách mới
- Hiển thị thông báo khi chưa mượn sách nào

## Cấu trúc ứng dụng

```
app/src/main/java/com/example/th1/
├── MainActivity.kt                 # Activity chính với navigation tabs
├── model/                          # Data models
│   ├── Student.kt                  # Model sinh viên
│   ├── Book.kt                     # Model sách
│   └── BorrowRecord.kt             # Model bản ghi mượn sách
├── data/
│   └── LibraryDataManager.kt       # Quản lý dữ liệu và logic nghiệp vụ
└── fragments/                      # UI Components
    ├── BorrowManagementScreen.kt   # Màn hình quản lý mượn sách
    ├── BookManagementScreen.kt     # Màn hình quản lý sách
    ├── StudentManagementScreen.kt   # Màn hình quản lý sinh viên
    └── BookSelectionDialog.kt       # Dialog chọn sách để mượn
```

## Cách sử dụng

### Tab "Quản lý" (Mặc định)
- Chọn sinh viên bằng nút "Thay đổi"
- Xem sách đã mượn của sinh viên được chọn
- Nhấn "Thêm" để mượn sách mới

### Tab "DS Sách"
- Xem danh sách tất cả sách
- Thêm sách mới bằng nút "+"
- Xem trạng thái sách (Có sẵn/Đã mượn)

### Tab "Sinh viên"
- Xem danh sách tất cả sinh viên
- Thêm sinh viên mới bằng nút "+"
- Xem thông tin chi tiết sinh viên

## Dữ liệu mẫu

Ứng dụng đã có sẵn dữ liệu mẫu:

**Sinh viên:**
- Nguyen Van A (SV001)
- Nguyen Thi B (SV002) 
- Nguyen Van C (SV003)

**Sách:**
- Sách 01 (Tác giả A)
- Sách 02 (Tác giả B)
- Lập trình Android (Google)
- Kotlin Programming (JetBrains)

**Tình trạng mượn sách:**
- Nguyen Van A đã mượn: Sách 01, Sách 02
- Nguyen Thi B đã mượn: Lập trình Android
- Nguyen Van C chưa mượn sách nào

## Yêu cầu hệ thống

- Android API 24+ (Android 7.0)
- Kotlin 2.0.21
- Jetpack Compose
- Material Design 3

## Cài đặt và chạy

1. Mở project trong Android Studio
2. Sync Gradle files
3. Chạy trên emulator hoặc thiết bị thật
4. Ứng dụng sẽ tự động load dữ liệu mẫu

## Tính năng nổi bật

- **UI hiện đại**: Sử dụng Material Design 3 với Jetpack Compose
- **Navigation tabs**: Dễ dàng chuyển đổi giữa các chức năng
- **Responsive design**: Tối ưu cho các kích thước màn hình khác nhau
- **Data management**: Quản lý dữ liệu trong memory với khả năng mở rộng
- **User-friendly**: Giao diện thân thiện, dễ sử dụng

Ứng dụng đã hoàn thành đầy đủ các yêu cầu của bài tập:
✅ Tạo danh sách sách
✅ Tạo danh sách sinh viên  
✅ Cho phép sinh viên mượn sách và hiển thị thông tin sách
