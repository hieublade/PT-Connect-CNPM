<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Bảng Điều Khiển Quản Trị Viên — ${u.fullName}</h2>
    <div class="grid" style="margin-bottom:16px;">
        <div class="card"><div>Tổng Học Viên</div><div class="kpi-value">${totalStudent}</div></div>
        <div class="card"><div>Tổng PT (${pendingPTCount} chờ duyệt)</div><div class="kpi-value">${totalPT}</div></div>
        <div class="card"><div>Tổng Ca Đặt Lịch</div><div class="kpi-value">${totalBooking}</div></div>
        <div class="card"><div>Tổng Doanh Thu</div><div class="kpi-value"><fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="0"/> đ</div></div>
    </div>
    <div class="grid">
        <a id="btnStats" class="btn" href="${pageContext.request.contextPath}/admin-statistics">Xem Thống Kê &amp; Biểu Đồ</a>
        <a id="btnUsers" class="btn" href="${pageContext.request.contextPath}/user-management">Quản Lý Người Dùng</a>
        <a id="btnApproval" class="btn" href="${pageContext.request.contextPath}/pt-pending-approval">Phê Duyệt PT (${pendingPTCount})</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
