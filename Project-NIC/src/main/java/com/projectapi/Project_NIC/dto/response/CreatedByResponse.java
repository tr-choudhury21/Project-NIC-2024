package com.projectapi.Project_NIC.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedByResponse {

    private String employeeCode;

    private String employeeName;

    private String designation;

    private String organization;

}
