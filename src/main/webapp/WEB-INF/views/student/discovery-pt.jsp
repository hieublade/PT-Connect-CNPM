<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Khám Phá Huấn Luyện Viên (PT)</h2>
    <form method="get" action="${pageContext.request.contextPath}/discovery-pt" style="display:flex; gap:8px; margin-bottom:12px;">
        <input id="txtKeyWord" type="text" name="keyWord" value="${keyWord}" placeholder="Tìm kiếm theo tên Huấn luyện viên..." style="flex:1; padding:8px;">
        <button id="btnSearch" type="submit" class="btn">Tìm Kiếm</button>
        <a id="btnRefresh" class="btn btn-secondary" href="${pageContext.request.contextPath}/discovery-pt">Làm mới</a>
    </form>
    <p>Tìm thấy <strong>${listPT.size()}</strong> Huấn luyện viên đã xác thực:</p>
    <c:if test="${param.err == 'course_inactive'}">
        <div class="alert-error">Khóa học này hiện đã ngưng mở bán!</div>
    </c:if>
    <c:if test="${param.err == 'cannot_purchase'}">
        <div class="alert-error">Không tìm thấy thông tin khóa học hoặc không thể mua khóa học này!</div>
    </c:if>
    <c:if test="${empty listPT}">
        <div class="alert-error">Không tìm thấy Huấn luyện viên phù hợp với từ khóa tìm kiếm.</div>
    </c:if>
    <table id="tblListPT">
        <thead>
        <tr>
            <th>STT</th><th>Họ tên PT</th><th>Đánh giá</th><th>Kinh nghiệm</th><th>Khu vực nhận dạy</th><th>Giá chỉ từ</th><th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${listPT}" varStatus="st">
            <tr>
                <td>${st.index + 1}</td>
                <td><strong>${p.fullName}</strong> <span class="badge badge-success">Đã xác thực</span></td>
                <td>${p.averageRating}/5 (${p.totalReviews} đánh giá)</td>
                <td>${p.experienceYears} năm KN</td>
                <td>
                    <c:forEach var="loc" items="${p.locations}" varStatus="ls">
                        ${loc.locationName}<c:if test="${!ls.last}">; </c:if>
                    </c:forEach>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty p.minCoursePrice}">
                            <fmt:formatNumber value="${p.minCoursePrice}" type="number" maxFractionDigits="0"/> đ
                        </c:when>
                        <c:otherwise>Chưa có khóa</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <div class="actions">
                        <a id="btnViewDetail" class="btn" href="${pageContext.request.contextPath}/pt-detail?ptId=${p.id}">Xem Chi Tiết</a>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/pt-detail-course?ptId=${p.id}&ptName=${p.fullName}">Khóa Học</a>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
