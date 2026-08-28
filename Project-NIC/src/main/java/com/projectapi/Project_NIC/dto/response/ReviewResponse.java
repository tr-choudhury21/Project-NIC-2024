package com.projectapi.Project_NIC.dto.response;

import com.projectapi.Project_NIC.dto.request.ReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long applicationTransactionId;
    private String review;
}
