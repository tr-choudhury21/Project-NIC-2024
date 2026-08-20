package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @Positive(message = "Application transaction ID must be positive")
    private long applicationTransactionId;

    @NotBlank(message = "Review is required")
    private String review;
}
