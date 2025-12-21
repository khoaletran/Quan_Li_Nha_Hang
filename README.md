# 🍽️ HỆ THỐNG QUẢN LÝ NHÀ HÀNG (CrabKing Restaurant)

**Dự án môn học:** Phát triển ứng dụng (Java)  
**Trường:** Đại học Công nghiệp TP. Hồ Chí Minh (IUH)

---

## 👥 Nhóm thực hiện

Sinh viên lớp **PTUD**:

- **Trần Lê Khoa** – Trưởng nhóm  
- Trần Quốc Nhã  
- Đỗ Minh Quân  
- Nguyễn Hà Nhật Khanh  

---

## 📝 Giới thiệu

**Hệ thống Quản Lý Nhà Hàng** là phần mềm được xây dựng trên nền tảng **Java Swing / JavaFX**, nhằm hỗ trợ và tự động hóa các hoạt động quản lý trong nhà hàng, bao gồm:

- Đặt bàn  
- Gọi món  
- Thanh toán  
- Quản lý khách hàng, nhân viên  
- Thống kê và báo cáo doanh thu  

Dự án hướng đến các mục tiêu chính:

- Đảm bảo **xử lý nghiệp vụ chính xác**  
- Giao diện **trực quan, dễ sử dụng**  
- **Kết nối cơ sở dữ liệu ổn định và nhất quán**

---

## ✨ Tính năng chính

### 🔹 Quản lý Bàn
- Theo dõi trạng thái bàn: **Trống – Đang sử dụng – Đã đặt**

### 🔹 Quản lý Thực đơn
- Thêm, sửa, xóa món ăn  
- Quản lý đơn giá và danh mục món

### 🔹 Quản lý Hóa đơn
- Tính tiền tự động  
- Hỗ trợ thanh toán nhanh

### 🔹 Quản lý Khách hàng & Nhân viên
- Lưu trữ thông tin  
- Phân quyền và đăng nhập hệ thống

### 🔹 Thống kê – Báo cáo
- Thống kê doanh thu theo **ngày / tháng**

---

## 🛠 Công nghệ sử dụng

- **Ngôn ngữ:** Java 17+  
- **UI Framework:** JavaFX, FXML  
- **Build Tool:** Maven  
- **Cơ sở dữ liệu:** Microsoft SQL Server  
- **Thư viện & Công nghệ hỗ trợ:**  
  - JDBC  
  - SQL Server JDBC Driver  
  - Lombok *(nếu có)*  
  - FontAwesome *(nếu có)*  

---

## 🚀 Hướng dẫn cài đặt & khởi chạy

### 1️⃣ Chuẩn bị môi trường

- Cài đặt **JDK 17 hoặc JDK 21**
- Cài đặt **Microsoft SQL Server 2019 trở lên**
- IDE khuyến nghị:
  - IntelliJ IDEA  
  - Eclipse  

---
### Cách 1: Cài và Sử dụng nhanh

### 2️⃣ Thiết lập cơ sở dữ liệu

1. Mở **SQL Server Management Studio (SSMS)**  
2. Mở file script:

```text
Quan_Li_Nha_Hang/src/data/DataBase.sql
```

3. Nhấn **F5** để thực thi  
→ Hệ thống sẽ tự động tạo database **QuanLyNhaHang** và dữ liệu mẫu
→ Khi database đã được tạo thành công

### 3️⃣ Cài và chạy ứng dụng
Tài khoản demo ADMIN: AD0000 | ADPASS: admin
1.tải "CrabKing Restaurant.exe"
2.Phần mềm sẽ được tải về mở App 'CrabKing Restaurant' để mở ứng dụng

---

### Cách 2: Tùy chỉnh và Cài đặt

### 2️⃣ Thiết lập cơ sở dữ liệu

1. Mở **SQL Server Management Studio (SSMS)**  
2. Mở file script:

```text
Quan_Li_Nha_Hang/src/data/DataBase.sql
```

3. Nhấn **F5** để thực thi  
→ Hệ thống sẽ tự động tạo database **QuanLyNhaHang** và dữ liệu mẫu

### 3️⃣ Cấu hình kết nối CSDL

Mở file `ConnectDB.java` (hoặc file cấu hình tương đương) và chỉnh sửa:

```java
String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhaHang;encrypt=true;trustServerCertificate=true";
String user = "sa";
String password = "YOUR_PASSWORD_HERE"; // Thay bằng mật khẩu SQL Server
```


### 4️⃣ Build và chạy ứng dụng
Tài khoản demo ADMIN: AD0000 | ADPASS: admin
```bash
mvn clean install
mvn javafx:run
```

