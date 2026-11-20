# PT-CONNECT — Nền Tảng Quản Lý & Kết Nối Huấn Luyện Viên Cá Nhân (PT)

Hệ thống web thương mại dịch vụ thể hình kết nối giữa **Học viên (Student)** và **Huấn luyện viên cá nhân (Personal Trainer - PT)**, được xây dựng theo chuẩn kiến trúc **JSP Model 2 (MVC)** với Java Servlet 6 (Jakarta EE 10), JSTL 3, HikariCP, MySQL 8 và máy chủ nhúng Jetty 12.

---

## 🛠️ Yêu cầu môi trường (Prerequisites)

Trước khi chạy, máy tính của bạn chỉ cần:
1. **[Git](https://git-scm.com/)**
2. **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (phải đang bật và chạy)

> 💡 **Lưu ý:** Cả máy chủ web (Java 17/Jetty) lẫn Cơ sở dữ liệu (MySQL 8) đều đã được đóng gói hoàn toàn trong Docker. Người dùng **không bắt buộc phải cài đặt Java hay Maven trên máy**.

---

## 🚀 Hướng Dẫn Khởi Chạy Nhanh (1 Câu Lệnh)

### Bước 1: Clone mã nguồn về máy
```bash
git clone <URL_REPOSITORY_CUA_BAN>
cd "PT connect cnpm"
```

### Bước 2: Khởi động toàn bộ hệ thống (Web + Database)
Chạy đúng một câu lệnh sau trong Terminal / PowerShell:
```bash
docker compose up -d --build
```
*(Lệnh này sẽ tự động tải MySQL 8.0, nạp toàn bộ CSDL mẫu từ `database/init.sql`, biên dịch mã nguồn Java và khởi động web server).*

### Bước 3: Mở trình duyệt và trải nghiệm
Truy cập ngay đường dẫn:
👉 **[http://localhost:8089/pt-connect](http://localhost:8089/pt-connect)**

---

### 💡 (Tùy chọn) Chạy Web ở máy ngoài (Local) kết nối DB Docker:
Nếu bạn muốn phát triển/chỉnh sửa code trực tiếp trên máy mà không chạy web trong Docker:
1. Chỉ bật Database trong Docker: `docker compose up -d mysql`
2. Chạy web bằng Maven Wrapper:
   * Trên Windows: `.\mvnw jetty-ee10:run`
   * Trên Linux/macOS: `./mvnw jetty-ee10:run`

---

## 🔑 Tài Khoản Kiểm Thử Hệ Thống (Demo Credentials)

> 🔐 **Mật khẩu dùng chung cho tất cả các tài khoản mẫu:** **`Password123@`**

| Vai trò | Email đăng nhập | Tên người dùng | Phạm vi chức năng kiểm thử |
| :--- | :--- | :--- | :--- |
| 🛡️ **Quản trị viên (Admin)** | `admin@ptconnect.vn` | Admin System | Duyệt hồ sơ PT, quản lý người dùng, xem thống kê & doanh thu |
| 🏋️ **Huấn luyện viên (PT)** | `pt.hoangc@ptconnect.vn` | Lê Hoàng Cường | PT đã xác thực: mở khung giờ rảnh, duyệt ca tập, xem lịch dạy |
| 🎓 **Học viên (Student)** | `student.a@ptconnect.vn` | Nguyễn Văn A | Tìm kiếm PT, nạp tiền ví, đặt lịch ca tập, mua khóa học |

*(Bạn cũng có thể bấm vào **"Đăng ký ngay"** trên giao diện để tạo mới tài khoản Học viên hoặc PT).*

---

## 📂 Cấu Trúc Dự Án

```
PT connect cnpm/
├── database/
│   └── init.sql              # Script DDL 14 bảng, trigger cập nhật rating & dữ liệu mẫu
├── src/
│   └── main/
│       ├── java/com/ptconnect/
│       │   ├── controller/   # Các Servlet điều hướng (Auth, Student, PT, Admin)
│       │   ├── model/
│       │   │   ├── dao/      # Tầng DAO truy xuất CSDL qua HikariCP Connection Pool
│       │   │   └── entity/   # 17 Entity POJO (id kiểu Long)
│       │   ├── filter/       # AuthenticationFilter & CharacterEncodingFilter
│       │   └── util/         # Tiện ích mã hóa BCrypt, Upload file, v.v.
│       ├── resources/
│       │   └── db.properties # Cấu hình kết nối JDBC (port 3307, user root)
│       └── webapp/
│           ├── static/       # CSS, hình ảnh giao diện
│           ├── uploads/      # Thư mục lưu trữ ảnh tải lên (avatar)
│           └── WEB-INF/views # Giao diện JSP (JSTL/EL chuẩn MVC Model 2)
├── Dockerfile                # Đóng gói ứng dụng web Java/Jetty vào Docker image
├── docker-compose.yml        # Điều phối 2 container: MySQL (DB) & Web (Jetty)
├── pom.xml                   # Cấu hình thư viện Maven & Jetty 12 EE10 Plugin
└── README.md                 # Hướng dẫn triển khai dự án
```

---

## 🛑 Dừng Hệ Thống & Dọn Dẹp

* Để dừng web server: Nhấn `Ctrl + C` tại cửa sổ dòng lệnh đang chạy.
* Để dừng database Docker:
  ```bash
  docker compose down
  ```
* Để xóa sạch cả dữ liệu database trong volume (khi muốn nạp lại DB ban đầu từ file `init.sql`):
  ```bash
  docker compose down -v
  ```
