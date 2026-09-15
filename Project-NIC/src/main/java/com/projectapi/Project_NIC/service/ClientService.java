package com.projectapi.Project_NIC.service;

import com.projectapi.Project_NIC.dto.request.SignUpRequest;
import com.projectapi.Project_NIC.config.DateConfig;
import com.projectapi.Project_NIC.model.Client;
import com.projectapi.Project_NIC.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Optional<String> login(long mobileNo) {
        Optional<Client> optionalClient = clientRepository.findByMobileNo(mobileNo);
        return optionalClient.isPresent() ? Optional.of(generateOtp()) : Optional.empty();
    }

    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public Optional<Client> signup(String clientId, SignUpRequest signUpRequest) {
        if (!DateConfig.isValidDate(signUpRequest.getDob())) {
            throw new IllegalArgumentException("Invalid date format. Please use dd-MM-yyyy");
        }

        return clientRepository.findByClientId(clientId)
                .map(client -> {
                    client.setMobileNo(signUpRequest.getMobileNo());
                    client.setEmailId(signUpRequest.getEmailId());
                    client.setName(signUpRequest.getName());
                    client.setGender(signUpRequest.getGender());
                    client.setDob(signUpRequest.getDob());
                    client.setAddress(signUpRequest.getAddress());
                    return clientRepository.save(client);
                });
    }
}
