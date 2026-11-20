package com.ptconnect.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PdfUtil {
    private PdfUtil() {}

    private static BaseFont loadFont(String resourcePath) throws Exception {
        try (InputStream is = PdfUtil.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Không tìm thấy font: " + resourcePath);
            }
            byte[] bytes = is.readAllBytes();
            return BaseFont.createFont(resourcePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
        }
    }

    public static byte[] generateContractPdf(
            Long enrollmentId,
            String studentName,
            String ptName,
            String courseTitle,
            String trainingLocation,
            Integer totalSessions,
            BigDecimal finalPrice,
            LocalDate startDate,
            LocalDate endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 48, 48, 42, 42);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            BaseFont bfReg = loadFont("/fonts/LiberationSerif-Regular.ttf");
            BaseFont bfBold = loadFont("/fonts/LiberationSerif-Bold.ttf");
            BaseFont bfItalic = loadFont("/fonts/LiberationSerif-Italic.ttf");

            Font fHeader = new Font(bfBold, 11.5f, Font.NORMAL, new Color(15, 23, 42));
            Font fTitle = new Font(bfBold, 15f, Font.NORMAL, new Color(30, 58, 138));
            Font fSub = new Font(bfItalic, 10.5f, Font.NORMAL, new Color(71, 85, 105));
            Font fSection = new Font(bfBold, 11.5f, Font.NORMAL, new Color(15, 23, 42));
            Font fBody = new Font(bfReg, 10.5f, Font.NORMAL, new Color(30, 41, 59));
            Font fBodyBold = new Font(bfBold, 10.5f, Font.NORMAL, new Color(15, 23, 42));
            Font fTableHead = new Font(bfBold, 10f, Font.NORMAL, new Color(30, 58, 138));

            // 1. Quốc hiệu & Tiêu ngữ
            Paragraph quocHieu = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", fHeader);
            quocHieu.setAlignment(Element.ALIGN_CENTER);
            doc.add(quocHieu);

            Paragraph tieuNgu = new Paragraph("Độc lập - Tự do - Hạnh phúc", fBodyBold);
            tieuNgu.setAlignment(Element.ALIGN_CENTER);
            doc.add(tieuNgu);

            Paragraph divider = new Paragraph("—————o0o—————", fSub);
            divider.setAlignment(Element.ALIGN_CENTER);
            divider.setSpacingAfter(14f);
            doc.add(divider);

            // 2. Tên hợp đồng & Số hợp đồng
            Paragraph title = new Paragraph("HỢP ĐỒNG DỊCH VỤ HUẤN LUYỆN CÁ NHÂN", fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            String contractCode = "PTC-HD-" + String.format("%06d", enrollmentId);
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String startStr = startDate != null ? startDate.format(dateFmt) : "";
            String endStr = endDate != null ? endDate.format(dateFmt) : "";

            Paragraph subCode = new Paragraph("Số: " + contractCode + "   |   Ngày hiệu lực: " + startStr, fSub);
            subCode.setAlignment(Element.ALIGN_CENTER);
            subCode.setSpacingAfter(14f);
            doc.add(subCode);

            // 3. Thông tin các bên
            Paragraph canCu = new Paragraph(
                    "Căn cứ Bộ luật Dân sự nước Cộng hòa Xã hội Chủ nghĩa Việt Nam và Quy chế hoạt động của nền tảng PT-Connect, hai bên thống nhất ký kết hợp đồng dịch vụ huấn luyện cá nhân với các nội dung sau:",
                    fSub);
            canCu.setSpacingAfter(10f);
            doc.add(canCu);

            PdfPTable partyTable = new PdfPTable(2);
            partyTable.setWidthPercentage(100);
            partyTable.setSpacingAfter(14f);
            partyTable.addCell(buildPartyCell("BÊN A (HỌC VIÊN)", studentName, "Người sử dụng dịch vụ trên PT-Connect", fBodyBold, fBody));
            partyTable.addCell(buildPartyCell("BÊN B (HUẤN LUYỆN VIÊN)", ptName, "Huấn luyện viên đã xác thực (VERIFIED)", fBodyBold, fBody));
            doc.add(partyTable);

            // 4. Điều 1: Nội dung gói dịch vụ (Bảng chi tiết)
            Paragraph d1 = new Paragraph("ĐIỀU 1. NỘI DUNG GÓI DỊCH VỤ VÀ HỌC PHÍ", fSection);
            d1.setSpacingAfter(6f);
            doc.add(d1);

            PdfPTable detailTable = new PdfPTable(new float[]{3.2f, 6.8f});
            detailTable.setWidthPercentage(100);
            detailTable.setSpacingAfter(14f);
            addDetailRow(detailTable, "Hạng mục", "Thông tin chi tiết", fTableHead, fTableHead, new Color(239, 246, 255));
            addDetailRow(detailTable, "Tên khóa học", safe(courseTitle), fBodyBold, fBody, Color.WHITE);
            addDetailRow(detailTable, "Địa điểm tập cố định", safe(trainingLocation), fBodyBold, fBody, new Color(248, 250, 252));
            addDetailRow(detailTable, "Tổng số buổi tập", (totalSessions != null ? totalSessions : 0) + " buổi", fBodyBold, fBody, Color.WHITE);
            addDetailRow(detailTable, "Thời hạn sử dụng", "Từ ngày " + startStr + " đến ngày " + endStr, fBodyBold, fBody, new Color(248, 250, 252));
            addDetailRow(detailTable, "Học phí đã thanh toán", formatVnd(finalPrice) + " VNĐ (Thanh toán qua Ví PT-Connect)", fBodyBold, fBodyBold, Color.WHITE);
            doc.add(detailTable);

            // 5. Điều 2: Quyền và nghĩa vụ
            Paragraph d2 = new Paragraph("ĐIỀU 2. QUYỀN VÀ NGHĨA VỤ CỦA CÁC BÊN", fSection);
            d2.setSpacingAfter(4f);
            doc.add(d2);
            doc.add(bullet("Bên A có quyền được hướng dẫn chuyên môn, nhận giáo án tập luyện và đặt lịch trong khung giờ rảnh của Bên B tại địa điểm cố định đã chọn.", fBody));
            doc.add(bullet("Bên B có trách nhiệm chuẩn bị giáo án phù hợp, có mặt đúng giờ, đảm bảo an toàn tập luyện và bảo mật thông tin cá nhân của Bên A.", fBody));

            // 6. Điều 3: Chính sách hủy lịch & Điểm danh OTP
            Paragraph d3 = new Paragraph("ĐIỀU 3. CHÍNH SÁCH HỦY LỊCH, ĐỀN BÙ VÀ ĐIỂM DANH OTP", fSection);
            d3.setSpacingBefore(8f);
            d3.setSpacingAfter(4f);
            doc.add(d3);
            doc.add(bullet("Điểm danh hai phía: Mỗi buổi tập được xác thực bằng mã OTP 6 chữ số (có hiệu lực trong 30 phút kể từ khi Bên B mở điểm danh).", fBody));
            doc.add(bullet("Bên A hủy lịch: Ca đang chờ duyệt (PENDING) hoặc hủy trước giờ tập >= 24 giờ được hoàn lại 1 buổi vào hợp đồng; hủy ca đã xác nhận (CONFIRMED) dưới 24 giờ không được hoàn buổi và đền bù 50% giá trị buổi tập cho Bên B.", fBody));
            doc.add(bullet("Bên B hủy lịch: Hoàn lại 1 buổi vào hợp đồng cho Bên A; nếu Bên B hủy ca CONFIRMED dưới 24 giờ sẽ bị phạt 20% giá trị buổi tập để đền bù vào ví Bên A.", fBody));

            // 7. Điều 4 & Chữ ký điện tử
            Paragraph d4 = new Paragraph("ĐIỀU 4. HIỆU LỰC HỢP ĐỒNG", fSection);
            d4.setSpacingBefore(8f);
            d4.setSpacingAfter(4f);
            doc.add(d4);
            Paragraph p4 = new Paragraph(
                    "Hợp đồng điện tử này có giá trị pháp lý trên nền tảng PT-Connect kể từ thời điểm giao dịch thanh toán được hệ thống xác nhận thành công.",
                    fBody);
            p4.setSpacingAfter(18f);
            doc.add(p4);

            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.addCell(buildSignCell("ĐẠI DIỆN BÊN A (HỌC VIÊN)", studentName, "(Đã xác nhận thanh toán điện tử)", fBodyBold, fSub));
            signTable.addCell(buildSignCell("ĐẠI DIỆN BÊN B (HUẤN LUYỆN VIÊN)", ptName, "(Đã xác nhận cung cấp khóa học)", fBodyBold, fSub));
            doc.add(signTable);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo file hợp đồng PDF", e);
        }
    }

    private static PdfPCell buildPartyCell(String roleTitle, String name, String note, Font fBold, Font fBody) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8f);
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setBackgroundColor(new Color(248, 250, 252));
        cell.addElement(new Paragraph(roleTitle, fBold));
        cell.addElement(new Paragraph("Họ và tên: " + safe(name), fBody));
        cell.addElement(new Paragraph("Vai trò: " + note, fBody));
        return cell;
    }

    private static void addDetailRow(PdfPTable table, String col1, String col2, Font f1, Font f2, Color bg) {
        PdfPCell c1 = new PdfPCell(new Phrase(col1, f1));
        c1.setPadding(7f);
        c1.setBackgroundColor(bg);
        c1.setBorderColor(new Color(203, 213, 225));

        PdfPCell c2 = new PdfPCell(new Phrase(col2, f2));
        c2.setPadding(7f);
        c2.setBackgroundColor(bg);
        c2.setBorderColor(new Color(203, 213, 225));

        table.addCell(c1);
        table.addCell(c2);
    }

    private static Paragraph bullet(String text, Font f) {
        Paragraph p = new Paragraph("•  " + text, f);
        p.setIndentationLeft(10f);
        p.setSpacingAfter(3f);
        return p;
    }

    private static PdfPCell buildSignCell(String title, String name, String status, Font fBold, Font fSub) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph p1 = new Paragraph(title, fBold);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph(status, fSub);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setSpacingBefore(4f);
        p2.setSpacingAfter(22f);
        Paragraph p3 = new Paragraph(safe(name), fBold);
        p3.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p1);
        cell.addElement(p2);
        cell.addElement(p3);
        return cell;
    }

    private static String formatVnd(BigDecimal amount) {
        if (amount == null) return "0";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(amount);
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
