package com.projectapi.Project_NIC.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ArchiveResponse {

    private Long applicationTransactionId;
    private String archivalComments;
}
