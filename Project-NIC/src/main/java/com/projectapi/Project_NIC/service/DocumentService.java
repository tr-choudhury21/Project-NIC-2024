package com.projectapi.Project_NIC.service;

import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.dto.response.DocumentResponse;
import com.projectapi.Project_NIC.exception.DocumentNotFoundException;
import com.projectapi.Project_NIC.exception.DocumentProcessingException;
import com.projectapi.Project_NIC.mapper.DocumentMapper;
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
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MongoTemplate mongoTemplate;
    private final ReviewRepository reviewRepository;
    private final ArchiveRepository archiveRepository;

    private final PdfService pdfService;
    private final DocumentMapper documentMapper;


    private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());



    public UUID saveDocument(CreateDocumentRequest request) {

        ClientDocument document = documentMapper.toEntity(request);

        document.setDocumentId(UUID.randomUUID());
        document.setCreatedOn(Instant.now());

        documentRepository.save(document);

        return document.getDocumentId();
    }

    public DocumentResponse updateDocument(
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


        documentMapper.updateEntity(existingDocument, request);

        ClientDocument updatedDocument = documentRepository.save(existingDocument);

        return documentMapper.toResponse(updatedDocument);
    }

    public DocumentResponse getDocumentById(UUID documentId) {
        ClientDocument document = documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found with id: " + documentId
                        )
                );

        return documentMapper.toResponse(document);

    }

    public List<DocumentResponse> getDocumentsByPersonId(int personId) {
//        System.out.println("searching for document with personId: " + personId);

        List<ClientDocument> documents = documentRepository.findByCreatedForPersonId(personId);

        return documents.stream()
                .map(documentMapper::toResponse)
                .toList();

    }

    public Optional<ClientDocument> getDocumentByApplicationTransactionId(
            long applicationTransactionId) {

        return documentRepository.findByApplicationTransactionId(
                applicationTransactionId
        );
    }

    public Review saveOrUpdateReview(ReviewRequest request) {

                documentRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                ).orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found for application transaction id: "
                                        + request.getApplicationTransactionId()
                        )
                );

        Optional<Review> existingReview =
                reviewRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                );

        if (existingReview.isPresent()) {

            Review existing = existingReview.get();
            existing.setReview(request.getReview());

            return reviewRepository.save(existing);
        }

        Review review = Review.builder()
                .applicationTransactionId(
                        request.getApplicationTransactionId()
                )
                .review(request.getReview())
                .build();

        return reviewRepository.save(review);
    }


    public ArchiveDocument archiveDocument(ArchiveDocumentRequest request) {

        Optional<ArchiveDocument> existingArchive = archiveRepository.findByApplicationTransactionId(request.getApplicationTransactionId());
        ClientDocument existingDocument = documentRepository.findByApplicationTransactionId(request.getApplicationTransactionId()).orElseThrow(() ->
                new DocumentNotFoundException(
                        "Document not found for application transaction id: "
                                + request.getApplicationTransactionId()
                )
        );

        if (existingArchive.isPresent()) {

            ArchiveDocument archiveDocument = existingArchive.get();

            archiveDocument.setArchivalComments(
                    request.getArchivalComments()
            );

            return archiveRepository.save(archiveDocument);
        }


        ArchiveDocument archive = ArchiveDocument.builder()
                .applicationTransactionId(
                        request.getApplicationTransactionId()
                )
                .archivalComments(
                        request.getArchivalComments()
                )
                .build();

        documentRepository.deleteById(
                existingDocument.getDocumentId()
        );

        return archiveRepository.save(archive);
    }

//    public void deleteDocumentById(UUID documentId) {
//        documentRepository.deleteById(documentId);
//    }
////    public ClientDocument updateDocument(ClientDocument document) {
////
////        document.setCreatedOn(Instant.now());
////        return documentRepository.save(document);
////    }

    public DocumentResponse addWatermarkToDocument(
            WatermarkRequest request) {

        ClientDocument clientDocument =
                documentRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                ).orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found for application transaction id: "
                                        + request.getApplicationTransactionId()
                        )
                );

        String watermarkedPdf =
                pdfService.addWatermark(
                        clientDocument
                                .getDocument()
                                .getActualDocumentBase64(),
                        request.getWatermark()
                );

        clientDocument
                .getDocument()
                .setActualDocumentBase64(
                        watermarkedPdf
                );

        ClientDocument updatedDocument =
                documentRepository.save(clientDocument);

        return documentMapper.toResponse(updatedDocument);
    }



    public Optional<Review> viewReviewLog(long applicationTransactionId) {
        return reviewRepository.findByApplicationTransactionId(applicationTransactionId);
    }



    public Optional<ArchiveDocument> viewEditLog(long applicationTransactionId) {
        return archiveRepository.findByApplicationTransactionId(applicationTransactionId);
    }


    public String addPasswordToPdf(PdfPasswordRequest request) {

        ClientDocument clientDocument =
                documentRepository.findByApplicationTransactionId(
                        request.getApplicationTransactionId()
                ).orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found for application transaction id: "
                                        + request.getApplicationTransactionId()
                        )
                );

        String protectedPdf =
                pdfService.addPassword(
                        clientDocument
                                .getDocument()
                                .getActualDocumentBase64(),
                        request.getPassword()
                );

        clientDocument
                .getDocument()
                .setActualDocumentBase64(
                        protectedPdf
                );

        documentRepository.save(clientDocument);

        return protectedPdf;
    }


}