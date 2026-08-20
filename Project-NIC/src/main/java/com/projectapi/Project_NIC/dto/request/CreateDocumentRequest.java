package com.projectapi.Project_NIC.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {

    @Valid
    @NotNull(message = "Application information is required")
    private ApplicationRequest application;


    @Positive(message = "Application transaction ID must be positive")
    private Long applicationTransactionId;


    @Valid
    @NotNull(message = "Created by information is required")
    private CreatedByRequest createdBy;

    @Valid
    @NotNull(message = "Created for information is required")
    private CreatedForRequest createdFor;

    @Valid
    @NotNull(message = "Document content is required")
    private DocumentContentRequest document;
}
