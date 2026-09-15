package com.projectapi.Project_NIC.dto.request;

import com.projectapi.Project_NIC.model.ClientDocument;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentRequest {

    @Valid
    @NotNull(message = "Application information is required")
    private ApplicationRequest application;

    @Valid
    @NotNull(message = "Created by information is required")
    private CreatedByRequest createdBy;

    @Valid
    @NotNull(message = "Created for information is required")
    private CreatedForRequest createdFor;

    @Valid
    @NotNull(message = "Document information is required")
    private DocumentContentRequest document;


}