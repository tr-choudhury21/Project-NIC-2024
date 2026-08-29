package com.projectapi.Project_NIC.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "archive_documents")
public class ArchiveDocument {

    @Id
    private UUID archiveId;

    @Indexed(unique = true)
    private Long applicationTransactionId;

    private Instant archivedOn;

    private String archivalComments;

    private UUID originalDocumentId;

    private Instant originalCreatedOn;

    private ClientDocument.Application application;

    private ClientDocument.CreatedBy createdBy;

    private ClientDocument.CreatedFor createdFor;

    private ClientDocument.DocumentContent document;
}
