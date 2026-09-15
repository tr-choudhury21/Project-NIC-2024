package com.projectapi.Project_NIC;

import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.dto.response.ArchiveResponse;
import com.projectapi.Project_NIC.dto.response.DocumentResponse;
import com.projectapi.Project_NIC.dto.response.ReviewResponse;
import com.projectapi.Project_NIC.mapper.DocumentMapper;
import com.projectapi.Project_NIC.model.ArchiveDocument;
import com.projectapi.Project_NIC.model.ClientDocument;
import com.projectapi.Project_NIC.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentMapperTest {

    private DocumentMapper documentMapper;

    @BeforeEach
    void setUp() {
        documentMapper = new DocumentMapper();
    }

    @Test
    void shouldMapCreateDocumentRequestToEntity() {

        ApplicationRequest applicationRequest =
                ApplicationRequest.builder()
                        .applicationId("APP-101")
                        .applicationName("Income Certificate")
                        .build();

        CreatedByRequest createdByRequest =
                CreatedByRequest.builder()
                        .employeeCode("EMP-001")
                        .employeeName("John Doe")
                        .designation("Officer")
                        .organization("NIC")
                        .build();

        CreatedForRequest createdForRequest =
                CreatedForRequest.builder()
                        .personId(101)
                        .name("Titu")
                        .gender("Male")
                        .age(22)
                        .mobileNumber(9876543210L)
                        .build();

        DocumentContentRequest documentContentRequest =
                DocumentContentRequest.builder()
                        .actualDocumentBase64("base64-document-data")
                        .build();

        CreateDocumentRequest request =
                CreateDocumentRequest.builder()
                        .applicationTransactionId(10001L)
                        .application(applicationRequest)
                        .createdBy(createdByRequest)
                        .createdFor(createdForRequest)
                        .document(documentContentRequest)
                        .build();

        ClientDocument result =
                documentMapper.toEntity(request);

        assertEquals(
                10001L,
                result.getApplicationTransactionId()
        );

        assertEquals(
                "APP-101",
                result.getApplication().getApplicationId()
        );

        assertEquals(
                "Income Certificate",
                result.getApplication().getApplicationName()
        );

        assertEquals(
                "EMP-001",
                result.getCreatedBy().getEmployeeCode()
        );

        assertEquals(
                "John Doe",
                result.getCreatedBy().getEmployeeName()
        );

        assertEquals(
                "Officer",
                result.getCreatedBy().getDesignation()
        );

        assertEquals(
                "NIC",
                result.getCreatedBy().getOrganization()
        );

        assertEquals(
                101,
                result.getCreatedFor().getPersonId()
        );

        assertEquals(
                "Titu",
                result.getCreatedFor().getName()
        );

        assertEquals(
                "Male",
                result.getCreatedFor().getGender()
        );

        assertEquals(
                22,
                result.getCreatedFor().getAge()
        );

        assertEquals(
                9876543210L,
                result.getCreatedFor().getMobileNumber()
        );

        assertEquals(
                "base64-document-data",
                result.getDocument().getActualDocumentBase64()
        );
    }

    @Test
    void shouldMapEntityToDocumentResponse() {

        UUID documentId = UUID.randomUUID();
        Instant createdOn = Instant.now();

        ClientDocument.Application application =
                ClientDocument.Application.builder()
                        .applicationId("APP-101")
                        .applicationName("Income Certificate")
                        .build();

        ClientDocument.CreatedBy createdBy =
                ClientDocument.CreatedBy.builder()
                        .employeeCode("EMP-001")
                        .employeeName("John Doe")
                        .designation("Officer")
                        .organization("NIC")
                        .build();

        ClientDocument.CreatedFor createdFor =
                ClientDocument.CreatedFor.builder()
                        .personId(101)
                        .name("Titu")
                        .gender("Male")
                        .age(22)
                        .mobileNumber(9876543210L)
                        .build();

        ClientDocument.DocumentContent documentContent =
                ClientDocument.DocumentContent.builder()
                        .actualDocumentBase64("base64-document-data")
                        .build();

        ClientDocument document =
                ClientDocument.builder()
                        .documentId(documentId)
                        .createdOn(createdOn)
                        .applicationTransactionId(10001L)
                        .application(application)
                        .createdBy(createdBy)
                        .createdFor(createdFor)
                        .document(documentContent)
                        .build();

        DocumentResponse result =
                documentMapper.toResponse(document);

        assertEquals(documentId, result.getDocumentId());
        assertEquals(createdOn, result.getCreatedOn());
        assertEquals(
                10001L,
                result.getApplicationTransactionId()
        );

        assertEquals(
                "APP-101",
                result.getApplication().getApplicationId()
        );

        assertEquals(
                "Income Certificate",
                result.getApplication().getApplicationName()
        );

        assertEquals(
                "EMP-001",
                result.getCreatedBy().getEmployeeCode()
        );

        assertEquals(
                "John Doe",
                result.getCreatedBy().getEmployeeName()
        );

        assertEquals(
                "Officer",
                result.getCreatedBy().getDesignation()
        );

        assertEquals(
                "NIC",
                result.getCreatedBy().getOrganization()
        );

        assertEquals(
                101,
                result.getCreatedFor().getPersonId()
        );

        assertEquals(
                "Titu",
                result.getCreatedFor().getName()
        );

        assertEquals(
                "Male",
                result.getCreatedFor().getGender()
        );

        assertEquals(
                22,
                result.getCreatedFor().getAge()
        );

        assertEquals(
                9876543210L,
                result.getCreatedFor().getMobileNumber()
        );

        assertEquals(
                "base64-document-data",
                result.getDocument().getActualDocumentBase64()
        );
    }

    @Test
    void shouldMapReviewToReviewResponse() {

        Review review = Review.builder()
                .applicationTransactionId(10001L)
                .review("Excellent service")
                .build();

        ReviewResponse result = documentMapper.toReviewResponse(review);

        assertNotNull(result);

        assertEquals(
                10001L,
                result.getApplicationTransactionId()
        );

        assertEquals(
                "Excellent service",
                result.getReview()
        );
    }

    @Test
    void shouldReturnNullWhenReviewIsNull() {

        ReviewResponse result = documentMapper.toReviewResponse(null);

        assertNull(result);
    }

    @Test
    void shouldMapArchiveDocumentToArchiveResponse() {

        UUID archiveId = UUID.randomUUID();
        UUID originalDocumentId = UUID.randomUUID();
        Instant archivedOn = Instant.now();
        Instant originalCreatedOn = Instant.now();

        ClientDocument.Application application =
                ClientDocument.Application.builder()
                        .applicationId("APP-101")
                        .applicationName("Income Certificate")
                        .build();

        ClientDocument.CreatedBy createdBy =
                ClientDocument.CreatedBy.builder()
                        .employeeCode("EMP-001")
                        .employeeName("John Doe")
                        .designation("Officer")
                        .organization("NIC")
                        .build();

        ClientDocument.CreatedFor createdFor =
                ClientDocument.CreatedFor.builder()
                        .personId(101)
                        .name("Titu")
                        .gender("Male")
                        .age(22)
                        .mobileNumber(9876543210L)
                        .build();

        ClientDocument.DocumentContent documentContent =
                ClientDocument.DocumentContent.builder()
                        .actualDocumentBase64("base64-document-data")
                        .build();

        ArchiveDocument archiveDocument =
                ArchiveDocument.builder()
                        .archiveId(archiveId)
                        .applicationTransactionId(10001L)
                        .archivedOn(archivedOn)
                        .archivalComments("Document archived")
                        .originalDocumentId(originalDocumentId)
                        .originalCreatedOn(originalCreatedOn)
                        .application(application)
                        .createdBy(createdBy)
                        .createdFor(createdFor)
                        .document(documentContent)
                        .build();

        ArchiveResponse result =
                documentMapper.toArchiveResponse(archiveDocument);

        assertNotNull(result);

        // Archive metadata
        assertEquals(archiveId, result.getArchiveId());
        assertEquals(
                10001L,
                result.getApplicationTransactionId()
        );
        assertEquals(archivedOn, result.getArchivedOn());
        assertEquals(
                "Document archived",
                result.getArchivalComments()
        );
        assertEquals(
                originalDocumentId,
                result.getOriginalDocumentId()
        );
        assertEquals(
                originalCreatedOn,
                result.getOriginalCreatedOn()
        );

        // Application
        assertEquals(
                "APP-101",
                result.getApplication().getApplicationId()
        );
        assertEquals(
                "Income Certificate",
                result.getApplication().getApplicationName()
        );

        // CreatedBy
        assertEquals(
                "EMP-001",
                result.getCreatedBy().getEmployeeCode()
        );
        assertEquals(
                "John Doe",
                result.getCreatedBy().getEmployeeName()
        );
        assertEquals(
                "Officer",
                result.getCreatedBy().getDesignation()
        );
        assertEquals(
                "NIC",
                result.getCreatedBy().getOrganization()
        );

        // CreatedFor
        assertEquals(
                101,
                result.getCreatedFor().getPersonId()
        );
        assertEquals(
                "Titu",
                result.getCreatedFor().getName()
        );
        assertEquals(
                "Male",
                result.getCreatedFor().getGender()
        );
        assertEquals(
                22,
                result.getCreatedFor().getAge()
        );
        assertEquals(
                9876543210L,
                result.getCreatedFor().getMobileNumber()
        );

        // Document content
        assertEquals(
                "base64-document-data",
                result.getDocument().getActualDocumentBase64()
        );
    }

    @Test
    void shouldReturnNullWhenArchiveDocumentIsNull() {

        ArchiveResponse result = documentMapper.toArchiveResponse(null);

        assertNull(result);
    }

    @Test
    void shouldUpdateEntityFromUpdateDocumentRequest() {

        ClientDocument.Application oldApplication =
                ClientDocument.Application.builder()
                        .applicationId("OLD-APP")
                        .applicationName("Old Application")
                        .build();

        ClientDocument.CreatedBy oldCreatedBy =
                ClientDocument.CreatedBy.builder()
                        .employeeCode("OLD-EMP")
                        .employeeName("Old Employee")
                        .designation("Old Designation")
                        .organization("Old Organization")
                        .build();

        ClientDocument.CreatedFor oldCreatedFor =
                ClientDocument.CreatedFor.builder()
                        .personId(100)
                        .name("Old Person")
                        .gender("Male")
                        .age(30)
                        .mobileNumber(9000000000L)
                        .build();

        ClientDocument.DocumentContent oldDocumentContent =
                ClientDocument.DocumentContent.builder()
                        .actualDocumentBase64("old-document")
                        .build();

        ClientDocument document =
                ClientDocument.builder()
                        .applicationTransactionId(10001L)
                        .application(oldApplication)
                        .createdBy(oldCreatedBy)
                        .createdFor(oldCreatedFor)
                        .document(oldDocumentContent)
                        .build();

        ApplicationRequest applicationRequest =
                ApplicationRequest.builder()
                        .applicationId("NEW-APP")
                        .applicationName("New Application")
                        .build();

        CreatedByRequest createdByRequest =
                CreatedByRequest.builder()
                        .employeeCode("NEW-EMP")
                        .employeeName("New Employee")
                        .designation("New Designation")
                        .organization("New Organization")
                        .build();

        CreatedForRequest createdForRequest =
                CreatedForRequest.builder()
                        .personId(101)
                        .name("New Person")
                        .gender("Female")
                        .age(25)
                        .mobileNumber(9876543210L)
                        .build();

        DocumentContentRequest documentContentRequest =
                DocumentContentRequest.builder()
                        .actualDocumentBase64("new-document")
                        .build();

        UpdateDocumentRequest request =
                UpdateDocumentRequest.builder()
                        .application(applicationRequest)
                        .createdBy(createdByRequest)
                        .createdFor(createdForRequest)
                        .document(documentContentRequest)
                        .build();

        documentMapper.updateEntity(document, request);

        // Application
        assertEquals(
                "NEW-APP",
                document.getApplication().getApplicationId()
        );
        assertEquals(
                "New Application",
                document.getApplication().getApplicationName()
        );

        // CreatedBy
        assertEquals(
                "NEW-EMP",
                document.getCreatedBy().getEmployeeCode()
        );
        assertEquals(
                "New Employee",
                document.getCreatedBy().getEmployeeName()
        );
        assertEquals(
                "New Designation",
                document.getCreatedBy().getDesignation()
        );
        assertEquals(
                "New Organization",
                document.getCreatedBy().getOrganization()
        );

        // CreatedFor
        assertEquals(
                101,
                document.getCreatedFor().getPersonId()
        );
        assertEquals(
                "New Person",
                document.getCreatedFor().getName()
        );
        assertEquals(
                "Female",
                document.getCreatedFor().getGender()
        );
        assertEquals(
                25,
                document.getCreatedFor().getAge()
        );
        assertEquals(
                9876543210L,
                document.getCreatedFor().getMobileNumber()
        );

        // Document content
        assertEquals(
                "new-document",
                document.getDocument()
                        .getActualDocumentBase64()
        );

        // applicationTransactionId should remain unchanged
        assertEquals(
                10001L,
                document.getApplicationTransactionId()
        );
    }
}
