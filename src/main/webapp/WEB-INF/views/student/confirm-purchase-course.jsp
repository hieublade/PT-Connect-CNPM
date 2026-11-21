<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" style="max-width:660px; margin:auto;">
    <h2>Xác Nhận Thanh Toán &amp; Ký Hợp Đồng</h2>
    <p><strong>Khóa học:</strong> ${course.title} (${course.totalSessions} buổi / ${course.validityDays} ngày)</p>
    <p id="lblFinalPrice"><strong>Học phí cần thanh toán:</strong> <fmt:formatNumber value="${finalPrice}" type="number" maxFractionDigits="0"/> đ</p>
    <p id="lblWalletBalance"><strong>Số dư ví hiện tại:</strong> <fmt:formatNumber value="${wallet.balance}" type="number" maxFractionDigits="0"/> đ</p>

    <c:if test="${param.err == 'insufficient_balance'}">
        <div class="alert-error">Số dư ví không đủ để thanh toán khóa học! Vui lòng <a href="${pageContext.request.contextPath}/wallet">nạp thêm tiền vào ví</a>.</div>
    </c:if>
    <c:if test="${param.err == 'enroll_failed'}">
        <div class="alert-error">Không thể khởi tạo hợp đồng đào tạo. Vui lòng thử lại!</div>
    </c:if>
    <c:if test="${param.err == 'payment_failed'}">
        <div class="alert-error">Giao dịch thanh toán khóa học thất bại. Vui lòng thử lại!</div>
    </c:if>

    <c:choose>
        <c:when test="${alreadyEnrolled}">
            <div class="alert-error">Bạn đang theo học khóa này (Không thể mua trùng hợp đồng đang hoạt động).</div>
        </c:when>
        <c:when test="${!enoughBalance}">
            <div class="alert-error">Số dư ví không đủ! Vui lòng <a href="${pageContext.request.contextPath}/wallet">nạp thêm tiền vào ví</a>.</div>
        </c:when>
        <c:otherwise>
            <form method="post" action="${pageContext.request.contextPath}/confirm-purchase">
                <input type="hidden" name="courseId" value="${course.id}">
                <div class="form-row">
                    <label>Chọn địa điểm tập cố định</label>
                    <select id="optLocationList" name="trainingLocation" onchange="document.getElementById('customLocRow').style.display=(this.value==='OTHER'?'block':'none');">
                        <c:forEach var="loc" items="${listLocation}">
                            <option value="${loc.locationName}">${loc.locationName}</option>
                        </c:forEach>
                        <option value="OTHER">Địa điểm khác (tự nhập)</option>
                    </select>
                </div>
                <div id="customLocRow" class="form-row" style="display:none;">
                    <label>Nhập địa điểm chi tiết</label>
                    <input type="text" name="customLocation" placeholder="Số nhà, tên phòng tập...">
                </div>
                <button id="btnConfirmPayment" type="submit" class="btn">Xác Nhận Thanh Toán &amp; Ký Hợp Đồng</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
