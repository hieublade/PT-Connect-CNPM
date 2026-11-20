<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" id="lblPTInfor">
    <h2>${pt.fullName} <span class="badge badge-success">${pt.verificationStatus}</span></h2>
    <p><strong>Đánh giá:</strong> ${pt.averageRating} / 5.0 (${pt.totalReviews} lượt đánh giá) | <strong>Kinh nghiệm:</strong> ${pt.experienceYears} năm</p>
    <p><strong>Liên hệ:</strong> ${pt.phone} | ${pt.email}</p>
    <p><strong>Giới thiệu:</strong> ${empty pt.bio ? 'Chưa có tiểu sử' : pt.bio}</p>
    <p><strong>Chính sách giá/di chuyển:</strong> ${pt.locationPricingNote}</p>
    <p id="lblLocations"><strong>Khu vực nhận dạy:</strong>
        <c:forEach var="loc" items="${pt.locations}" varStatus="st">
            ${loc.locationName}<c:if test="${!st.last}">; </c:if>
        </c:forEach>
    </p>
</div>

<div class="card">
    <h3>Khóa học đang mở bán</h3>
    <table id="tblListCourse">
        <thead><tr><th>Tên khóa học</th><th>Mô tả</th><th>Số buổi</th><th>Thời hạn</th><th>Học phí</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${pt.courses}">
            <tr>
                <td><strong>${c.title}</strong></td>
                <td>${c.description}</td>
                <td>${c.totalSessions} buổi</td>
                <td>${c.validityDays} ngày</td>
                <td><fmt:formatNumber value="${c.basePrice}" type="number" maxFractionDigits="0"/> đ</td>
                <td><a class="btn" href="${pageContext.request.contextPath}/confirm-purchase?courseId=${c.id}">Mua Gói Tập Này</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="card">
    <h3>Khung giờ rảnh khả dụng trong tương lai (${pt.availabilitySlots.size()})</h3>
    <table id="tblListSlot">
        <thead><tr><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${pt.availabilitySlots}">
            <tr>
                <td class="nowrap">${fn:replace(s.startTime, 'T', ' ')}</td>
                <td class="nowrap">${fn:replace(s.endTime, 'T', ' ')}</td>
                <td><span class="badge badge-success">${s.status}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="card">
    <h3>Đánh giá từ học viên (${listReview.size()})</h3>
    <table id="tblListReview">
        <thead><tr><th>Học viên</th><th>Số sao</th><th>Nhận xét</th><th>Thời gian</th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${listReview}">
            <tr><td>${r.studentName}</td><td>${r.rating} / 5</td><td>${r.comment}</td><td>${r.createdAt}</td></tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
