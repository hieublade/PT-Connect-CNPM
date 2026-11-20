package com.ptconnect.model.dao;

import com.ptconnect.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest extends BaseDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void initDAO() {
        userDAO = new UserDAO(testConn);
    }

    @Test
    @DisplayName("TC01 - Đăng nhập thành công với tài khoản Admin hợp lệ")
    public void testLoginSuccess() {
        User user = userDAO.login("admin@ptconnect.vn", "Password123@");
        assertNotNull(user, "User đăng nhập thành công không được null");
        assertEquals("ADMIN", user.getRole());
        assertEquals("Admin System", user.getFullName());
    }

    @Test
    @DisplayName("TC02 - Đăng nhập thất bại khi sai mật khẩu")
    public void testLoginWrongPassword() {
        User user = userDAO.login("admin@ptconnect.vn", "WrongPassword123@");
        assertNull(user, "Sai mật khẩu phải trả về null");
    }

    @Test
    @DisplayName("TC03 - Đăng nhập thất bại với email không tồn tại")
    public void testLoginEmailNotFound() {
        User user = userDAO.login("notfound_user@ptconnect.vn", "Password123@");
        assertNull(user, "Email không tồn tại phải trả về null");
    }

    @Test
    @DisplayName("TC04 - Lấy thông tin User theo ID hợp lệ (User ID = 1)")
    public void testGetProfileSuccess() {
        User user = userDAO.getProfile(1L);
        assertNotNull(user);
        assertEquals("student.a@ptconnect.vn", user.getEmail());
    }

    @Test
    @DisplayName("TC05 - Lấy thông tin User theo ID không tồn tại (-1)")
    public void testGetProfileNotFound() {
        User user = userDAO.getProfile(-1L);
        assertNull(user, "ID không tồn tại phải trả về null");
    }

    @Test
    @DisplayName("TC06 - Lấy danh sách người dùng theo vai trò PT")
    public void testGetUserByRole() {
        ArrayList<User> pts = userDAO.getUserByRole("PT");
        assertNotNull(pts);
        assertFalse(pts.isEmpty(), "Danh sách PT mẫu phải có ít nhất 1 người");
    }

    @Test
    @DisplayName("TC07 - Đăng ký người dùng mới thành công (Rollback sau test)")
    public void testRegisterNewUser() {
        User newUser = new User();
        newUser.setFullName("Nguyen Test JUnit");
        newUser.setEmail("test_junit_" + System.currentTimeMillis() + "@ptconnect.vn");
        newUser.setPasswordHash("$2a$10$IciBJIW1NX.1Za3YIyYqjO7H/SpL//eFExQrDxRk/33YLKZjOvyey");
        newUser.setPhone("0988" + (int)(Math.random() * 1000000));
        newUser.setRole("STUDENT");
        newUser.setStatus("ACTIVE");

        Long newId = userDAO.register(newUser);
        assertNotNull(newId, "Đăng ký thành công phải sinh ID");
        assertTrue(newId > 0);
    }
}
