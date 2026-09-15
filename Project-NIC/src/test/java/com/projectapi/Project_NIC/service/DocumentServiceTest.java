package com.projectapi.Project_NIC.service;

import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.dto.response.ArchiveResponse;
import com.projectapi.Project_NIC.dto.response.DocumentResponse;
import com.projectapi.Project_NIC.dto.response.ReviewResponse;
import com.projectapi.Project_NIC.exception.DocumentAlreadyArchivedException;
import com.projectapi.Project_NIC.exception.DocumentNotFoundException;
import com.projectapi.Project_NIC.exception.DuplicateDocumentException;
import com.projectapi.Project_NIC.mapper.DocumentMapper;
import com.projectapi.Project_NIC.model.ArchiveDocument;
import com.projectapi.Project_NIC.model.ClientDocument;
import com.projectapi.Project_NIC.model.Review;
import com.projectapi.Project_NIC.repository.ArchiveRepository;
import com.projectapi.Project_NIC.repository.DocumentRepository;
import com.projectapi.Project_NIC.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ArchiveRepository archiveRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private  PdfService pdfService;

    @Test
    void shouldReturnDocumentWhenDocumentExists() {

        UUID documentId = UUID.randomUUID();

        ClientDocument document = ClientDocument.builder()
                .documentId(documentId)
                .applicationTransactionId(10001L)
                .build();

        DocumentResponse expectedResponse = DocumentResponse.builder()
                .documentId(documentId)
                .applicationTransactionId(10001L)
                .build();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        when(documentMapper.toResponse(document))
                .thenReturn(expectedResponse);

        DocumentResponse actualResponse =
                documentService.getDocumentById(documentId);

        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    void shouldThrowExceptionWhenDocumentDoesNotExist() {

        UUID documentId = UUID.randomUUID();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.getDocumentById(documentId)
                );

        assertEquals(
                "Document not found with id: " + documentId,
                exception.getMessage()
        );
    }

    @Test
    void shouldCreateDocumentSuccessfully() {

        CreateDocumentRequest request = new CreateDocumentRequest();
        request.setApplicationTransactionId(10001L);

        ClientDocument document = ClientDocument.builder()
                .applicationTransactionId(10001L)
                .build();

        when(documentRepository.findByApplicationTransactionId(10001L))
                .thenReturn(Optional.empty());

        when(documentMapper.toEntity(request))
                .thenReturn(document);

        when(documentRepository.save(any(ClientDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID actualDocumentId =
                documentService.saveDocument(request);

        assertNotNull(actualDocumentId);

        assertEquals(
                actualDocumentId,
                document.getDocumentId()
        );

        assertNotNull(document.getCreatedOn());

        verify(documentRepository).save(document);
    }

    @Test
    void shouldThrowExceptionWhenApplicationTransactionIdAlreadyExists() {

        CreateDocumentRequest request = new CreateDocumentRequest();
        request.setApplicationTransactionId(10001L);

        ClientDocument existingDocument = ClientDocument.builder()
                .applicationTransactionId(10001L)
                .build();

        when(documentRepository.findByApplicationTransactionId(10001L))
                .thenReturn(Optional.of(existingDocument));

        DuplicateDocumentException exception =
                assertThrows(
                        DuplicateDocumentException.class,
                        () -> documentService.saveDocument(request)
                );

        assertEquals(
                "Document already exists for application transaction id: 10001",
                exception.getMessage()
        );

        verify(documentRepository, never()).save(any());
    }

    @Test
    void shouldUpdateDocumentSuccessfully() {

        UUID documentId = UUID.randomUUID();

        UpdateDocumentRequest request = new UpdateDocumentRequest();

        ClientDocument existingDocument = ClientDocument.builder()
                .documentId(documentId)
                .applicationTransactionId(10001L)
                .build();

        DocumentResponse expectedResponse = DocumentResponse.builder()
                .documentId(documentId)
                .applicationTransactionId(10001L)
                .build();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.of(existingDocument));

        when(documentRepository.save(existingDocument))
                .thenReturn(existingDocument);

        when(documentMapper.toResponse(existingDocument))
                .thenReturn(expectedResponse);

        DocumentResponse actualResponse =
                documentService.updateDocument(documentId, request);

        assertEquals(expectedResponse, actualResponse);

        verify(documentRepository).findById(documentId);
        verify(documentMapper).updateEntity(existingDocument, request);
        verify(documentRepository).save(existingDocument);
        verify(documentMapper).toResponse(existingDocument);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingDocument() {

        UUID documentId = UUID.randomUUID();

        UpdateDocumentRequest request = new UpdateDocumentRequest();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.updateDocument(documentId, request)
                );

        assertEquals(
                "Document not found with id: " + documentId,
                exception.getMessage()
        );

        verify(documentRepository).findById(documentId);

        verify(documentMapper, never())
                .updateEntity(any(ClientDocument.class), any(UpdateDocumentRequest.class));

        verify(documentRepository, never())
                .save(any(ClientDocument.class));

        verify(documentMapper, never())
                .toResponse(any(ClientDocument.class));
    }


    @Test
    void shouldReturnDocumentsWhenPersonHasDocuments() {

        int personId = 101;

        ClientDocument document1 = ClientDocument.builder()
                .documentId(UUID.randomUUID())
                .applicationTransactionId(10001L)
                .build();

        ClientDocument document2 = ClientDocument.builder()
                .documentId(UUID.randomUUID())
                .applicationTransactionId(10002L)
                .build();

        DocumentResponse response1 = DocumentResponse.builder()
                .documentId(document1.getDocumentId())
                .applicationTransactionId(10001L)
                .build();

        DocumentResponse response2 = DocumentResponse.builder()
                .documentId(document2.getDocumentId())
                .applicationTransactionId(10002L)
                .build();

        when(documentRepository.findByCreatedForPersonId(personId))
                .thenReturn(List.of(document1, document2));

        when(documentMapper.toResponse(document1))
                .thenReturn(response1);

        when(documentMapper.toResponse(document2))
                .thenReturn(response2);

        List<DocumentResponse> actualResponses =
                documentService.getDocumentsByPersonId(personId);

        assertEquals(
                List.of(response1, response2),
                actualResponses
        );

        verify(documentRepository).findByCreatedForPersonId(personId);

        verify(documentMapper).toResponse(document1);

        verify(documentMapper).toResponse(document2);
    }

    @Test
    void shouldReturnEmptyListWhenPersonHasNoDocuments() {

        int personId = 101;

        when(documentRepository.findByCreatedForPersonId(personId))
                .thenReturn(List.of());

        List<DocumentResponse> actualResponses =
                documentService.getDocumentsByPersonId(personId);

        assertTrue(actualResponses.isEmpty());

        verify(documentRepository)
                .findByCreatedForPersonId(personId);

        verifyNoInteractions(documentMapper);
    }

    @Test
    void shouldSaveReviewSuccessfully() {

        long applicationTransactionId = 10001L;

        ReviewRequest request = new ReviewRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setReview("Excellent service");

        ClientDocument document = ClientDocument.builder()
                .applicationTransactionId(applicationTransactionId)
                .build();

        Review savedReview = Review.builder()
                .applicationTransactionId(applicationTransactionId)
                .review("Excellent service")
                .build();

        ReviewResponse expectedResponse = ReviewResponse.builder()
                .applicationTransactionId(applicationTransactionId)
                .review("Excellent service")
                .build();

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.of(document));

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(savedReview);

        when(documentMapper.toReviewResponse(savedReview))
                .thenReturn(expectedResponse);

        ReviewResponse actualResponse =
                documentService.saveOrUpdateReview(request);

        assertEquals(expectedResponse, actualResponse);

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(reviewRepository)
                .save(any(Review.class));

        verify(documentMapper)
                .toReviewResponse(savedReview);
    }

    @Test
    void shouldThrowExceptionWhenReviewDocumentDoesNotExist() {

        long applicationTransactionId = 10001L;

        ReviewRequest request = new ReviewRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setReview("Excellent service");

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.saveOrUpdateReview(request)
                );

        assertEquals(
                "Document not found for application transaction id: "
                        + applicationTransactionId,
                exception.getMessage()
        );

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verifyNoInteractions(reviewRepository);
        verifyNoInteractions(documentMapper);
    }

    @Test
    void shouldArchiveDocumentSuccessfully() {

        Long applicationTransactionId = 10001L;
        UUID documentId = UUID.randomUUID();

        ArchiveDocumentRequest request = new ArchiveDocumentRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setArchivalComments("Document archived successfully");

        ClientDocument existingDocument = ClientDocument.builder()
                .documentId(documentId)
                .applicationTransactionId(applicationTransactionId)
                .createdOn(Instant.now())
                .build();

        ArchiveDocument savedArchive = ArchiveDocument.builder()
                .archiveId(UUID.randomUUID())
                .applicationTransactionId(applicationTransactionId)
                .archivalComments("Document archived successfully")
                .originalDocumentId(documentId)
                .build();

        ArchiveResponse expectedResponse = ArchiveResponse.builder()
                .applicationTransactionId(applicationTransactionId)
                .archivalComments("Document archived successfully")
                .originalDocumentId(documentId)
                .build();

        when(archiveRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.of(existingDocument));

        when(archiveRepository.save(any(ArchiveDocument.class)))
                .thenReturn(savedArchive);

        when(documentMapper.toArchiveResponse(savedArchive))
                .thenReturn(expectedResponse);

        ArchiveResponse actualResponse =
                documentService.archiveDocument(request);

        assertEquals(expectedResponse, actualResponse);

        verify(archiveRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(documentRepository)
                .deleteById(documentId);

        verify(archiveRepository)
                .save(any(ArchiveDocument.class));

        verify(documentMapper)
                .toArchiveResponse(savedArchive);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsAlreadyArchived() {

        Long applicationTransactionId = 10001L;

        ArchiveDocumentRequest request = new ArchiveDocumentRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setArchivalComments("Archive again");

        ArchiveDocument existingArchive = ArchiveDocument.builder()
                .applicationTransactionId(applicationTransactionId)
                .build();

        when(archiveRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.of(existingArchive));

        DocumentAlreadyArchivedException exception =
                assertThrows(
                        DocumentAlreadyArchivedException.class,
                        () -> documentService.archiveDocument(request)
                );

        assertEquals(
                "Document is already archived for application transaction id: "
                        + applicationTransactionId,
                exception.getMessage()
        );

        verify(archiveRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verifyNoInteractions(documentRepository);
        verifyNoInteractions(documentMapper);
    }


    @Test
    void shouldThrowExceptionWhenDocumentDoesNotExistForArchiving() {

        long applicationTransactionId = 10001L;

        ArchiveDocumentRequest request = new ArchiveDocumentRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setArchivalComments("Archive document");

        when(archiveRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.archiveDocument(request)
                );

        assertEquals(
                "Document not found for application transaction id: "
                        + applicationTransactionId,
                exception.getMessage()
        );

        verify(archiveRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(documentRepository, never())
                .deleteById(any(UUID.class));

        verify(archiveRepository, never())
                .save(any(ArchiveDocument.class));

        verifyNoInteractions(documentMapper);
    }

    @Test
    void shouldAddWatermarkSuccessfully() {

        long applicationTransactionId = 10001L;

        WatermarkRequest request = new WatermarkRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setWatermark("CONFIDENTIAL");

        ClientDocument.DocumentContent documentContent =
                ClientDocument.DocumentContent.builder()
                        .actualDocumentBase64("original-pdf-base64")
                        .build();

        ClientDocument clientDocument = ClientDocument.builder()
                .documentId(UUID.randomUUID())
                .applicationTransactionId(applicationTransactionId)
                .document(documentContent)
                .build();

        String watermarkedPdf = "watermarked-pdf-base64";

        DocumentResponse expectedResponse = DocumentResponse.builder()
                .documentId(clientDocument.getDocumentId())
                .applicationTransactionId(applicationTransactionId)
                .build();

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.of(clientDocument));

        when(pdfService.addWatermark(
                "original-pdf-base64",
                "CONFIDENTIAL"))
                .thenReturn(watermarkedPdf);

        when(documentRepository.save(clientDocument))
                .thenReturn(clientDocument);

        when(documentMapper.toResponse(clientDocument))
                .thenReturn(expectedResponse);

        DocumentResponse actualResponse =
                documentService.addWatermarkToDocument(request);

        assertEquals(expectedResponse, actualResponse);

        assertEquals(
                watermarkedPdf,
                clientDocument.getDocument().getActualDocumentBase64()
        );

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(pdfService)
                .addWatermark(
                        "original-pdf-base64",
                        "CONFIDENTIAL"
                );

        verify(documentRepository).save(clientDocument);

        verify(documentMapper).toResponse(clientDocument);
    }

    @Test
    void shouldThrowExceptionWhenDocumentDoesNotExistForWatermark() {

        long applicationTransactionId = 10001L;

        WatermarkRequest request = new WatermarkRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setWatermark("CONFIDENTIAL");

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.addWatermarkToDocument(request)
                );

        assertEquals(
                "Document not found for application transaction id: "
                        + applicationTransactionId,
                exception.getMessage()
        );

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verifyNoInteractions(pdfService);
        verifyNoInteractions(documentMapper);

        verify(documentRepository, never())
                .save(any(ClientDocument.class));
    }


    @Test
    void shouldAddPasswordSuccessfully() {

        long applicationTransactionId = 10001L;

        PdfPasswordRequest request = new PdfPasswordRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setPassword("securePassword123");

        ClientDocument.DocumentContent documentContent =
                ClientDocument.DocumentContent.builder()
                        .actualDocumentBase64("original-pdf-base64")
                        .build();

        ClientDocument clientDocument = ClientDocument.builder()
                .documentId(UUID.randomUUID())
                .applicationTransactionId(applicationTransactionId)
                .document(documentContent)
                .build();

        String protectedPdf = "password-protected-pdf-base64";

        DocumentResponse expectedResponse = DocumentResponse.builder()
                .documentId(clientDocument.getDocumentId())
                .applicationTransactionId(applicationTransactionId)
                .build();

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.of(clientDocument));

        when(pdfService.addPassword(
                "original-pdf-base64",
                "securePassword123"))
                .thenReturn(protectedPdf);

        when(documentRepository.save(clientDocument))
                .thenReturn(clientDocument);

        when(documentMapper.toResponse(clientDocument))
                .thenReturn(expectedResponse);

        DocumentResponse actualResponse =
                documentService.addPasswordToPdf(request);

        assertEquals(expectedResponse, actualResponse);

        assertEquals(
                protectedPdf,
                clientDocument.getDocument().getActualDocumentBase64()
        );

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verify(pdfService)
                .addPassword(
                        "original-pdf-base64",
                        "securePassword123"
                );

        verify(documentRepository).save(clientDocument);

        verify(documentMapper).toResponse(clientDocument);
    }


    @Test
    void shouldThrowExceptionWhenDocumentDoesNotExistForPasswordProtection() {

        long applicationTransactionId = 10001L;

        PdfPasswordRequest request = new PdfPasswordRequest();
        request.setApplicationTransactionId(applicationTransactionId);
        request.setPassword("securePassword123");

        when(documentRepository.findByApplicationTransactionId(
                applicationTransactionId))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> documentService.addPasswordToPdf(request)
                );

        assertEquals(
                "Document not found for application transaction id: "
                        + applicationTransactionId,
                exception.getMessage()
        );

        verify(documentRepository)
                .findByApplicationTransactionId(applicationTransactionId);

        verifyNoInteractions(pdfService);
        verifyNoInteractions(documentMapper);

        verify(documentRepository, never()).save(any(ClientDocument.class));
    }

}


