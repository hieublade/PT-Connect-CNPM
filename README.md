# PT-CONNECT — Nền Tảng Quản Lý & Kết Nối Huấn Luyện Viên Cá Nhân (PT)

Hệ thống web thương mại dịch vụ thể hình kết nối giữa **Học viên (Student)** và **Huấn luyện viên cá nhân (Personal Trainer - PT)**, được xây dựng theo chuẩn kiến trúc **JSP Model 2 (MVC)** với Java Servlet 6 (Jakarta EE 10), JSTL 3, HikariCP, MySQL 8 và máy chủ nhúng Jetty 12.

---

##  Yêu cầu môi trường (Prerequisites)

Trước khi chạy, máy tính của bạn chỉ cần:
1. **[Git](https://git-scm.com/)**
2. **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (phải đang bật và chạy)

>  **Lưu ý:** Cả máy chủ web (Java 17/Jetty) lẫn Cơ sở dữ liệu (MySQL 8) đều đã được đóng gói hoàn toàn trong Docker. Người dùng **không bắt buộc phải cài đặt Java hay Maven trên máy**.

---

##  Hướng Dẫn Khởi Chạy Nhanh (1 Câu Lệnh)

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
 **[http://localhost:8089/pt-connect](http://localhost:8089/pt-connect)**

---

###  (Tùy chọn) Chạy Web ở máy ngoài (Local) kết nối DB Docker:
Nếu bạn muốn phát triển/chỉnh sửa code trực tiếp trên máy mà không chạy web trong Docker:
1. Chỉ bật Database trong Docker: `docker compose up -d mysql`
2. Chạy web bằng Maven Wrapper:
   * Trên Windows: `.\mvnw jetty-ee10:run`
   * Trên Linux/macOS: `./mvnw jetty-ee10:run`

---

##  Tài Khoản Kiểm Thử Hệ Thống (Demo Credentials)

>  **Mật khẩu dùng chung cho tất cả các tài khoản mẫu:** **`Password123@`**

| Vai trò | Email đăng nhập | Tên người dùng | Phạm vi chức năng kiểm thử |
| :--- | :--- | :--- | :--- |
|  **Quản trị viên (Admin)** | `admin@ptconnect.vn` | Admin System | Duyệt hồ sơ PT, quản lý người dùng, xem thống kê & doanh thu |
|  **Huấn luyện viên (PT)** | `pt.hoangc@ptconnect.vn` | Lê Hoàng Cường | PT đã xác thực: mở khung giờ rảnh, duyệt ca tập, xem lịch dạy |
|  **Học viên (Student)** | `student.a@ptconnect.vn` | Nguyễn Văn A | Tìm kiếm PT, nạp tiền ví, đặt lịch ca tập, mua khóa học |

*(Bạn cũng có thể bấm vào **"Đăng ký ngay"** trên giao diện để tạo mới tài khoản Học viên hoặc PT).*

---

##  Kiểm Thử Đơn Vị (JUnit 5 Unit Test Suite Cho Tầng Model)

Dự án áp dụng mô hình kiểm thử đơn vị tự động theo chuẩn kiến trúc **JSP Model 2 (MVC)** với **JUnit 5**, tập trung kiểm thử toàn diện các lớp xử lý logic nghiệp vụ và truy xuất cơ sở dữ liệu (**các lớp DAO trong tầng Model**).

###  Đặc điểm kiểm thử:
* **Tự động Rollback CSDL:** Mọi thao tác thêm/sửa/xóa (`INSERT`, `UPDATE`, `DELETE`) trong lúc test đều được thực thi trong Transaction và tự động `rollback()` khi kết thúc test case, **không làm sai lệch hoặc sinh rác trong CSDL**.
* **Độ bao phủ (Coverage):** Bao gồm cả **Kịch bản chuẩn (Positive test)** và **Kịch bản ngoại lệ / Biên (Negative / Boundary test)**.

###  Danh sách các Test Suites trong Tầng Model:
| Lớp Kiểm Thử (Test Class) | Đối tượng kiểm thử (Model / DAO) | Số lượng Test Cases |
| :--- | :--- | :--- |
| `UserDAOTest` | `UserDAO` (Đăng nhập, đăng ký, xác thực, phân quyền) | 7 TCs |
| `PTProfileDAOTest` | `PTProfileDAO` (Hồ sơ PT, duyệt tài khoản, rating) | 6 TCs |
| `CourseDAOTest` | `CourseDAO` (Gói tập, quản lý khóa học) | 4 TCs |
| `AvailabilitySlotDAOTest` | `AvailabilitySlotDAO` (Mở ca rảnh, khóa ca đã đặt) | 4 TCs |
| `BookingDAOTest` | `BookingDAO` (Đặt lịch tập, cập nhật trạng thái) | 4 TCs |
| `EnrollmentDAOTest` | `EnrollmentDAO` (Hợp đồng khóa học, trừ số buổi tập) | 3 TCs |
| `WalletDAOTest` | `WalletDAO` (Ví tiền, nạp/trừ số dư, đóng băng cọc) | 3 TCs |
| `PaymentDAOTest` | `PaymentDAO` (Giao dịch nạp tiền, thanh toán gói) | 3 TCs |
| `AttendanceDAOTest` | `AttendanceDAO` (Điểm danh ca tập qua QR/OTP) | 2 TCs |
| `ReviewDAOTest` | `ReviewDAO` (Đánh giá chất lượng ca tập) | 3 TCs |
| `CancellationDAOTest` | `CancellationDAO` (Xử lý hủy ca, bồi thường cọc) | 2 TCs |
| `PTLocationDAOTest` | `PTLocationDAO` (Địa điểm nhận dạy của PT) | 3 TCs |
| `WorkoutPlanDAOTest` | `WorkoutPlanDAO` (Giáo án huấn luyện thể hình) | 2 TCs |
| `WorkoutDAOTest` | `WorkoutDAO` (Chi tiết các bài tập thể hình) | 1 TC |

###  Lệnh chạy kiểm thử:
Đảm bảo container MySQL đang bật, sau đó chạy:
```bash
# Windows
.\mvnw test

# Linux / macOS
./mvnw test
```

---

##  Cấu Trúc Dự Án

```
PT connect cnpm/
├── database/
│   └── init.sql              # Script DDL 14 bảng, trigger cập nhật rating & dữ liệu mẫu
├── src/
│   ├── main/
│   │   ├── java/com/ptconnect/
│   │   │   ├── controller/   # Các Servlet điều hướng (Auth, Student, PT, Admin)
│   │   │   ├── model/
│   │   │   │   ├── dao/      # Tầng DAO truy xuất CSDL qua HikariCP Connection Pool
│   │   │   │   └── entity/   # 17 Entity POJO (id kiểu Long)
│   │   │   ├── filter/       # AuthenticationFilter & CharacterEncodingFilter
│   │   │   └── util/         # Tiện ích mã hóa BCrypt, Upload file, v.v.
│   │   ├── resources/
│   │   │   └── db.properties # Cấu hình kết nối JDBC (port 3307, user root)
│   │   └── webapp/
│   │       ├── static/       # CSS, hình ảnh giao diện
│   │       ├── uploads/      # Thư mục lưu trữ ảnh tải lên (avatar)
│   │       └── WEB-INF/views # Giao diện JSP (JSTL/EL chuẩn MVC Model 2)
│   └── test/
│       └── java/com/ptconnect/
│           └── model/dao/    # 14 Test Suites kiểm thử tự động tầng DAO (JUnit 5)
├── Dockerfile                # Đóng gói ứng dụng web Java/Jetty vào Docker image
├── docker-compose.yml        # Điều phối 2 container: MySQL (DB) & Web (Jetty)
├── pom.xml                   # Cấu hình thư viện Maven & Jetty 12 EE10 Plugin
└── README.md                 # Hướng dẫn triển khai dự án
```

---

##  Dừng Hệ Thống & Dọn Dẹp

* Để dừng web server: Nhấn `Ctrl + C` tại cửa sổ dòng lệnh đang chạy.
* Để dừng database Docker:
  ```bash
  docker compose down
  ```
* Để xóa sạch cả dữ liệu database trong volume (khi muốn nạp lại DB ban đầu từ file `init.sql`):
  ```bash
  docker compose down -v
  ```
