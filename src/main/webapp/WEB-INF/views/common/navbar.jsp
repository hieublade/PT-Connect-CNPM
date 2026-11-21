<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="navbar">
    <c:choose>
        <c:when test="${sessionScope.currentUser.role == 'PT'}">
            <a class="brand" href="${pageContext.request.contextPath}/pt-home">PT-Connect (HLV)</a>
            <div class="nav-links">
                <a id="subBookingApproval" href="${pageContext.request.contextPath}/pt-booking-approval">Duyệt Lịch</a>
                <a id="subSlot" href="${pageContext.request.contextPath}/manage-slot">Khung Giờ</a>
                <a id="subCourse" href="${pageContext.request.contextPath}/manage-course">Quản Lý Khóa</a>
                <a id="subWorkoutPlan" href="${pageContext.request.contextPath}/manage-workout-plan">Giáo Án</a>
                <a id="subProfile" href="${pageContext.request.contextPath}/manage-pt-profile">Hồ Sơ PT</a>
                <span><strong>${sessionScope.currentUser.fullName}</strong></span>
                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </c:when>
        <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
            <a class="brand" href="${pageContext.request.contextPath}/admin-dashboard">PT-Connect (Admin)</a>
            <div class="nav-links">
                <a id="subStats" href="${pageContext.request.contextPath}/admin-statistics">Thống Kê</a>
                <a id="subUsers" href="${pageContext.request.contextPath}/user-management">Quản Lý Người Dùng</a>
                <a id="subApproval" href="${pageContext.request.contextPath}/pt-pending-approval">Phê Duyệt PT</a>
                <span><strong>${sessionScope.currentUser.fullName}</strong></span>
                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </c:when>
        <c:when test="${sessionScope.currentUser.role == 'STUDENT'}">
            <a class="brand" href="${pageContext.request.contextPath}/student-home">PT-Connect (Học viên)</a>
            <div class="nav-links">
                <a id="subDiscoveryPt" href="${pageContext.request.contextPath}/discovery-pt">Khám Phá PT</a>
                <a id="subBookSchedule" href="${pageContext.request.contextPath}/booking-package">Đặt Lịch</a>
                <a id="subMySchedule" href="${pageContext.request.contextPath}/my-schedule">Lịch Của Tôi</a>
                <a id="subMyCourse" href="${pageContext.request.contextPath}/my-course">Khóa Của Tôi</a>
                <a id="subWallet" href="${pageContext.request.contextPath}/wallet">Ví Tiền</a>
                <a id="subProfile" href="${pageContext.request.contextPath}/profile">Hồ Sơ</a>
                <span><strong>${sessionScope.currentUser.fullName}</strong></span>
                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </c:when>
    </c:choose>
</div>
