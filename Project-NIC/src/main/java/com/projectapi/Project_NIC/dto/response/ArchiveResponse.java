package com.projectapi.Project_NIC.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ArchiveResponse {

    private UUID archiveId;

    private Long applicationTransactionId;

    private Instant archivedOn;

    private String archivalComments;

    private UUID originalDocumentId;

    private Instant originalCreatedOn;

    private ApplicationResponse application;

    private CreatedByResponse createdBy;

    private CreatedForResponse createdFor;

    private DocumentContentResponse document;
}
