package com.projectapi.Project_NIC.repository;

import com.projectapi.Project_NIC.model.ClientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends MongoRepository<ClientDocument, UUID> {

    List<ClientDocument> findByCreatedForPersonId(int personId);


    Optional<ClientDocument> findByApplicationTransactionId(long applicationId);

}
