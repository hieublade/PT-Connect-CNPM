<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="card" style="max-width:640px; margin:auto;">
    <h2>Hồ Sơ Cá Nhân Học Viên</h2>
    <c:if test="${param.updated == '1'}"><div class="alert-success">Cập nhật ảnh đại diện thành công!</div></c:if>
    <img id="imgAvatar" src="${empty avatarURL ? 'https://api.dicebear.com/7.x/avataaars/svg?seed=student' : avatarURL}"
         alt="Avatar" style="width:96px; height:96px; border-radius:50%; object-fit:cover; margin-bottom:12px; border:1px solid #cbd5e1;">
    <p id="lblFullName"><strong>Họ và tên:</strong> ${u.fullName}</p>
    <p id="lblEmail"><strong>Email:</strong> ${u.email}</p>
    <p id="lblPhone"><strong>Số điện thoại:</strong> ${u.phone}</p>
    <p id="lblRole"><strong>Vai trò:</strong> <span class="badge">${u.role}</span></p>
    <p id="lblStatus"><strong>Trạng thái tài khoản:</strong> <span class="badge badge-success">${u.status}</span></p>

    <hr style="margin:16px 0; border:none; border-top:1px solid #e2e8f0;">
    <form method="post" action="${pageContext.request.contextPath}/profile">
        <input type="hidden" name="action" value="SAVE_URL">
        <div class="form-row">
            <label>Đường dẫn URL ảnh đại diện</label>
            <input id="txtAvatarURL" type="url" name="avatarUrl" value="${avatarURL}" placeholder="https://..." required
                   oninvalid="this.setCustomValidity(this.validity.valueMissing ? 'Vui lòng nhập đường dẫn URL ảnh!' : 'Vui lòng nhập đường dẫn URL hợp lệ (bắt đầu bằng http:// hoặc https://)!')"
                   oninput="this.setCustomValidity('')">
        </div>
        <button id="btnSaveAvatarURL" type="submit" class="btn">Lưu Đường Link Ảnh</button>
    </form>

    <form method="post" action="${pageContext.request.contextPath}/profile" enctype="multipart/form-data" style="margin-top:16px;">
        <input type="hidden" name="action" value="UPLOAD_FILE">
        <div class="form-row">
            <label>Hoặc tải file ảnh từ máy tính (.png, .jpg, .webp)</label>
            <input type="file" name="avatarFile" accept="image/*" required
                   oninvalid="this.setCustomValidity('Vui lòng chọn một tệp ảnh (.png, .jpg, .webp) trước khi tải lên!')"
                   oninput="this.setCustomValidity('')">
        </div>
        <button id="btnUploadFile" type="submit" class="btn btn-secondary">Lưu Ảnh Tải Lên Làm Avatar</button>
    </form>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
