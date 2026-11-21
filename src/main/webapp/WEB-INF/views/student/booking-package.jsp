<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" style="max-width:720px; margin:auto;">
    <h2>Đặt Lịch Tập Cùng Huấn Luyện Viên</h2>
    <c:if test="${param.err == 'slot_unavailable'}"><div class="alert-error">Không thể đặt lịch: Khung giờ tập đã được đặt hoặc không còn khả dụng!</div></c:if>
    <c:if test="${param.err == 'conflict'}"><div class="alert-error">Không thể đặt lịch: Lịch tập bị trùng giờ với một ca tập khác của bạn!</div></c:if>
    <c:if test="${param.err == 'trial_exists'}"><div class="alert-error">Không thể đặt lịch: Bạn đã từng sử dụng lượt học thử với huấn luyện viên này rồi!</div></c:if>
    <c:if test="${param.err == 'invalid_enroll'}"><div class="alert-error">Không thể đặt lịch: Hợp đồng không hợp lệ hoặc đã hết số buổi tập khả dụng!</div></c:if>
    <c:if test="${param.err == 'failed'}"><div class="alert-error">Không thể đặt lịch: Đặt lịch ca tập thất bại, vui lòng thử lại sau!</div></c:if>
    <c:if test="${not empty param.err and param.err != 'slot_unavailable' and param.err != 'conflict' and param.err != 'trial_exists' and param.err != 'invalid_enroll' and param.err != 'failed'}">
        <div class="alert-error">Không thể đặt lịch. Vui lòng kiểm tra khung giờ trống, lịch trùng giờ hoặc lượt học thử!</div>
    </c:if>

    <div style="display:flex; gap:8px; margin-bottom:16px;">
        <a class="btn ${bookingType == 'PACKAGE' ? '' : 'btn-secondary'}" href="${pageContext.request.contextPath}/booking-package?bookingType=PACKAGE">Đặt theo Gói Khóa Học</a>
        <a class="btn ${bookingType == 'TRIAL' ? '' : 'btn-secondary'}" href="${pageContext.request.contextPath}/booking-package?bookingType=TRIAL">Buổi Học Thử Miễn Phí</a>
    </div>

    <c:choose>
        <c:when test="${bookingType == 'PACKAGE'}">
            <c:if test="${empty listEnrollment}">
                <div class="alert-error">Chưa có hợp đồng khóa học nào còn buổi tập khả dụng.</div>
            </c:if>
            <c:if test="${not empty listEnrollment}">
                <form method="post" action="${pageContext.request.contextPath}/booking-package">
                    <input type="hidden" name="bookingType" value="PACKAGE">
                    <div class="form-row">
                        <label>Chọn hợp đồng sử dụng cho ca tập</label>
                        <select id="optEnrollment" name="enrollmentId" onchange="location.href='${pageContext.request.contextPath}/booking-package?bookingType=PACKAGE&enrollmentId='+this.value;">
                            <c:forEach var="e" items="${listEnrollment}">
                                <option value="${e.id}" ${selectedEnrollment.id == e.id ? 'selected' : ''}>
                                    PTC-HD-${e.id} - ${e.courseTitle} (${e.remainingSessions}/${e.totalSessions} buổi)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="card" style="background:#f8fafc;">
                        <div id="lblContractCode"><strong>Mã hợp đồng:</strong> PTC-HD-${selectedEnrollment.id}</div>
                        <div id="lblPTName"><strong>Huấn luyện viên:</strong> ${selectedEnrollment.ptName}</div>
                        <div id="lblTrainingLocation"><strong>Địa điểm tập:</strong> ${selectedEnrollment.trainingLocation}</div>
                        <div id="lblRemainingSessions"><strong>Số buổi còn lại sau ca này:</strong> ${selectedEnrollment.remainingSessions - 1}</div>
                    </div>
                    <div class="form-row">
                        <label>Khung giờ tập luyện khả dụng</label>
                        <select id="optSlot" name="slotId" required>
                            <c:forEach var="s" items="${listSlot}">
                                <option value="${s.id}">${s.startTime} → ${s.endTime}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-row">
                        <label>Ghi chú cho Huấn luyện viên (tùy chọn)</label>
                        <input id="txtStudentNote" type="text" name="studentNote" placeholder="Mong muốn tập trung bài Squat và Deadlift...">
                    </div>
                    <button id="btnConfirmBooking" type="submit" class="btn">Xác Nhận Đặt Lịch Ca Tập</button>
                </form>
            </c:if>
        </c:when>
        <c:otherwise>
            <form method="post" action="${pageContext.request.contextPath}/booking-package">
                <input type="hidden" name="bookingType" value="TRIAL">
                <div class="form-row">
                    <label>Chọn Huấn luyện viên học thử (Miễn phí 0 đ, mỗi PT tối đa 1 lần)</label>
                    <select name="ptId" onchange="location.href='${pageContext.request.contextPath}/booking-package?bookingType=TRIAL&ptId='+this.value;">
                        <c:forEach var="pt" items="${listPT}">
                            <option value="${pt.id}" ${selectedPT.id == pt.id ? 'selected' : ''}>${pt.fullName} (${pt.experienceYears} năm KN)</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-row">
                    <label>Chọn khung giờ rảnh</label>
                    <select id="optSlot" name="slotId" required>
                        <c:forEach var="s" items="${listSlot}">
                            <option value="${s.id}">${s.startTime} → ${s.endTime}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-row">
                    <label>Mục tiêu buổi học thử</label>
                    <input id="txtStudentNote" type="text" name="studentNote" placeholder="Đánh giá thể lực, tư vấn lộ trình...">
                </div>
                <button id="btnConfirmBooking" type="submit" class="btn">Đặt Buổi Học Thử Ngay</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
