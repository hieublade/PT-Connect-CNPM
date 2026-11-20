<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Quản Lý Giáo Án Học Viên</h2>
    <c:if test="${param.err == 'invalid_dates'}"><div class="alert-error">Ngày bắt đầu không được sau ngày kết thúc!</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/manage-workout-plan">
        <input type="hidden" name="action" value="CREATE_PLAN">
        <div class="grid">
            <div class="form-row">
                <label>Chọn Học viên / Hợp đồng</label>
                <select id="optListEnrollment" name="enrollmentId" required
                        onchange="location.href='${pageContext.request.contextPath}/manage-workout-plan?enrollmentId='+this.value;">
                    <option value="">-- Tất cả hợp đồng --</option>
                    <c:forEach var="e" items="${listEnrollment}">
                        <option value="${e.id}" ${enrollmentIdFilter == e.id ? 'selected' : ''}>PTC-HD-${e.id} — ${e.studentName} (${e.courseTitle})</option>
                    </c:forEach>
                </select>
                <c:if test="${not empty enrollment}">
                    <small id="lblEnrollmentInfo">Đang lọc hợp đồng PTC-HD-${enrollment.id}: ${enrollment.studentName} (${enrollment.remainingSessions}/${enrollment.totalSessions} buổi)</small>
                </c:if>
            </div>
            <div class="form-row"><label>Tiêu đề giáo án</label><input id="txtTitle" type="text" name="title" required></div>
            <div class="form-row"><label>Mục tiêu</label><input id="txtGoal" type="text" name="goal"></div>
            <div class="form-row"><label>Ngày bắt đầu</label><input id="txtStartDate" type="date" name="startDate" required></div>
            <div class="form-row"><label>Ngày kết thúc</label><input id="txtEndDate" type="date" name="endDate" required></div>
        </div>
        <button id="btnCreatePlan" type="submit" class="btn">Tạo Giáo Án</button>
    </form>
</div>

<div class="card">
    <h3>Danh Sách Giáo Án (${listWorkoutPlan.size()})</h3>
    <c:forEach var="wp" items="${listWorkoutPlan}">
        <div class="card" style="background:#f8fafc;">
            <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap;">
                <div><strong>${wp.title}</strong> — Học viên: ${wp.studentName} (${wp.courseTitle}) | ${wp.startDate} → ${wp.endDate}</div>
                <form method="post" action="${pageContext.request.contextPath}/manage-workout-plan">
                    <input type="hidden" name="action" value="DELETE_PLAN">
                    <input type="hidden" name="planId" value="${wp.id}">
                    <button type="submit" class="btn btn-danger">Xóa giáo án</button>
                </form>
            </div>
            <table id="tblListWorkoutPlan">
                <thead><tr><th>Bài tập</th><th>Nhóm cơ</th><th>Sets x Reps</th><th>Mức tạ (kg)</th><th>Nghỉ (s)</th><th>Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="w" items="${wp.workouts}">
                    <tr>
                        <td>${w.exerciseName}</td><td>${w.targetMuscle}</td><td>${w.sets} x ${w.reps}</td>
                        <td>${w.weightKg}</td><td>${w.restSecond}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/manage-workout-plan">
                                <input type="hidden" name="action" value="DELETE_WORKOUT">
                                <input type="hidden" name="workoutId" value="${w.id}">
                                <button type="submit" class="btn btn-danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <form method="post" action="${pageContext.request.contextPath}/manage-workout-plan" style="margin-top:10px; display:flex; gap:6px; flex-wrap:wrap;">
                <input type="hidden" name="action" value="ADD_WORKOUT">
                <input type="hidden" name="planId" value="${wp.id}">
                <input type="text" name="exerciseName" placeholder="Tên bài tập" required>
                <input type="text" name="targetMuscle" placeholder="Nhóm cơ">
                <input type="number" name="sets" value="3" style="width:70px;" required>
                <input type="number" name="reps" value="12" style="width:70px;" required>
                <input type="number" name="weightKg" value="10" step="0.5" style="width:80px;">
                <input type="number" name="restSeconds" value="60" style="width:80px;" required>
                <input type="url" name="videoDemoUrl" placeholder="Video URL">
                <button type="submit" class="btn">Thêm bài tập này</button>
            </form>
        </div>
    </c:forEach>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
