package com.projectapi.Project_NIC.service;


import com.projectapi.Project_NIC.exception.DocumentProcessingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class PdfService {

    public String addPassword(String base64Pdf, String password) {

        try {
            byte[] pdfBytes =
                    Base64.getDecoder().decode(base64Pdf);

            try (PDDocument document =
                         PDDocument.load(
                                 new ByteArrayInputStream(pdfBytes))) {

                AccessPermission accessPermission = new AccessPermission();

                StandardProtectionPolicy protectionPolicy =
                        new StandardProtectionPolicy(
                                password,
                                password,
                                accessPermission
                        );

                protectionPolicy.setEncryptionKeyLength(128);

                protectionPolicy.setPermissions(
                        accessPermission
                );

                document.protect(protectionPolicy);

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream();

                document.save(outputStream);

                return Base64.getEncoder()
                        .encodeToString(
                                outputStream.toByteArray()
                        );
            }

        } catch (IllegalArgumentException exception) {

            throw new DocumentProcessingException(
                    "Document contains invalid Base64 PDF data",
                    exception
            );

        } catch (IOException exception) {

            throw new DocumentProcessingException(
                    "Failed to add password to PDF",
                    exception
            );
        }
    }


    public String addWatermark(
            String base64Pdf,
            String watermark) {

        try {
            byte[] pdfBytes =
                    Base64.getDecoder().decode(base64Pdf);

            try (PDDocument document =
                         PDDocument.load(
                                 new ByteArrayInputStream(pdfBytes))) {

                for (PDPage page : document.getPages()) {

                    PDRectangle pageSize =
                            page.getMediaBox();

                    float pageWidth =
                            pageSize.getWidth();

                    float pageHeight =
                            pageSize.getHeight();

                    try (PDPageContentStream contentStream =
                                 new PDPageContentStream(
                                         document,
                                         page,
                                         PDPageContentStream.AppendMode.PREPEND,
                                         true,
                                         true)) {

                        contentStream.setFont(
                                PDType1Font.HELVETICA_BOLD,
                                50
                        );

                        contentStream.setNonStrokingColor(
                                200,
                                200,
                                200
                        );

                        float stringWidth =
                                PDType1Font.HELVETICA_BOLD
                                        .getStringWidth(watermark)
                                        / 1000 * 50;

                        float stringHeight =
                                PDType1Font.HELVETICA_BOLD
                                        .getFontDescriptor()
                                        .getCapHeight()
                                        / 1000 * 50;

                        float centerX =
                                (pageWidth - stringWidth) / 2;

                        float centerY =
                                (pageHeight - stringHeight) / 3;

                        contentStream.beginText();

                        contentStream.setTextMatrix(
                                Matrix.getRotateInstance(
                                        Math.toRadians(45),
                                        centerX,
                                        centerY
                                )
                        );

                        contentStream.showText(watermark);

                        contentStream.endText();
                    }
                }

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream();

                document.save(outputStream);

                return Base64.getEncoder()
                        .encodeToString(
                                outputStream.toByteArray()
                        );
            }

        } catch (IllegalArgumentException exception) {

            throw new DocumentProcessingException(
                    "Document contains invalid Base64 PDF data",
                    exception
            );

        } catch (IOException exception) {

            throw new DocumentProcessingException(
                    "Failed to add watermark to PDF",
                    exception
            );
        }
    }
}
