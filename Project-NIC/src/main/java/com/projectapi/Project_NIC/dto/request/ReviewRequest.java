package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewRequest {

    @Positive(message = "Application transaction ID must be positive")
    private long applicationTransactionId;

    @NotBlank(message = "Review is required")
    private String review;
}
