package com.projectapi.Project_NIC.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedByRequest {

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Organization is required")
    private String organization;
}
