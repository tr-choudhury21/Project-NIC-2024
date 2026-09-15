package com.projectapi.Project_NIC.service;


import com.projectapi.Project_NIC.dto.request.AuthenticationRequest;
import com.projectapi.Project_NIC.dto.response.AuthenticationResponse;
import com.projectapi.Project_NIC.dto.request.RegisterRequest;
import com.projectapi.Project_NIC.dto.response.InitResponse;
import com.projectapi.Project_NIC.filter.JwtService;
import com.projectapi.Project_NIC.model.Client;
import com.projectapi.Project_NIC.model.Role;
import com.projectapi.Project_NIC.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    void shouldRegisterClientSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setClient_id("client123");
        request.setClient_secret("plainSecret");

        String encodedSecret = "encodedSecret";
        String jwtToken = "jwt-token";

        when(passwordEncoder.encode("plainSecret"))
                .thenReturn(encodedSecret);

        when(jwtService.generateToken(any(Client.class)))
                .thenReturn(jwtToken);

        AuthenticationResponse response =
                authenticationService.register(request);

        assertNotNull(response);

        assertEquals(
                jwtToken,
                response.getToken()
        );

        ArgumentCaptor<Client> clientCaptor =
                ArgumentCaptor.forClass(Client.class);

        verify(clientRepository)
                .save(clientCaptor.capture());

        Client savedClient = clientCaptor.getValue();

        assertEquals(
                "client123",
                savedClient.getClient_id()
        );

        assertEquals(
                encodedSecret,
                savedClient.getClient_secret()
        );

        assertEquals(
                Role.USER,
                savedClient.getRole()
        );

        assertNotNull(savedClient.getCreated_on());
        assertNotNull(savedClient.getExpiry_on());

        assertTrue(
                savedClient.getExpiry_on()
                        .after(savedClient.getCreated_on())
        );

        verify(passwordEncoder)
                .encode("plainSecret");

        verify(jwtService)
                .generateToken(savedClient);
    }

    @Test
    void shouldAuthenticateClientSuccessfully() {

        AuthenticationRequest request = new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("plainSecret");

        Client user = Client.builder()
                .client_id("client123")
                .client_secret("encodedSecret")
                .role(Role.USER)
                .build();

        String jwtToken = "jwt-token";

        when(clientRepository.findByClientId("client123"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn(jwtToken);

        AuthenticationResponse response =
                authenticationService.authenticate(request);

        assertNotNull(response);

        assertEquals(
                jwtToken,
                response.getToken()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(clientRepository)
                .findByClientId("client123");

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {

        AuthenticationRequest request =
                new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("wrongSecret");

        AuthenticationException authenticationException =
                new BadCredentialsException("Invalid credentials");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(authenticationException);

        AuthenticationException exception =
                assertThrows(
                        AuthenticationException.class,
                        () -> authenticationService.authenticate(request)
                );

        assertEquals(
                "Invalid credentials",
                exception.getMessage()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verifyNoInteractions(clientRepository);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedClientIsNotFound() {

        AuthenticationRequest request =
                new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("plainSecret");

        when(clientRepository.findByClientId("client123"))
                .thenReturn(Optional.empty());

        NoSuchElementException exception =
                assertThrows(
                        NoSuchElementException.class,
                        () -> authenticationService.authenticate(request)
                );

        assertNotNull(exception);

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(clientRepository)
                .findByClientId("client123");

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldAuthenticateExistingClientDuringInit() {

        AuthenticationRequest request =
                new AuthenticationRequest();

        request.setClient_id("client123");
        request.setClient_secret("plainSecret");

        Client existingClient = Client.builder()
                .client_id("client123")
                .build();

        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                        .token("jwt-token")
                        .build();

        when(clientRepository.findByClientId("client123"))
                .thenReturn(Optional.of(existingClient));

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(jwtService.generateToken(existingClient))
                .thenReturn("jwt-token");

        InitResponse response = authenticationService.init(request);

        assertNotNull(response);

        assertFalse(response.isRegistered());

        assertNotNull(response.getAuthenticationResponse());

        assertEquals(
                "jwt-token",
                response.getAuthenticationResponse().getToken()
        );

        verify(clientRepository, times(2))
                .findByClientId("client123");

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(jwtService)
                .generateToken(existingClient);
    }

    @Test
    void shouldRegisterNewClientDuringInit() {

        AuthenticationRequest request = new AuthenticationRequest();

        request.setClient_id("newClient");
        request.setClient_secret("plainSecret");

        when(clientRepository.findByClientId("newClient"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("plainSecret"))
                .thenReturn("encodedSecret");

        when(jwtService.generateToken(any(Client.class)))
                .thenReturn("jwt-token");

        InitResponse response =
                authenticationService.init(request);

        assertNotNull(response);
        assertTrue(response.isRegistered());

        assertNotNull(response.getAuthenticationResponse());

        assertEquals(
                "jwt-token",
                response.getAuthenticationResponse().getToken()
        );

        verify(clientRepository)
                .findByClientId("newClient");

        verify(passwordEncoder)
                .encode("plainSecret");

        verify(clientRepository)
                .save(any(Client.class));

        verify(jwtService)
                .generateToken(any(Client.class));

        verifyNoInteractions(authenticationManager);
    }
}
