<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Bảng điều khiển Huấn luyện viên — ${u.fullName}</h2>
    <p><strong>Trạng thái xét duyệt hồ sơ:</strong> <span class="badge">${ptProfile.verificationStatus}</span>
       | <strong>Đánh giá:</strong> ${ptProfile.averageRating}/5 (${ptProfile.totalReviews} lượt)</p>
    <div class="grid">
        <a id="btnBooking" class="btn" href="${pageContext.request.contextPath}/pt-booking-approval">Duyệt Lịch Dạy</a>
        <a id="btnSlot" class="btn" href="${pageContext.request.contextPath}/manage-slot">Quản Lý Khung Giờ</a>
        <a id="btnCourse" class="btn" href="${pageContext.request.contextPath}/manage-course">Quản Lý Khóa Học</a>
        <a id="btnWorkoutPlan" class="btn" href="${pageContext.request.contextPath}/manage-workout-plan">Quản Lý Giáo Án</a>
        <a id="btnProfile" class="btn btn-secondary" href="${pageContext.request.contextPath}/manage-pt-profile">Hồ Sơ Năng Lực</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
