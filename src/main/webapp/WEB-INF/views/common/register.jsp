<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký PT-Connect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<div class="container" style="max-width: 480px; margin-top: 40px;">
    <div class="card">
        <h2>Đăng ký tài khoản mới</h2>
        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-row"><label>Họ và tên</label><input type="text" name="fullName" required></div>
            <div class="form-row"><label>Email</label><input type="email" name="email" required></div>
            <div class="form-row"><label>Số điện thoại</label><input type="text" name="phone" required></div>
            <div class="form-row"><label>Mật khẩu</label><input type="password" name="password" required></div>
            <div class="form-row">
                <label>Vai trò</label>
                <select name="role">
                    <option value="STUDENT">Học viên (STUDENT)</option>
                    <option value="PT">Huấn luyện viên (PT)</option>
                </select>
            </div>
            <button type="submit" class="btn" style="width: 100%;">Đăng ký</button>
        </form>
        <p style="margin-top: 12px; font-size: 14px;">
            Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </p>
    </div>
</div>
</body>
</html>
