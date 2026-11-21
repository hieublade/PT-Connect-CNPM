<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card">
    <h2>Quản Lý Khung Giờ Rảnh</h2>
    <c:if test="${param.err == 'overlap'}"><div class="alert-error">Khung giờ bị trùng hoặc chồng lấn với lịch rảnh khác!</div></c:if>
    <c:if test="${param.err == 'invalid_time'}"><div class="alert-error">Thời gian bắt đầu phải ở tương lai và thời gian kết thúc phải sau thời gian bắt đầu!</div></c:if>
    <c:if test="${param.msg == 'created'}"><div class="alert-success">Mở khung giờ rảnh thành công!</div></c:if>
    <c:if test="${param.msg == 'deleted'}"><div class="alert-success">Đã xóa khung giờ rảnh thành công!</div></c:if>

    <form method="post" action="${pageContext.request.contextPath}/manage-slot" class="grid">
        <input type="hidden" name="action" value="OPEN_SLOT">
        <div class="form-row"><label>Ngày rảnh</label><input id="txtDate" type="date" name="date" required></div>
        <div class="form-row"><label>Giờ bắt đầu</label><input id="txtStartTime" type="time" name="startTime" required></div>
        <div class="form-row"><label>Giờ kết thúc</label><input id="txtEndTime" type="time" name="endTime" required></div>
        <div class="form-row" style="display:flex; align-items:flex-end;">
            <button id="btnOpenSlot" type="submit" class="btn">Mở Khung Giờ</button>
        </div>
    </form>
</div>

<div class="card">
    <h3>Danh Sách Khung Giờ (${listSlot.size()})</h3>
    <table id="tblListSlot">
        <thead><tr><th>STT</th><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${listSlot}" varStatus="st">
            <tr>
                <td>${st.index + 1}</td>
                <td class="nowrap">${fn:replace(s.startTime, 'T', ' ')}</td>
                <td class="nowrap">${fn:replace(s.endTime, 'T', ' ')}</td>
                <td><span class="badge ${s.status == 'AVAILABLE' ? 'badge-success' : 'badge-warn'}">${s.status == 'AVAILABLE' ? 'Khả dụng' : 'Đã được đặt'}</span></td>
                <td>
                    <c:if test="${s.status == 'AVAILABLE'}">
                        <form method="post" action="${pageContext.request.contextPath}/manage-slot">
                            <input type="hidden" name="action" value="DELETE_SLOT">
                            <input type="hidden" name="slotId" value="${s.id}">
                            <button id="btnDeleteSlot" type="submit" class="btn btn-danger">Xóa</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
