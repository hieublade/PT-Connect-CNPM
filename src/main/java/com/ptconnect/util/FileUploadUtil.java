package com.ptconnect.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class FileUploadUtil {
    private FileUploadUtil() {}

    public static String saveUploadedFile(Part filePart, ServletContext context, String subFolder) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        String submitted = filePart.getSubmittedFileName();
        String ext = ".png";
        if (submitted != null && submitted.contains(".")) {
            ext = submitted.substring(submitted.lastIndexOf(".")).toLowerCase();
        }
        if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".webp")) {
            throw new IllegalArgumentException("Chỉ chấp nhận tệp ảnh .png, .jpg, .jpeg, .webp");
        }

        String realBase = context.getRealPath("/uploads/" + subFolder);
        if (realBase == null) {
            realBase = "src/main/webapp/uploads" + File.separator + subFolder;
        }
        Path dir = Path.of(realBase);
        Files.createDirectories(dir);

        String fileName = UUID.randomUUID().toString() + ext;
        Path dest = dir.resolve(fileName);
        try (InputStream is = filePart.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        return context.getContextPath() + "/uploads/" + subFolder + "/" + fileName;
    }
}
