package com.projectapi.Project_NIC.service;

import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.exception.DocumentNotFoundException;
import com.projectapi.Project_NIC.exception.DocumentProcessingException;
import com.projectapi.Project_NIC.model.ArchiveDocument;
import com.projectapi.Project_NIC.model.ClientDocument;

import com.projectapi.Project_NIC.model.Review;
import com.projectapi.Project_NIC.repository.ArchiveRepository;
import com.projectapi.Project_NIC.repository.DocumentRepository;
import com.projectapi.Project_NIC.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MongoTemplate mongoTemplate;
    private final ReviewRepository reviewRepository;
    private final ArchiveRepository archiveRepository;

    private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());



    public UUID saveDocument(CreateDocumentRequest request) {

        ClientDocument document = new ClientDocument();

        document.setDocument_id(UUID.randomUUID());
        document.setCreated_on(new Date());

        document.setApplication(request.getApplication());
        document.setModule(request.getModule());
        document.setWorkflow(request.getWorkflow());
        document.setFile_information(request.getFileInformation());
        document.setCreated_by(request.getCreatedBy());
        document.setCreated_for(request.getCreatedFor());
        document.setDocument(request.getDocument());
        document.setAdditional_info_1(request.getAdditionalInfo1());
        document.setAdditional_info_2(request.getAdditionalInfo2());

        documentRepository.save(document);

        return document.getDocument_id();
    }

    public ClientDocument updateDocument(
            UUID documentId,
            UpdateDocumentRequest request) {

        ClientDocument existingDocument =
                documentRepository.findById(documentId)
                        .orElseThrow(() ->
                                new DocumentNotFoundException(
                                        "Document not found with id: "
                                                + documentId
                                )
                        );


        existingDocument.setApplication(request.getApplication());
        existingDocument.setModule(request.getModule());
        existingDocument.setWorkflow(request.getWorkflow());
        existingDocument.setFile_information(request.getFileInformation());
        existingDocument.setCreated_by(request.getCreatedBy());
        existingDocument.setCreated_for(request.getCreatedFor());
        existingDocument.setDocument(request.getDocument());
        existingDocument.setAdditional_info_1(request.getAdditionalInfo1());
        existingDocument.setAdditional_info_2(request.getAdditionalInfo2());

        return documentRepository.save(existingDocument);
    }

    public ClientDocument getDocumentById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found with id: " + documentId
                        )
                );

    }

    public List<ClientDocument> getDocumentsByPersonId(int personId) {
//        System.out.println("searching for document with personId: " + personId);

        return documentRepository.findByPersonId(personId);

    }

    public Optional<ClientDocument> getDocumentByApplicationTransactionId(
            long applicationTransactionId) {

        return documentRepository.findByApplicationTransactionId(
                applicationTransactionId
        );
    }

    public Review saveOrUpdateReview(Review review) {
        Optional<Review> existingReview = reviewRepository.findByApplicationTransactionId(review.getApplication_transaction_id());

        if (existingReview.isPresent()) {
            Review existing = existingReview.get();
            existing.setReview(review.getReview());

            return reviewRepository.save(existing);
        }

        return reviewRepository.save(review);
    }

    public Review createOrUpdateReview(ReviewRequest request) {

        Optional<ClientDocument> clientDocument =
                documentRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                );

        if (clientDocument.isEmpty()) {
            throw new DocumentNotFoundException(
                    "Document not found for application transaction id: "
                            + request.getApplicationTransactionId()
            );
        }

        Review review = new Review();

        review.setApplication_transaction_id(
                clientDocument.get()
                        .getFile_information()
                        .getApplication_transaction_id()
        );

        review.setReview(request.getReview());

        return saveOrUpdateReview(review);
    }

    public ArchiveDocument archiveDocument(ArchiveDocumentRequest request) {

        Optional<ArchiveDocument> existingArchive = archiveRepository.findByApplicationTransactionId(request.getApplicationTransactionId());
        Optional<ClientDocument> existingDocument = documentRepository.findByApplicationTransactionId(request.getApplicationTransactionId());

        if (existingArchive.isPresent()) {

            ArchiveDocument archiveDocument = existingArchive.get();

            archiveDocument.setArchival_comments(
                    request.getArchivalComments()
            );

            return archiveRepository.save(archiveDocument);
        }

        existingDocument.ifPresent(document ->
                documentRepository.deleteById(document.getDocument_id())
        );

        ArchiveDocument archiveDocument = new ArchiveDocument();

        archiveDocument.setApplication_transaction_id(
                request.getApplicationTransactionId()
        );

        archiveDocument.setArchival_comments(
                request.getArchivalComments()
        );

        return archiveRepository.save(archiveDocument);
    }

    public ArchiveDocument createArchive(ArchiveDocumentRequest request) {

        Optional<ClientDocument> clientDocument =
                documentRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                );

        if (clientDocument.isEmpty()) {
            throw new DocumentNotFoundException(
                    "Document not found for application transaction id: "
                            + request.getApplicationTransactionId()
            );
        }

        return archiveDocument(request);
    }

    public void deleteDocumentById(UUID documentId) {
        documentRepository.deleteById(documentId);
    }
    public ClientDocument updateDocument(ClientDocument document) {
        Date date = new Date();
        document.setCreated_on(date);
        return documentRepository.save(document);
    }

    public ClientDocument addWatermarkToDocument(long applicationTransactionId, String watermark) {
        Optional<ClientDocument> existingDocument = documentRepository.findByApplicationTransactionId(applicationTransactionId);

        if (existingDocument.isEmpty()) {
            throw new DocumentNotFoundException(
                    "Document not found for application transaction id: "
                            + applicationTransactionId
            );
        }

        ClientDocument clientDocument = existingDocument.get();

        try{
            byte[] pdfBytes = Base64.getDecoder().decode(clientDocument.getDocument().getActual_document_base_64());

            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            //loop to add watermark to each page
            for(PDPage page : document.getPages()){
                PDRectangle pageSize = page.getMediaBox();
                float pageWidth = pageSize.getWidth();
                float pageHeight = pageSize.getHeight();

                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, true, true);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 70);
                contentStream.setNonStrokingColor(200, 200, 200);    //Light Grey colour


                float stringWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(watermark) / 1000 * 50;
                float stringHeight = PDType1Font.HELVETICA_BOLD.getFontDescriptor().getCapHeight() / 1000 * 50;

                //calculating middle coordinates
                float centerX = (pageWidth - stringWidth) / 2;
                float centerY = (pageHeight - stringHeight) / 3;

                contentStream.beginText();
                contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(45), centerX, centerY));  // adjust the position and angle as required
                contentStream.showText(watermark);
                contentStream.endText();
                contentStream.close();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();

            String base64WatermarkedPdf = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            clientDocument.getDocument().setActual_document_base_64(base64WatermarkedPdf);

            documentRepository.deleteById(existingDocument.get().getDocument_id());
            documentRepository.save(clientDocument);

            return clientDocument;
        } catch (IOException e){
            throw new DocumentProcessingException(
                    "Failed to process PDF document",
                    e
            );
        }

    }





    public Optional<Review> viewReviewLog(long applicationTransactionId) {
        return reviewRepository.findByApplicationTransactionId(applicationTransactionId);
    }



    public Optional<ArchiveDocument> viewEditLog(long applicationTransactionId) {
        return archiveRepository.findByApplicationTransactionId(applicationTransactionId);
    }


    public String addPasswordToPdf(PdfPasswordRequest request) {
        Optional<ClientDocument> existingDocument = documentRepository.findByApplicationTransactionId(request.getApplicationTransactionId());

        if (existingDocument.isEmpty()) {
            throw new DocumentNotFoundException(
                    "Document not found for application transaction id: "
                            + request.getApplicationTransactionId()
            );
        }

        ClientDocument clientDocument = existingDocument.get();

        try{
            byte[] pdfBytes = Base64.getDecoder().decode(clientDocument.getDocument().getActual_document_base_64());

            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            // Set the password protection
            AccessPermission accessPermission = new AccessPermission();
            StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(
                    request.getPassword(), request.getPassword(), accessPermission);

            // Customize the protection policy if necessary
            protectionPolicy.setEncryptionKeyLength(128);  // 128-bit key length
            protectionPolicy.setPermissions(accessPermission);
            document.protect(protectionPolicy);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();

            String base64PdfWithPassword = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            // Update the ClientDocument with the new Base64 content
            clientDocument.getDocument().setActual_document_base_64(base64PdfWithPassword);

            // Save the updated ClientDocument
            documentRepository.save(clientDocument);

            return base64PdfWithPassword;
        } catch (IOException e){
            throw new DocumentProcessingException(
                    "Failed to process PDF document",
                    e
            );
        }
    }


}