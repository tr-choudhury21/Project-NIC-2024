package com.projectapi.Project_NIC.mapper;


import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.dto.response.*;
import com.projectapi.Project_NIC.model.ClientDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public ClientDocument toEntity(CreateDocumentRequest request) {

        return ClientDocument.builder()
                .applicationTransactionId(request.getApplicationTransactionId())
                .application(toApplication(request.getApplication()))
                .createdBy(toCreatedBy(request.getCreatedBy()))
                .createdFor(toCreatedFor(request.getCreatedFor()))
                .document(toDocumentContent(request.getDocument()))
                .build();
    }

    private ClientDocument.Application toApplication(
            ApplicationRequest request) {

        return ClientDocument.Application.builder()
                .applicationId(request.getApplicationId())
                .applicationName(request.getApplicationName())
                .build();
    }

    private ClientDocument.CreatedBy toCreatedBy(
            CreatedByRequest request) {

        return ClientDocument.CreatedBy.builder()
                .employeeCode(request.getEmployeeCode())
                .employeeName(request.getEmployeeName())
                .designation(request.getDesignation())
                .organization(request.getOrganization())
                .build();
    }

    private ClientDocument.CreatedFor toCreatedFor(
            CreatedForRequest request) {

        return ClientDocument.CreatedFor.builder()
                .personId(request.getPersonId())
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .mobileNumber(request.getMobileNumber())
                .build();
    }

    private ClientDocument.DocumentContent toDocumentContent(
            DocumentContentRequest request) {

        return ClientDocument.DocumentContent.builder()
                .actualDocumentBase64(request.getActualDocumentBase64())
                .build();
    }


    public DocumentResponse toResponse(ClientDocument document) {

        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .createdOn(document.getCreatedOn())
                .applicationTransactionId(document.getApplicationTransactionId())
                .application(toApplicationResponse(document.getApplication()))
                .createdBy(toCreatedByResponse(document.getCreatedBy()))
                .createdFor(toCreatedForResponse(document.getCreatedFor()))
                .document(toDocumentContentResponse(document.getDocument()))
                .build();
    }

    private ApplicationResponse toApplicationResponse(
            ClientDocument.Application application) {

        return ApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .applicationName(application.getApplicationName())
                .build();
    }

    private CreatedByResponse toCreatedByResponse(
            ClientDocument.CreatedBy createdBy) {

        return CreatedByResponse.builder()
                .employeeCode(createdBy.getEmployeeCode())
                .employeeName(createdBy.getEmployeeName())
                .designation(createdBy.getDesignation())
                .organization(createdBy.getOrganization())
                .build();
    }

    private CreatedForResponse toCreatedForResponse(
            ClientDocument.CreatedFor createdFor) {

        return CreatedForResponse.builder()
                .personId(createdFor.getPersonId())
                .name(createdFor.getName())
                .gender(createdFor.getGender())
                .age(createdFor.getAge())
                .mobileNumber(createdFor.getMobileNumber())
                .build();
    }

    private DocumentContentResponse toDocumentContentResponse(
            ClientDocument.DocumentContent documentContent) {

        return DocumentContentResponse.builder()
                .actualDocumentBase64(
                        documentContent.getActualDocumentBase64()
                )
                .build();
    }

    public void updateEntity(
            ClientDocument document,
            UpdateDocumentRequest request) {

        document.setApplication(
                toApplication(request.getApplication())
        );

        document.setCreatedBy(
                toCreatedBy(request.getCreatedBy())
        );

        document.setCreatedFor(
                toCreatedFor(request.getCreatedFor())
        );

        document.setDocument(
                toDocumentContent(request.getDocument())
        );
    }


}
