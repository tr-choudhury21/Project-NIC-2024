package com.projectapi.Project_NIC.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedForResponse {

    private Integer personId;

    private String name;

    private String gender;

    private Integer age;

    private Long mobileNumber;
}
