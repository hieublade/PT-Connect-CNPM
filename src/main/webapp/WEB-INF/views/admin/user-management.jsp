<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Quản Lý Người Dùng &amp; Trạng Thái Hoạt Động</h2>
    <c:if test="${param.err == 'self_lock'}"><div class="alert-error">Không thể tự khóa tài khoản Quản trị viên đang đăng nhập!</div></c:if>
    <c:if test="${param.msg == 'updated'}"><div class="alert-success">Cập nhật trạng thái tài khoản thành công!</div></c:if>

    <form method="get" action="${pageContext.request.contextPath}/user-management" style="display:flex; gap:8px; align-items:center; margin-bottom:12px;">
        <select id="optFilterRole" name="role" style="padding:8px;">
            <option value="ALL" ${roleFilter == 'ALL' ? 'selected' : ''}>Tất cả</option>
            <option value="STUDENT" ${roleFilter == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
            <option value="PT" ${roleFilter == 'PT' ? 'selected' : ''}>PT</option>
            <option value="ADMIN" ${roleFilter == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
        </select>
        <button type="submit" class="btn">Lọc theo vai trò</button>
        <span id="lblCountFound">Tìm thấy <strong>${countFound}</strong> tài khoản</span>
    </form>

    <table id="tblListUsers">
        <thead><tr><th>ID</th><th>Họ tên</th><th>Email</th><th>SĐT</th><th>Vai trò</th><th>Trạng thái</th><th>Ngày tạo</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="usr" items="${listUsers}">
            <tr>
                <td>${usr.id}</td>
                <td><strong>${usr.fullName}</strong></td>
                <td>${usr.email}</td>
                <td>${usr.phone}</td>
                <td><span class="badge">${usr.role}</span></td>
                <td><span class="badge ${usr.status == 'ACTIVE' ? 'badge-success' : (usr.status == 'SUSPENDED' ? 'badge-danger' : 'badge-warn')}">${usr.status}</span></td>
                <td>${usr.createdAt}</td>
                <td>
                    <c:choose>
                        <c:when test="${usr.status == 'ACTIVE'}">
                            <form method="post" action="${pageContext.request.contextPath}/user-management">
                                <input type="hidden" name="userId" value="${usr.id}">
                                <input type="hidden" name="action" value="LOCK">
                                <button id="btnLockUser" type="submit" class="btn btn-danger">Khóa tài khoản</button>
                            </form>
                        </c:when>
                        <c:when test="${usr.status == 'SUSPENDED'}">
                            <form method="post" action="${pageContext.request.contextPath}/user-management">
                                <input type="hidden" name="userId" value="${usr.id}">
                                <input type="hidden" name="action" value="UNLOCK">
                                <button id="btnUnlockUser" type="submit" class="btn btn-success">Mở khóa tài khoản</button>
                            </form>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
