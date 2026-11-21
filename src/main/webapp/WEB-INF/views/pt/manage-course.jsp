<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Quản Lý Khóa Học Mở Bán</h2>
    <c:if test="${param.err == 'has_enrollment'}"><div class="alert-error">Khóa học đã có hợp đồng học viên, chỉ được Ngưng bán!</div></c:if>
    <c:if test="${param.msg == 'ok'}"><div class="alert-success">Cập nhật khóa học thành công!</div></c:if>

    <form method="post" action="${pageContext.request.contextPath}/manage-course">
        <input type="hidden" name="action" value="CREATE">
        <div class="grid">
            <div class="form-row"><label>Tên khóa học (*)</label><input id="txtTitle" type="text" name="title" required
                   oninvalid="this.setCustomValidity('Vui lòng nhập tên khóa học!')" oninput="this.setCustomValidity('')"></div>
            <div class="form-row"><label>Số buổi tập (*)</label><input id="txtTotalSessions" type="number" name="totalSessions" min="1" required
                   oninvalid="this.setCustomValidity(this.validity.valueMissing ? 'Vui lòng nhập số buổi tập!' : 'Số buổi tập tối thiểu là 1 buổi!')" oninput="this.setCustomValidity('')"></div>
            <div class="form-row"><label>Hạn sử dụng (ngày) (*)</label><input id="txtValidityDays" type="number" name="validityDays" min="1" required
                   oninvalid="this.setCustomValidity(this.validity.valueMissing ? 'Vui lòng nhập hạn sử dụng khóa học!' : 'Hạn sử dụng tối thiểu là 1 ngày!')" oninput="this.setCustomValidity('')"></div>
            <div class="form-row"><label>Học phí trọn gói (VND) (*)</label><input id="txtBasePrice" type="number" name="basePrice" min="0" required
                   oninvalid="this.setCustomValidity(this.validity.valueMissing ? 'Vui lòng nhập học phí khóa học!' : 'Học phí không được là số âm!')" oninput="this.setCustomValidity('')"></div>
        </div>
        <div class="form-row"><label>Mô tả chi tiết</label><textarea id="txtDescription" name="description" rows="2"></textarea></div>
        <button id="btnCreateCourse" type="submit" class="btn">Tạo Khóa Học</button>
    </form>
</div>

<div class="card">
    <h3>Danh Sách Khóa Học (${listCourse.size()})</h3>
    <table id="tblListCourse">
        <thead><tr><th>STT</th><th>Tên khóa học</th><th>Học phí</th><th>Số buổi</th><th>Thời hạn</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${listCourse}" varStatus="st">
            <tr>
                <td>${st.index + 1}</td>
                <td><strong>${c.title}</strong><br><small>${c.description}</small></td>
                <td><fmt:formatNumber value="${c.basePrice}" type="number" maxFractionDigits="0"/> đ</td>
                <td>${c.totalSessions}</td>
                <td>${c.validityDays} ngày</td>
                <td><span class="badge ${c.status == 'ACTIVE' ? 'badge-success' : 'badge-warn'}">${c.status}</span></td>
                <td>
                    <div class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/manage-course">
                            <input type="hidden" name="action" value="TOGGLE_STATUS">
                            <input type="hidden" name="courseId" value="${c.id}">
                            <input type="hidden" name="currentStatus" value="${c.status}">
                            <button id="btnDeactiveCourse" type="submit" class="btn btn-secondary">
                                ${c.status == 'ACTIVE' ? 'Ngưng bán' : 'Mở bán lại'}
                            </button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/manage-course">
                            <input type="hidden" name="action" value="DELETE">
                            <input type="hidden" name="courseId" value="${c.id}">
                            <button id="btnDeleteCourse" type="submit" class="btn btn-danger">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
