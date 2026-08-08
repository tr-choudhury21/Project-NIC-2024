package com.projectapi.Project_NIC.dto.request;

import com.projectapi.Project_NIC.model.ClientDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {

    private ClientDocument.Application application;

    private ClientDocument.Module module;

    private ClientDocument.Workflow workflow;

    private ClientDocument.FileInformation fileInformation;

    private ClientDocument.CreatedBy createdBy;

    private ClientDocument.CreatedFor createdFor;

    private ClientDocument.DocumentContent document;

    private ClientDocument.AdditionalInfo additionalInfo1;

    private ClientDocument.AdditionalInfo additionalInfo2;
}
