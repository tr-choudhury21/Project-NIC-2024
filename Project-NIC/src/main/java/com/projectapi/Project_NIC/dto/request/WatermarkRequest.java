package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatermarkRequest {

    @Positive(message = "Application transaction ID must be positive")
    private long applicationTransactionId;
    @NotBlank(message = "Watermark cannot be blank")
    private String watermark;
}
