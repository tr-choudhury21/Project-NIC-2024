package com.projectapi.Project_NIC.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "archive_documents")
public class ArchiveDocument {

    private long applicationTransactionId;
    private String archivalComments;
}
