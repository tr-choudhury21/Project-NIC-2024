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

    private final AuthenticationService service;


    private final ClientRepository clientRepository;

    @PostMapping("/init")
    public ResponseEntity<AuthenticationResponse> init(@RequestBody AuthenticationRequest request){
        if(clientRepository.findByClientId(request.getClient_id()).isPresent()){
            try {
                AuthenticationResponse response = service.authenticate(request);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }catch (Exception e){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }else{
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setClient_id(request.getClient_id());
            registerRequest.setClient_secret(request.getClient_secret());

            try {
                AuthenticationResponse response = service.register(registerRequest);
                return new ResponseEntity<>(response , HttpStatus.CREATED);
            }catch (Exception e){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }
    }
}

