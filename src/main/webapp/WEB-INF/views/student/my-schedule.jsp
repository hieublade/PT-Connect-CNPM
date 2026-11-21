<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Lịch Tập Luyện Của Tôi</h2>
    <c:if test="${param.msg == 'booked'}"><div class="alert-success">Đặt lịch ca tập thành công (đang chờ PT xác nhận)!</div></c:if>
    <c:if test="${param.msg == 'cancelled'}"><div class="alert-success">Đã hủy lịch ca tập và cập nhật chính sách hoàn/đền bù.</div></c:if>
    <c:if test="${param.msg == 'otp_ok'}"><div class="alert-success">Xác nhận điểm danh bằng OTP thành công!</div></c:if>
    <c:if test="${param.err == 'otp_invalid'}"><div class="alert-error">Mã OTP không đúng hoặc đã hết hạn 30 phút!</div></c:if>
    <c:if test="${param.msg == 'review_ok'}"><div class="alert-success">Gửi đánh giá huấn luyện viên thành công!</div></c:if>

    <form method="get" action="${pageContext.request.contextPath}/my-schedule" style="display:flex; gap:8px; margin-bottom:12px;">
        <select id="optFilterStatus" name="status" style="padding:8px;">
            <option value="ALL" ${status == 'ALL' ? 'selected' : ''}>Tất cả ca tập</option>
            <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chờ HLV duyệt (PENDING)</option>
            <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận (CONFIRMED)</option>
            <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành (COMPLETED)</option>
            <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Đã hủy (CANCELLED)</option>
        </select>
        <button id="btnRefresh" type="submit" class="btn">Lọc / Làm mới</button>
    </form>

    <c:if test="${empty listBooking}">
        <p>Chưa có đơn đặt lịch ca tập nào.</p>
    </c:if>
    <table id="tblListBooking">
        <thead><tr><th>Mã ca</th><th>Hình thức</th><th>PT</th><th>Thời gian</th><th>Địa điểm / Khóa</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${listBooking}">
            <tr>
                <td>#${b.id}</td>
                <td><span class="badge">${b.bookingType}</span></td>
                <td>${b.ptName}</td>
                <td class="nowrap">
                    <div><strong>${fn:substring(b.slotStartTime, 0, 10)}</strong></div>
                    <small>${fn:substring(b.slotStartTime, 11, 16)} – ${fn:substring(b.slotEndTime, 11, 16)}</small>
                </td>
                <td>${empty b.courseTitle ? 'Học thử miễn phí' : b.courseTitle}<br><small>${b.trainingLocation}</small></td>
                <td><span class="badge">${b.status}</span></td>
                <td>
                    <c:if test="${b.status == 'PENDING' || b.status == 'CONFIRMED'}">
                        <a id="btnCancelBooking" class="btn btn-danger" href="${pageContext.request.contextPath}/cancel-booking?bookingId=${b.id}">Hủy Đặt Lịch</a>
                    </c:if>
                    <c:if test="${b.status == 'CONFIRMED' && (empty b.attendance || !b.attendance.studentConfirmed)}">
                        <form method="post" action="${pageContext.request.contextPath}/my-schedule" style="margin-top:6px; display:flex; gap:4px;">
                            <input type="hidden" name="action" value="OTP_CHECKIN">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <input type="text" name="otpCode" placeholder="Nhập OTP 6 số" maxlength="6" style="width:120px; padding:4px;" required
                                   oninvalid="this.setCustomValidity('Vui lòng nhập mã OTP 6 số!')"
                                   oninput="this.setCustomValidity('')">
                            <button id="btnOTPCheckin" type="submit" class="btn btn-success">Xác nhận OTP</button>
                        </form>
                    </c:if>
                    <c:if test="${b.status == 'COMPLETED' && empty b.review}">
                        <form method="post" action="${pageContext.request.contextPath}/my-schedule" style="margin-top:6px; display:flex; gap:4px; flex-wrap:wrap;">
                            <input type="hidden" name="action" value="SUBMIT_REVIEW">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <select name="rating" style="padding:4px;">
                                <option value="5">5 ★</option><option value="4">4 ★</option>
                                <option value="3">3 ★</option><option value="2">2 ★</option><option value="1">1 ★</option>
                            </select>
                            <input type="text" name="comment" placeholder="Nhận xét PT..." style="padding:4px;">
                            <button type="submit" class="btn">Gửi đánh giá</button>
                        </form>
                    </c:if>
                    <c:if test="${not empty b.review}">
                        <div><small>Đã đánh giá: <strong>${b.review.rating} ★</strong> — ${b.review.comment}</small></div>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
