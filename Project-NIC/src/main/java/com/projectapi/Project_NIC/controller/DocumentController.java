package com.projectapi.Project_NIC.controller;

import com.projectapi.Project_NIC.exception.DocumentProcessingException;
import com.projectapi.Project_NIC.model.*;
import com.projectapi.Project_NIC.repository.DocumentRepository;
import com.projectapi.Project_NIC.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;


    //to save a document
    @PostMapping("/savedocument")
    public ResponseEntity<UUID> saveDocument(@RequestBody ClientDocument document) {
        UUID documentId = documentService.saveDocument(document);
        return ResponseEntity.ok(documentId);
    }

    //to get a document
    @GetMapping("/getdocument/{id}")
    public ResponseEntity<ClientDocument> getDocument(@PathVariable("id") UUID documentId){
        System.out.println("received request for document ID: " + documentId);

        return ResponseEntity.ok(documentService.getDocumentById(documentId));

    }

    //to get a document of particular client
    @GetMapping("/documentofaperson/{personId}")
    public ResponseEntity<List<ClientDocument>> getDocumentsByPersonId(@PathVariable("personId") int personId) {
        List<ClientDocument> documents = documentService.getDocumentsByPersonId(personId);

        if (documents.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(documents);
        }
    }

    //to review the client's document
    @PostMapping("/reviewdocument")
    public ResponseEntity<?> saveOrUpdateReview(@RequestBody Review review) {

        Review savedReview = documentService.createOrUpdateReview(review);

        return ResponseEntity.ok(savedReview);
    }


    //to archive any document
    @PostMapping("/archivedocument")
    public ResponseEntity<?> archiveDocument(@RequestBody ArchiveDocument archiveDocument) {

        ArchiveDocument savedArchiveDocument = documentService.createArchive(archiveDocument);

        return ResponseEntity.ok(savedArchiveDocument);
    }


    //to edit & update the document
    @PostMapping("/editdocumentinfo/{documentId}")
    public ResponseEntity<?> editDocumentInfo(@PathVariable UUID documentId, @RequestBody ClientDocument newDocument) {

        ClientDocument savedDocument = documentService.updateDocument(documentId, newDocument);

        return ResponseEntity.ok(savedDocument);
    }


    // to view list of review section
    @GetMapping("/viewreviewlog/{applicationTransactionId}")
    public ResponseEntity<Review> getReviewByApplicationId(@PathVariable long applicationTransactionId) {
        Optional<Review> reviewOptional = documentService.viewReviewLog(applicationTransactionId);

        return reviewOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //to view list of edited document setion
    @GetMapping("/vieweditlog/{applicationTransactionId}")
    public ResponseEntity<ArchiveDocument> getArchiveDocumentByApplicationTransactionId(@PathVariable long applicationTransactionId) {
        Optional<ArchiveDocument> archiveDocumentOptional = documentService.viewEditLog(applicationTransactionId);

        return archiveDocumentOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //to add watermark to any document
    @PostMapping("/addwatermarktodocument")
    public ResponseEntity<?> addWatermarkToDocument(@RequestBody WatermarkRequest watermarkRequest){
        ClientDocument updatedDocument =
                documentService.addWatermarkToDocument(
                        watermarkRequest.getApplication_transaction_id(),
                        watermarkRequest.getWatermark()
                );

        return ResponseEntity.ok(updatedDocument);
    }

    //to set password & make any document confidential
    @PostMapping("/setadocumentconfidential")
    public ResponseEntity<?> addPasswordToPdf(@RequestBody PdfPasswordRequest request) {
        String base64PdfWithPassword =
                documentService.addPasswordToPdf(request);

        return ResponseEntity.ok(base64PdfWithPassword);
    }

}