---


## 📂 Cấu trúc thư mục (Sơ lược)

```text
src/
┣ data/
┃ ┣ Ban.sql
┃ ┣ DataBase.sql
┃ ┣ DuLieu.sql
┃ ┣ LOOP_HD.sql
┃ ┗ MON_DATA.sql
┗ main/
  ┣ java/
┃ ┃ ┣ connectDB/
┃ ┃ ┃ ┗ connectDB.java
┃ ┃ ┣ dao/
┃ ┃ ┃ ┣ BanDAO.java
┃ ┃ ┃ ┣ ChiTietHDDAO.java
┃ ┃ ┃ ┣ CocDAO.java
┃ ┃ ┃ ┣ HangKhachDAO.java
┃ ┃ ┃ ┣ HoaDonDAO.java
┃ ┃ ┃ ┣ KhachHangDAO.java
┃ ┃ ┃ ┣ KhuVucDAO.java
┃ ┃ ┃ ┣ KhuyenMaiDAO.java
┃ ┃ ┃ ┣ LoaiBanDAO.java
┃ ┃ ┃ ┣ LoaiMonDAO.java
┃ ┃ ┃ ┣ MonDAO.java
┃ ┃ ┃ ┣ NhanVienDAO.java
┃ ┃ ┃ ┣ PhanTramGiaBanDAO.java
┃ ┃ ┃ ┣ PhieuKetCaDAO.java
┃ ┃ ┃ ┣ SuKienDAO.java
┃ ┃ ┃ ┗ ThoiGianDoiBanDAO.java
┃ ┃ ┣ entity/
┃ ┃ ┃ ┣ Ban.java
┃ ┃ ┃ ┣ ChinhSach.java
┃ ┃ ┃ ┣ ChiTietHoaDon.java
┃ ┃ ┃ ┣ Coc.java
┃ ┃ ┃ ┣ HangKhachHang.java
┃ ┃ ┃ ┣ HoaDon.java
┃ ┃ ┃ ┣ KhachHang.java
┃ ┃ ┃ ┣ KhuVuc.java
┃ ┃ ┃ ┣ KhuyenMai.java
┃ ┃ ┃ ┣ LoaiBan.java
┃ ┃ ┃ ┣ LoaiMon.java
┃ ┃ ┃ ┣ Mon.java
┃ ┃ ┃ ┣ NhanVien.java
┃ ┃ ┃ ┣ PhanTramGiaBan.java
┃ ┃ ┃ ┣ PhieuKetCa.java
┃ ┃ ┃ ┣ SuKien.java
┃ ┃ ┃ ┗ ThoiGianDoiBan.java
┃ ┃ ┣ ui/
┃ ┃ ┃ ┣ controllers/
┃ ┃ ┃ ┃ ┣ AlertController.java
┃ ┃ ┃ ┃ ┣ BanGiaoCaController.java
┃ ┃ ┃ ┃ ┣ CheckinController.java
┃ ┃ ┃ ┃ ┣ CheckoutController.java
┃ ┃ ┃ ┃ ┣ ChinhSachController.java
┃ ┃ ┃ ┃ ┣ ChonMonController.java
┃ ┃ ┃ ┃ ┣ ConfirmController.java
┃ ┃ ┃ ┃ ┣ DangXuatController.java
┃ ┃ ┃ ┃ ┣ DashboardController.java
┃ ┃ ┃ ┃ ┣ DatBanController.java
┃ ┃ ┃ ┃ ┣ HoaDonInController.java
┃ ┃ ┃ ┃ ┣ HoTroControllerNV.java
┃ ┃ ┃ ┃ ┣ HoTroControllerQL.java
┃ ┃ ┃ ┃ ┣ KhuyenMaiController.java
┃ ┃ ┃ ┃ ┣ LoginController.java
┃ ┃ ┃ ┃ ┣ MainController_NV.java
┃ ┃ ┃ ┃ ┣ MainController_QL.java
┃ ┃ ┃ ┃ ┣ QLBanController.java
┃ ┃ ┃ ┃ ┣ QLDatBanController.java
┃ ┃ ┃ ┃ ┣ QLMenuController.java
┃ ┃ ┃ ┃ ┣ QLNhanVienController.java
┃ ┃ ┃ ┃ ┣ QLThanhVienController.java
┃ ┃ ┃ ┃ ┣ QrCodeController.java
┃ ┃ ┃ ┃ ┣ QRThanhToanController.java
┃ ┃ ┃ ┃ ┣ SidebarController_NV.java
┃ ┃ ┃ ┃ ┣ SidebarController_QL.java
┃ ┃ ┃ ┃ ┣ SplashController.java
┃ ┃ ┃ ┃ ┣ ThongKeController.java
┃ ┃ ┃ ┃ ┣ TopBarController.java
┃ ┃ ┃ ┃ ┣ TraCuuHoaDonController.java
┃ ┃ ┃ ┃ ┣ TraCuuKetCaController.java
┃ ┃ ┃ ┃ ┗ XacNhanXoaController.java
┃ ┃ ┃ ┣ AlertCus.java
┃ ┃ ┃ ┣ AppConstants.java
┃ ┃ ┃ ┣ ConfirmCus.java
┃ ┃ ┃ ┣ DangXuat.java
┃ ┃ ┃ ┣ HoaDonIn.java
┃ ┃ ┃ ┣ Login.java
┃ ┃ ┃ ┣ MainNV.java
┃ ┃ ┃ ┣ MainQL.java
┃ ┃ ┃ ┣ QRThanhToan.java
┃ ┃ ┃ ┗ XacNhanXoa.java
┃ ┃ ┗ module-info.java
  ┗ resources/
┃   ┣ CSS/
┃ ┃ ┃ ┣ AppMD.css
┃ ┃ ┃ ┣ bangiaoca.css
┃ ┃ ┃ ┣ checkin.css
┃ ┃ ┃ ┣ checkout.css
┃ ┃ ┃ ┣ chinhsach.css
┃ ┃ ┃ ┣ chonmon.css
┃ ┃ ┃ ┣ dashboard.css
┃ ┃ ┃ ┣ datban.css
┃ ┃ ┃ ┣ filter.css
┃ ┃ ┃ ┣ hotronv.css
┃ ┃ ┃ ┣ hotroql.css
┃ ┃ ┃ ┣ khuyenmai.css
┃ ┃ ┃ ┣ login.css
┃ ┃ ┃ ┣ qlban.css
┃ ┃ ┃ ┣ qldatban.css
┃ ┃ ┃ ┣ qlmenu.css
┃ ┃ ┃ ┣ qlnhanvien.css
┃ ┃ ┃ ┣ qlthanhvien.css
┃ ┃ ┃ ┣ sidebar.css
┃ ┃ ┃ ┣ splash.css
┃ ┃ ┃ ┣ thongke.css
┃ ┃ ┃ ┣ topbar.css
┃ ┃ ┃ ┣ tracuuhoadon.css
┃ ┃ ┃ ┗ tracuuketca.css
┃   ┣ FXML/
┃ ┃ ┃ ┣ Alert.fxml
┃ ┃ ┃ ┣ BanGiaoCa.fxml
┃ ┃ ┃ ┣ CheckIn.fxml
┃ ┃ ┃ ┣ CheckOut.fxml
┃ ┃ ┃ ┣ ChinhSach.fxml
┃ ┃ ┃ ┣ ChonMon.fxml
┃ ┃ ┃ ┣ Confirm.fxml
┃ ┃ ┃ ┣ DangXuat.fxml
┃ ┃ ┃ ┣ DashBoard.fxml
┃ ┃ ┃ ┣ DatBan.fxml
┃ ┃ ┃ ┣ HoaDonIn.fxml
┃ ┃ ┃ ┣ HoTroNV.fxml
┃ ┃ ┃ ┣ HoTroQL.fxml
┃ ┃ ┃ ┣ KhuyenMai.fxml
┃ ┃ ┃ ┣ Login.fxml
┃ ┃ ┃ ┣ MainNhanVien.fxml
┃ ┃ ┃ ┣ MainQuanLi.fxml
┃ ┃ ┃ ┣ QLBan.fxml
┃ ┃ ┃ ┣ QLDatBan.fxml
┃ ┃ ┃ ┣ QLMenu.fxml
┃ ┃ ┃ ┣ QLNhanVien.fxml
┃ ┃ ┃ ┣ QLThanhVien.fxml
┃ ┃ ┃ ┣ QRThanhToan.fxml
┃ ┃ ┃ ┣ sidebar_NV.fxml
┃ ┃ ┃ ┣ sidebar_QL.fxml
┃ ┃ ┃ ┣ SplashScreen.fxml
┃ ┃ ┃ ┣ ThongKe.fxml
┃ ┃ ┃ ┣ TopBar.fxml
┃ ┃ ┃ ┣ TraCuuHoaDon.fxml
┃ ┃ ┃ ┣ TraCuuKetCa.fxml
┃ ┃ ┃ ┗ XacNhanXoa.fxml
```

---

## 📌 Ghi chú

- Dự án phục vụ mục đích **học tập**
- Không sử dụng cho mục đích thương mại



