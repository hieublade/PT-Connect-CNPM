<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Danh Sách Huấn Luyện Viên Đang Chờ Phê Duyệt (${listPendingPTs.size()})</h2>
    <c:if test="${param.msg == 'done'}"><div class="alert-success">Đã xử lý hồ sơ Huấn luyện viên thành công!</div></c:if>
    <c:if test="${empty listPendingPTs}">
        <div class="alert-success">Hiện không có hồ sơ Huấn luyện viên nào đang chờ phê duyệt. Tất cả hồ sơ đã được xử lý!</div>
    </c:if>

    <table id="tblListPendingPTs">
        <thead><tr><th>ID</th><th>Họ tên</th><th>Email / SĐT</th><th>Kinh nghiệm</th><th>Khu vực &amp; Phụ thu</th><th>Chứng chỉ</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="pt" items="${listPendingPTs}">
            <tr>
                <td>${pt.id}</td>
                <td><strong>${pt.fullName}</strong></td>
                <td>${pt.email}<br><small>${pt.phone}</small></td>
                <td>${pt.experienceYears} năm</td>
                <td>
                    <c:forEach var="loc" items="${pt.locations}" varStatus="ls">
                        ${loc.locationName}<c:if test="${!ls.last}">; </c:if>
                    </c:forEach>
                    <br><small>${pt.locationPricingNote}</small>
                </td>
                <td><c:if test="${not empty pt.certificateUrls}"><a href="${pt.certificateUrls}" target="_blank">Xem chứng chỉ</a></c:if></td>
                <td><span class="badge badge-warn">${pt.verificationStatus}</span></td>
                <td>
                    <div class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/pt-pending-approval">
                            <input type="hidden" name="action" value="APPROVE">
                            <input type="hidden" name="profileId" value="${pt.id}">
                            <input type="hidden" name="ptId" value="${pt.userId}">
                            <button id="btnApprove" type="submit" class="btn btn-success">Phê Duyệt Hồ Sơ</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/pt-pending-approval">
                            <input type="hidden" name="action" value="REJECT">
                            <input type="hidden" name="profileId" value="${pt.id}">
                            <button id="btnReject" type="submit" class="btn btn-danger">Từ Chối Hồ Sơ</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
