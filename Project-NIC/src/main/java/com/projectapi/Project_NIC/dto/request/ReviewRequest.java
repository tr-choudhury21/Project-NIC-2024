package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewRequest {

    @Positive
    private long applicationTransactionId;

    @NotBlank(message = "Review cannot be blank")
    private String review;
}
