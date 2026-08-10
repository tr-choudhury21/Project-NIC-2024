package com.projectapi.Project_NIC.mapper;


import com.projectapi.Project_NIC.dto.request.*;
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
}
