package com.projectapi.Project_NIC.repository;

import com.projectapi.Project_NIC.model.ArchiveDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ArchiveRepository extends MongoRepository<ArchiveDocument, Long> {


    Optional<ArchiveDocument> findByApplicationTransactionId(long applicationTransactionId);
}
