package com.projectapi.Project_NIC.controller;

import com.projectapi.Project_NIC.model.*;
import com.projectapi.Project_NIC.repository.DocumentRepository;
import com.projectapi.Project_NIC.service.DocumentService;
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
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    public DocumentController(
            DocumentService documentService,
            DocumentRepository documentRepository) {

        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }


    //to save a document
    @PostMapping("/savedocument")
    public ResponseEntity<UUID> saveDocument(@RequestBody ClientDocument document) {
        UUID documentId = documentService.saveDocument(document);
        return ResponseEntity.ok(documentId);
    }

    //to get a document
    @GetMapping("/getdocument/{id}")
    public ResponseEntity<ClientDocument> getDocument(@PathVariable("id") UUID document_id){
        System.out.println("received request for document ID: " + document_id);
        ResponseEntity<ClientDocument> document = documentService.getDocumentById(document_id);

        return ResponseEntity.ok(document.getBody());
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
        Optional<ClientDocument> clientdocumentOptional = documentRepository.findByApplicationTransactionId(review.getApplication_transaction_id());

        if (clientdocumentOptional.isPresent()) {
            review.setApplication_transaction_id(clientdocumentOptional.get().getFile_information().getApplication_transaction_id());
            Review savedReview = documentService.saveOrUpdateReview(review);

            return ResponseEntity.ok(savedReview);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    //to archive any document
    @PostMapping("/archivedocument")
    public ResponseEntity<?> archiveDocument(@RequestBody ArchiveDocument archiveDocument) {
        Optional<ClientDocument> clientDocumentOptional = documentRepository.findByApplicationTransactionId(archiveDocument.getApplication_transaction_id());

        if (clientDocumentOptional.isPresent()) {
            ArchiveDocument savedArchiveDocument = documentService.archiveDocument(archiveDocument);
            return ResponseEntity.ok(savedArchiveDocument);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //to edit & update the document
    @PostMapping("/editdocumentinfo/{documentId}")
    public ResponseEntity<?> editDocumentInfo(@PathVariable UUID documentId, @RequestBody ClientDocument newDocument) {
        ResponseEntity<ClientDocument> responseEntity = documentService.getDocumentById(documentId);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            documentService.deleteDocumentById(documentId);

            newDocument.setDocument_id(documentId);
            ClientDocument savedDocument = documentService.updateDocument(newDocument);

            return ResponseEntity.ok(savedDocument);
        } else {
            return ResponseEntity.notFound().build();
        }
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
        try {
            ClientDocument updatedDocument = documentService.addWatermarkToDocument(watermarkRequest.getApplication_transaction_id(),
                    watermarkRequest.getWatermark());
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //to set password & make any document confidential
    @PostMapping("/setadocumentconfidential")
    public ResponseEntity<?> addPasswordToPdf(@RequestBody PdfPasswordRequest request) {
        try {
            String base64PdfWithPassword = documentService.addPasswordToPdf(request);
            return ResponseEntity.ok(base64PdfWithPassword);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to add password to PDF: " + e.getMessage());
        }
    }

}

