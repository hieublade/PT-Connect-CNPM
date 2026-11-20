<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" style="max-width:680px; margin:auto;">
    <h2>Hủy Đặt Lịch Ca Tập #${booking.id}</h2>
    <p id="lblCancelPolicy"><strong>Chính sách hủy:</strong> ${cancelPolicy}</p>
    <div id="lblBenefitWarning" class="alert-success">${benefitWarning}</div>
    <form method="post" action="${pageContext.request.contextPath}/cancel-booking">
        <input type="hidden" name="bookingId" value="${booking.id}">
        <div class="form-row">
            <label>Lý do hủy lịch</label>
            <input id="txtCancelReason" type="text" name="cancelReason" required placeholder="Nhập lý do hủy...">
        </div>
        <button id="btnConfirmCancel" type="submit" class="btn btn-danger">Xác Nhận Hủy Lịch</button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/my-schedule">Quay lại</a>
    </form>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
