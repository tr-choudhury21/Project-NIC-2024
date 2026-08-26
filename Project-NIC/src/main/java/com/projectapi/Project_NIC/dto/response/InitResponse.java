package com.projectapi.Project_NIC.dto.response;

import com.projectapi.Project_NIC.auth.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InitResponse {

    private AuthenticationResponse authenticationResponse;
    private boolean registered;
}
