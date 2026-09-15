package com.projectapi.Project_NIC.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedForRequest {

    @NotNull(message = "Person ID is required")
    @Positive(message = "Person ID must be positive")
    private Integer personId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    private Integer age;

    @NotNull(message = "Mobile number is required")
    @Positive(message = "Mobile number must be positive")
    private Long mobileNumber;
}
