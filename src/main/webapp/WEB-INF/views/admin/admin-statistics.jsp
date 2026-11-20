<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<script src="https://cdn.plot.ly/plotly-2.32.0.min.js"></script>

<div class="grid">
    <div class="card"><div id="lblTotalStudent">Tổng Học Viên</div><div class="kpi-value">${totalStudent}</div></div>
    <div class="card"><div id="lblTotalPT">Tổng PT (${pendingPTCount} chờ duyệt)</div><div class="kpi-value">${totalPT}</div></div>
    <div class="card"><div id="lblTotalBooking">Tổng Ca Đặt Lịch</div><div class="kpi-value">${totalBooking}</div></div>
    <div class="card"><div id="lblTotalRevenue">Tổng Doanh Thu</div><div class="kpi-value"><fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="0"/> đ</div></div>
</div>

<div class="grid">
    <div class="card"><h3>1. Phân Bố Trạng Thái Ca Tập</h3><div id="chartBookingStatus" style="height:300px;"></div></div>
    <div class="card"><h3>2. Xu Hướng Ca Tập &amp; Doanh Thu Theo Tháng</h3><div id="chartMonthlyTrend" style="height:300px;"></div></div>
</div>
<div class="grid">
    <div class="card"><h3>3. Top Huấn Luyện Viên Dẫn Đầu</h3><div id="chartTopPT" style="height:300px;"></div></div>
    <div class="card"><h3>4. Khung Giờ Tập Đông Nhất</h3><div id="chartPopularHour" style="height:300px;"></div></div>
    <div class="card"><h3>5. Tỷ Lệ Ca Hủy Theo Đối Tượng</h3><div id="chartCancellation" style="height:300px;"></div></div>
</div>

<script>
const statusX = [<c:forEach var="e" items="${statusCounts}" varStatus="s">'${e.key}'${!s.last ? ',' : ''}</c:forEach>];
const statusY = [<c:forEach var="e" items="${statusCounts}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
Plotly.newPlot('chartBookingStatus', [{ x: statusX, y: statusY, type: 'bar', marker: { color: '#2563eb' } }], { margin: { t: 20, b: 40, l: 40, r: 20 }, paper_bgcolor: '#ffffff', plot_bgcolor: '#f8fafc' });

const revX = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="s">'${e.key}'${!s.last ? ',' : ''}</c:forEach>];
const revY = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
const bookX = [<c:forEach var="e" items="${monthlyBookings}" varStatus="s">'${e.key}'${!s.last ? ',' : ''}</c:forEach>];
const bookY = [<c:forEach var="e" items="${monthlyBookings}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
Plotly.newPlot('chartMonthlyTrend', [
    { x: bookX, y: bookY, name: 'Số ca tập', type: 'bar', marker: { color: '#60a5fa' } },
    { x: revX, y: revY, name: 'Doanh thu (VND)', type: 'scatter', yaxis: 'y2', line: { color: '#16a34a', width: 3 } }
], { margin: { t: 20, b: 40, l: 40, r: 50 }, yaxis2: { overlaying: 'y', side: 'right' }, paper_bgcolor: '#ffffff', plot_bgcolor: '#f8fafc' });

const ptNames = [<c:forEach var="e" items="${ptCompletedCounts}" varStatus="s">'${e.key}'${!s.last ? ',' : ''}</c:forEach>];
const ptVals = [<c:forEach var="e" items="${ptCompletedCounts}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
Plotly.newPlot('chartTopPT', [{ y: ptNames, x: ptVals, type: 'bar', orientation: 'h', marker: { color: '#0284c7' } }], { margin: { t: 20, b: 40, l: 110, r: 20 }, paper_bgcolor: '#ffffff', plot_bgcolor: '#f8fafc' });

const hourX = [<c:forEach var="e" items="${hourCounts}" varStatus="s">'${e.key}h'${!s.last ? ',' : ''}</c:forEach>];
const hourY = [<c:forEach var="e" items="${hourCounts}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
Plotly.newPlot('chartPopularHour', [{ x: hourX, y: hourY, type: 'bar', marker: { color: '#7c3aed' } }], { margin: { t: 20, b: 40, l: 40, r: 20 }, paper_bgcolor: '#ffffff', plot_bgcolor: '#f8fafc' });

const cRoles = [<c:forEach var="e" items="${cancelByRole}" varStatus="s">'${e.key}'${!s.last ? ',' : ''}</c:forEach>];
const cVals = [<c:forEach var="e" items="${cancelByRole}" varStatus="s">${e.value}${!s.last ? ',' : ''}</c:forEach>];
Plotly.newPlot('chartCancellation', [{ labels: cRoles, values: cVals, type: 'pie', hole: 0.45 }], { margin: { t: 20, b: 20, l: 20, r: 20 }, paper_bgcolor: '#ffffff' });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
