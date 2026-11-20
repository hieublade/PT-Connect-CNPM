<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Khóa Học Đã Mua (${listEnrollment.size()})</h2>
    <c:if test="${not empty param.purchased}">
        <div class="alert-success">Mua khóa học &amp; ký hợp đồng điện tử thành công! Mã hợp đồng: PTC-HD-${param.purchased}</div>
    </c:if>
    <div class="table-wrap">
    <table id="tblListEnrollment">
        <thead><tr><th>Mã HĐ</th><th>Khóa học</th><th>HLV</th><th>Địa điểm tập cố định</th><th>Buổi còn lại</th><th>Thời hạn</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="e" items="${listEnrollment}">
            <tr>
                <td class="nowrap">PTC-HD-${e.id}</td>
                <td class="col-wide"><strong>${e.courseTitle}</strong></td>
                <td class="nowrap">${e.ptName}</td>
                <td class="col-med">${e.trainingLocation}</td>
                <td class="nowrap">${e.remainingSessions} / ${e.totalSessions}</td>
                <td class="nowrap">${e.startDate}<br><small>đến ${e.endDate}</small></td>
                <td><span class="badge badge-success">${e.status}</span></td>
                <td>
                    <div class="actions-col">
                        <form method="post" action="${pageContext.request.contextPath}/my-course">
                            <input type="hidden" name="enrollmentId" value="${e.id}">
                            <button id="btnDownloadPDF" type="submit" class="btn">Tải Hợp Đồng PDF</button>
                        </form>
                        <a id="btnBookPackage" class="btn btn-secondary" href="${pageContext.request.contextPath}/booking-package?enrollmentId=${e.id}">Đặt lịch tập</a>
                        <c:forEach var="wp" items="${e.workoutPlans}">
                            <a id="btnTogglePlan" class="btn btn-secondary" href="${pageContext.request.contextPath}/my-course?planId=${wp.id}">Xem giáo án</a>
                        </c:forEach>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
</div>

<div class="card">
    <h3>Giáo Án &amp; Bài Tập Chi Tiết</h3>
    <table id="tblListWorkout">
        <thead><tr><th>Bài tập</th><th>Nhóm cơ</th><th>Sets</th><th>Reps</th><th>Mức tạ (kg)</th><th>Nghỉ (s)</th><th>Video</th></tr></thead>
        <tbody>
        <c:forEach var="w" items="${listWorkout}">
            <tr>
                <td>${w.exerciseName}</td><td>${w.targetMuscle}</td><td>${w.sets}</td>
                <td>${w.reps}</td><td>${w.weightKg}</td><td>${w.restSecond}</td>
                <td><c:if test="${not empty w.videoDemoUrl}"><a href="${w.videoDemoUrl}" target="_blank">Xem demo</a></c:if></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
