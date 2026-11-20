<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Bảng điều khiển Học viên — Xin chào ${u.fullName}</h2>
    <div class="grid grid-3">
        <a id="btnDiscoveryPt" class="btn" href="${pageContext.request.contextPath}/discovery-pt">Khám Phá PT</a>
        <a id="btnWallet" class="btn" href="${pageContext.request.contextPath}/wallet">Ví Tiền</a>
        <a id="btnMyCourse" class="btn" href="${pageContext.request.contextPath}/my-course">Khóa Của Tôi</a>
        <a id="btnMySchedule" class="btn" href="${pageContext.request.contextPath}/my-schedule">Lịch Của Tôi</a>
        <a id="btnBookSchedule" class="btn" href="${pageContext.request.contextPath}/booking-package">Đặt Lịch Ca Tập</a>
        <a id="btnProfile" class="btn btn-secondary" href="${pageContext.request.contextPath}/profile">Hồ Sơ Cá Nhân</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
