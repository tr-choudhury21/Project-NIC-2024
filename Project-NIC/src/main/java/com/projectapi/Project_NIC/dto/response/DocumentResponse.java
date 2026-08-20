package com.projectapi.Project_NIC.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private UUID documentId;

    private Instant createdOn;

    private Long applicationTransactionId;

    private ApplicationResponse application;

    private CreatedByResponse createdBy;

    private CreatedForResponse createdFor;

    private DocumentContentResponse document;
}
