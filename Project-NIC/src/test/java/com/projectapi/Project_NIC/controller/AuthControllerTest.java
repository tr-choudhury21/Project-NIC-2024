package com.projectapi.Project_NIC.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectapi.Project_NIC.dto.request.AuthenticationRequest;
import com.projectapi.Project_NIC.dto.response.AuthenticationResponse;
import com.projectapi.Project_NIC.dto.response.InitResponse;
import com.projectapi.Project_NIC.filter.JwtFilter;
import com.projectapi.Project_NIC.filter.JwtService;
import com.projectapi.Project_NIC.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldReturn201WhenNewClientIsRegistered() throws Exception {

        AuthenticationRequest request = new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("secret");

        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                        .token("jwt-token")
                        .build();

        InitResponse initResponse =
                new InitResponse(authenticationResponse, true);

        when(authenticationService.init(any(AuthenticationRequest.class)))
                .thenReturn(initResponse);

        mockMvc.perform(
                        post("/init")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(
                        """
                        {
                            "token": "jwt-token"
                        }
                        """
                ));
    }

    @Test
    void shouldReturn200WhenClientAlreadyExists() throws Exception {

        AuthenticationRequest request =
                new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("secret");

        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                        .token("jwt-token")
                        .build();

        InitResponse initResponse =
                new InitResponse(authenticationResponse, false);

        when(authenticationService.init(any(AuthenticationRequest.class)))
                .thenReturn(initResponse);

        mockMvc.perform(
                        post("/init")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                        {
                            "token": "jwt-token"
                        }
                        """
                ));
    }

    @Test
    void shouldPassRequestToAuthenticationService() throws Exception {

        AuthenticationRequest request =
                new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("secret");

        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                        .token("jwt-token")
                        .build();

        InitResponse initResponse =
                new InitResponse(authenticationResponse, false);

        when(authenticationService.init(any(AuthenticationRequest.class)))
                .thenReturn(initResponse);

        mockMvc.perform(
                        post("/init")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        ArgumentCaptor<AuthenticationRequest> captor =
                ArgumentCaptor.forClass(AuthenticationRequest.class);

        verify(authenticationService)
                .init(captor.capture());

        AuthenticationRequest capturedRequest =
                captor.getValue();

        assertEquals("client123", capturedRequest.getClient_id());
        assertEquals("secret", capturedRequest.getClient_secret());
    }
}
