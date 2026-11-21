<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Quản Lý &amp; Phê Duyệt Lịch Tập</h2>
    <form method="get" action="${pageContext.request.contextPath}/pt-booking-approval" style="display:flex; gap:8px; margin-bottom:12px;">
        <select id="optStatusFilter" name="statusFilter" style="padding:8px;">
            <option value="ALL" ${statusFilter == 'ALL' ? 'selected' : ''}>Tất cả trạng thái</option>
            <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Chờ duyệt (PENDING)</option>
            <option value="CONFIRMED" ${statusFilter == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận (CONFIRMED)</option>
            <option value="COMPLETED" ${statusFilter == 'COMPLETED' ? 'selected' : ''}>Hoàn thành (COMPLETED)</option>
            <option value="CANCELLED" ${statusFilter == 'CANCELLED' ? 'selected' : ''}>Đã hủy (CANCELLED)</option>
        </select>
        <button type="submit" class="btn">Lọc</button>
    </form>

    <h3>Ca Tập Chờ Phê Duyệt (${pendingBookingList.size()})</h3>
    <table id="tblPendingBookingList">
        <thead><tr><th>Mã ca</th><th>Loại</th><th>Học viên (SĐT)</th><th>Thời gian</th><th>Giá trị</th><th>Ghi chú</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${pendingBookingList}">
            <tr>
                <td>#${b.id}</td><td><span class="badge badge-warn">${b.bookingType}</span></td>
                <td>${b.studentName} (${b.studentPhone})</td>
                <td class="nowrap">
                    <div><strong>${fn:substring(b.slotStartTime, 0, 10)}</strong></div>
                    <small>${fn:substring(b.slotStartTime, 11, 16)} – ${fn:substring(b.slotEndTime, 11, 16)}</small>
                </td>
                <td class="nowrap"><fmt:formatNumber value="${b.sessionFee}" type="number" maxFractionDigits="0"/> đ</td>
                <td>${b.studentNote}</td>
                <td>
                    <div class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval">
                            <input type="hidden" name="action" value="APPROVE">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <button id="btnConfirmBooking" type="submit" class="btn btn-success">Xác Nhận</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval" style="display:flex; gap:4px;">
                            <input type="hidden" name="action" value="REJECT">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <input type="text" name="reason" placeholder="Lý do từ chối" required style="width:120px; padding:4px;"
                                   oninvalid="this.setCustomValidity('Vui lòng nhập lý do từ chối ca tập!')"
                                   oninput="this.setCustomValidity('')">
                            <button id="btnRejectBooking" type="submit" class="btn btn-danger">Từ Chối</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="card">
    <h3>Ca Tập Đã Xác Nhận (${confirmedBookingList.size()})</h3>
    <table id="tblConfirmedBookingList">
        <thead><tr><th>Mã ca</th><th>Học viên</th><th>Thời gian</th><th>Địa điểm</th><th>Điểm danh OTP</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${confirmedBookingList}">
            <tr>
                <td>#${b.id}</td><td>${b.studentName}</td>
                <td class="nowrap">
                    <div><strong>${fn:substring(b.slotStartTime, 0, 10)}</strong></div>
                    <small>${fn:substring(b.slotStartTime, 11, 16)} – ${fn:substring(b.slotEndTime, 11, 16)}</small>
                </td>
                <td>${b.trainingLocation}</td>
                <td>
                    <c:if test="${not empty b.attendance and not empty b.attendance.otpCode}">
                        OTP: <strong style="font-size:16px; color:#2563eb;">${b.attendance.otpCode}</strong>
                        <br><small>HV xác nhận: ${b.attendance.studentConfirmed}</small>
                    </c:if>
                    <c:if test="${empty b.attendance or empty b.attendance.otpCode}">
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval">
                            <input type="hidden" name="action" value="OPEN_OTP">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <button id="btnOpenAttendancePopover" type="submit" class="btn">Mở OTP Điểm Danh</button>
                        </form>
                    </c:if>
                </td>
                <td>
                    <div class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval">
                            <input type="hidden" name="action" value="COMPLETE_SESSION">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <button type="submit" class="btn btn-success">Hoàn Tất Buổi</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval">
                            <input type="hidden" name="action" value="MARK_ABSENT">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <button id="btnMarkAbsent" type="submit" class="btn btn-secondary">Báo Vắng (ABSENT)</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/pt-booking-approval" style="display:flex; gap:4px;">
                            <input type="hidden" name="action" value="CANCEL_CONFIRMED">
                            <input type="hidden" name="bookingId" value="${b.id}">
                            <input type="text" name="reason" placeholder="Lý do hủy" required style="width:110px; padding:4px;"
                                   oninvalid="this.setCustomValidity('Vui lòng nhập lý do hủy ca tập!')"
                                   oninput="this.setCustomValidity('')">
                            <button id="btnCancelBooking" type="submit" class="btn btn-danger">Hủy Ca</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
