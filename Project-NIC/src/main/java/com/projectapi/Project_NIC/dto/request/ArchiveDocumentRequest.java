package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArchiveDocumentRequest {

    @Positive(message = "Application transaction ID must be positive")
    private long applicationTransactionId;

    @NotBlank(message = "Archival comments are required")
    @Size(
            max = 500,
            message = "Archival comments cannot exceed 500 characters"
    )
    private String archivalComments;
}
