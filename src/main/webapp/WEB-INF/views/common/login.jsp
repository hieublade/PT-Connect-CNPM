<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập PT-Connect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<div class="container" style="max-width: 460px; margin-top: 60px;">
    <div class="card">
        <h2>Đăng nhập PT-Connect</h2>
        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>
        <c:if test="${param.error == 'suspended'}">
            <div class="alert-error">Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ Quản trị viên để được hỗ trợ!</div>
        </c:if>
        <c:if test="${param.error == 'login_required'}">
            <div class="alert-error">Vui lòng đăng nhập để tiếp tục!</div>
        </c:if>
        <c:if test="${param.registered == '1'}">
            <div class="alert-success">Đăng ký thành công! Vui lòng đăng nhập.</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-row">
                <label>Email</label>
                <input type="email" name="email" required placeholder="Nhập địa chỉ email">
            </div>
            <div class="form-row">
                <label>Mật khẩu</label>
                <input type="password" name="password" required placeholder="Nhập mật khẩu">
            </div>
            <button type="submit" class="btn" style="width: 100%;">Đăng nhập</button>
        </form>
        <p style="margin-top: 12px; font-size: 14px;">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
        </p>
    </div>
</div>
</body>
</html>
