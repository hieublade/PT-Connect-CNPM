<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" id="lblWalletInfo">
    <h2>Quản Lý Ví Điện Tử Nội Bộ &amp; Nạp Tiền</h2>
    <c:if test="${param.success == '1'}"><div class="alert-success">Nạp tiền thành công!</div></c:if>
    <c:if test="${param.error == 'amount'}"><div class="alert-error">Số tiền nạp phải từ 10.000 đ đến 100.000.000 đ.</div></c:if>
    <p><strong>Chủ tài khoản:</strong> ${sessionScope.currentUser.fullName} |
       <strong>Trạng thái ví:</strong> <span class="badge badge-success">${wallet.status}</span></p>
    <p class="kpi-value">Số dư khả dụng: <fmt:formatNumber value="${wallet.balance}" type="number" maxFractionDigits="0"/> ${wallet.currency}</p>

    <form method="post" action="${pageContext.request.contextPath}/wallet">
        <div style="display:flex; gap:8px; margin-bottom:10px; flex-wrap:wrap;">
            <button type="button" class="btn btn-secondary" onclick="document.getElementById('txtCustomAmount').value='1000000'">1Tr</button>
            <button type="button" class="btn btn-secondary" onclick="document.getElementById('txtCustomAmount').value='2000000'">2Tr</button>
            <button type="button" class="btn btn-secondary" onclick="document.getElementById('txtCustomAmount').value='5000000'">5Tr</button>
            <button type="button" class="btn btn-secondary" onclick="document.getElementById('txtCustomAmount').value='10000000'">10Tr</button>
        </div>
        <div class="form-row">
            <label>Số tiền nạp (VND)</label>
            <input id="txtCustomAmount" type="number" name="amount" min="10000" max="100000000" value="2000000" required
                   oninvalid="this.setCustomValidity(this.validity.valueMissing ? 'Vui lòng nhập số tiền cần nạp!' : (this.validity.rangeUnderflow ? 'Số tiền nạp tối thiểu là 10.000 đ!' : 'Số tiền nạp tối đa là 100.000.000 đ!'))"
                   oninput="this.setCustomValidity('')">
        </div>
        <div class="form-row">
            <label>Phương thức thanh toán</label>
            <select id="optPaymentMethod" name="paymentMethod">
                <option value="BANK_TRANSFER">Chuyển khoản ngân hàng (VNPAY QR / MoMo / VietQR)</option>
                <option value="CREDIT_CARD">Thẻ tín dụng / ghi nợ</option>
                <option value="CASH">Tiền mặt tại quầy</option>
            </select>
        </div>
        <button id="btnTopUp" type="submit" class="btn">Nạp Tiền Ngay</button>
    </form>
</div>

<div class="card">
    <h3>Lịch Sử Giao Dịch Tài Chính</h3>
    <table id="tblListTransaction">
        <thead><tr><th>Mã Giao Dịch</th><th>Loại GD</th><th>Số Tiền</th><th>Phương Thức</th><th>Trạng Thái</th><th>Thời Gian</th></tr></thead>
        <tbody>
        <c:forEach var="t" items="${listTransaction}">
            <tr>
                <td>${t.transactionRef}</td>
                <td>${t.paymentType}</td>
                <td><fmt:formatNumber value="${t.amount}" type="number" maxFractionDigits="0"/> đ</td>
                <td>${t.paymentMethod}</td>
                <td><span class="badge">${t.status}</span></td>
                <td>${t.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
