package com.projectapi.Project_NIC.repository;

import com.projectapi.Project_NIC.model.ClientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends MongoRepository<ClientDocument, UUID> {
    @Query("{'createdFor.personId': ?0}")
    List<ClientDocument> findByPersonId(int personId);

    @Query("{'applicationTransactionId': ?0}")
    Optional<ClientDocument> findByApplicationTransactionId(long applicationId);

}
