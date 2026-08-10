package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContentRequest {

    @NotBlank(message = "Document content is required")
    private String actualDocumentBase64;
}
