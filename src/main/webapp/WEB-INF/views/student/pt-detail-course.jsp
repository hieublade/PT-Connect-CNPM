<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2 id="lblPTName">Khóa Học Đang Mở Bán — HLV ${ptName}</h2>
    <table id="tblCourseList">
        <thead><tr><th>Tên khóa học</th><th>Mô tả</th><th>Số buổi</th><th>Thời hạn</th><th>Học phí</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${listCourse}">
            <tr>
                <td><strong>${c.title}</strong></td><td>${c.description}</td><td>${c.totalSessions} buổi</td>
                <td>${c.validityDays} ngày</td>
                <td><fmt:formatNumber value="${c.basePrice}" type="number" maxFractionDigits="0"/> đ</td>
                <td><a id="btnBuyCourse" class="btn" href="${pageContext.request.contextPath}/confirm-purchase?courseId=${c.id}">Mua Gói Tập Này</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
