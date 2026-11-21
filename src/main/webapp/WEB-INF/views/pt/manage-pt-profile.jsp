<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" style="max-width:760px; margin:auto;">
    <h2>Quản Lý Hồ Sơ Năng Lực PT</h2>
    <c:if test="${param.err == 'empty_location'}"><div class="alert-error">Địa điểm nhận dạy không được để trống!</div></c:if>
    <c:if test="${param.msg == 'saved' || param.msg == 'avatar'}"><div class="alert-success">Cập nhật hồ sơ thành công!</div></c:if>

    <div style="display:flex; gap:16px; align-items:center; margin-bottom:16px;">
        <img src="${empty u.avatarUrl ? 'https://api.dicebear.com/7.x/avataaars/svg?seed=pt' : u.avatarUrl}"
             alt="Avatar" style="width:84px; height:84px; border-radius:50%; object-fit:cover; border:1px solid #cbd5e1;">
        <div>
            <div><strong>${u.fullName}</strong> (${u.email})</div>
            <div>Trạng thái hồ sơ: <span class="badge">${ptProfile.verificationStatus == 'VERIFIED' ? 'Đã xác thực' : (ptProfile.verificationStatus == 'REJECTED' ? 'Bị từ chối' : 'Chờ phê duyệt')}</span></div>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/manage-pt-profile" style="margin-bottom:16px;">
        <input type="hidden" name="action" value="SAVE_PROFILE">
        <div class="form-row"><label>Giới thiệu bản thân (Bio)</label><textarea id="txtBio" name="bio" rows="3">${ptProfile.bio}</textarea></div>
        <div class="form-row"><label>Số năm kinh nghiệm</label><input id="txtExperienceYears" type="number" name="experienceYears" min="0" value="${ptProfile.experienceYears}" required></div>
        <div class="form-row"><label>Chính sách phụ thu theo địa điểm</label><input id="txtLocationPricingNote" type="text" name="locationPricingNote" value="${ptProfile.locationPricingNote}"></div>
        <div class="form-row"><label>Đường dẫn chứng chỉ / bằng cấp</label><input id="txtCertificateURL" type="text" name="certificateUrls" value="${ptProfile.certificateUrls}"></div>
        <div class="form-row">
            <label>Danh sách địa điểm nhận dạy (phân cách bằng dấu phẩy, tối thiểu 1 địa điểm)</label>
            <input id="optAvailableLocations" type="text" name="locationsText" required
                   value="<c:forEach var='l' items='${ptProfile.locations}' varStatus='st'>${l.locationName}<c:if test='${!st.last}'>, </c:if></c:forEach>">
            <small>Khu vực gợi ý trên hệ thống:
                <c:forEach var="av" items="${availableLocations}" varStatus="avs">
                    ${av.locationName}<c:if test="${!avs.last}">; </c:if>
                </c:forEach>
            </small>
        </div>
        <button id="btnSaveProfile" type="submit" class="btn">Lưu Cập Nhật Hồ Sơ</button>
    </form>

    <hr style="margin:16px 0; border:none; border-top:1px solid #e2e8f0;">
    <form method="post" action="${pageContext.request.contextPath}/manage-pt-profile" enctype="multipart/form-data">
        <input type="hidden" name="action" value="UPLOAD_AVATAR">
        <div class="form-row"><label>Tải ảnh đại diện mới</label><input type="file" name="avatarFile" accept="image/*" required></div>
        <button type="submit" class="btn btn-secondary">Cập Nhật Ảnh Đại Diện</button>
    </form>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
