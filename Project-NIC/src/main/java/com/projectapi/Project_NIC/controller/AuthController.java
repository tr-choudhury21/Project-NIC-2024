package com.projectapi.Project_NIC.controller;

import com.projectapi.Project_NIC.auth.AuthenticationRequest;
import com.projectapi.Project_NIC.auth.AuthenticationResponse;
import com.projectapi.Project_NIC.auth.RegisterRequest;
import com.projectapi.Project_NIC.repository.ClientRepository;
import com.projectapi.Project_NIC.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/init")
    public ResponseEntity<AuthenticationResponse> init(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response =
                authenticationService.init(request);

        return ResponseEntity.ok(response);

    }
}

