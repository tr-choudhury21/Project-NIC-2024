package com.projectapi.Project_NIC.repository;

import com.projectapi.Project_NIC.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    @Query("{'applicationTransactionId' :  ?0 }")
    Optional<Review> findByApplicationTransactionId(long application_id);
}
