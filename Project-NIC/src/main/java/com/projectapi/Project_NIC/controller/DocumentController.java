package com.projectapi.Project_NIC.controller;

import com.projectapi.Project_NIC.dto.request.*;
import com.projectapi.Project_NIC.dto.response.DocumentResponse;
import com.projectapi.Project_NIC.model.*;
import com.projectapi.Project_NIC.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;


    //to save a document
    @PostMapping
    public ResponseEntity<UUID> saveDocument(@Valid @RequestBody CreateDocumentRequest request) {
        UUID documentId = documentService.saveDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentId);
    }

    //to get a document
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID documentId){
//        System.out.println("received request for document ID: " + documentId);

        return ResponseEntity.ok(documentService.getDocumentById(documentId));

    }

    //to get a document of particular client
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByPersonId(@PathVariable("personId") int personId) {

        return ResponseEntity.ok(documentService.getDocumentsByPersonId(personId));
    }

    //to review the client's document
    @PostMapping("/review")
    public ResponseEntity<Review> saveOrUpdateReview(@Valid @RequestBody ReviewRequest request) {

        Review savedReview = documentService.saveOrUpdateReview(request);

        return ResponseEntity.ok(savedReview);
    }


    //to archive any document
    @PostMapping("/archive")
    public ResponseEntity<ArchiveDocument> archiveDocument(@Valid @RequestBody ArchiveDocumentRequest request) {

        ArchiveDocument savedArchiveDocument = documentService.archiveDocument(request);

        return ResponseEntity.ok(savedArchiveDocument);
    }


    //to edit & update the document
    @PutMapping("/{documentId}")
    public ResponseEntity<?> updateDocument(@PathVariable UUID documentId, @Valid @RequestBody UpdateDocumentRequest request) {

        return ResponseEntity.ok(documentService.updateDocument(documentId, request));
    }


    // to view list of review section
    @GetMapping("/reviews/{applicationTransactionId}")
    public ResponseEntity<Review> getReviewByApplicationId(@PathVariable long applicationTransactionId) {
        Optional<Review> reviewOptional = documentService.viewReviewLog(applicationTransactionId);

        return reviewOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //to view list of edited document setion
    @GetMapping("/archive/{applicationTransactionId}")
    public ResponseEntity<ArchiveDocument> getArchiveDocumentByApplicationTransactionId(@PathVariable long applicationTransactionId) {
        Optional<ArchiveDocument> archiveDocumentOptional = documentService.viewEditLog(applicationTransactionId);

        return archiveDocumentOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //to add watermark to any document
    @PostMapping("/watermark")
    public ResponseEntity<DocumentResponse> addWatermarkToDocument(
            @Valid @RequestBody WatermarkRequest request){

        return ResponseEntity.ok(
                documentService.addWatermarkToDocument(request)
        );
    }

    //to set password & make any document confidential
    @PostMapping("/password")
    public ResponseEntity<String> addPasswordToPdf(@Valid @RequestBody PdfPasswordRequest request) {

        String protectedPdf = documentService.addPasswordToPdf(request);

        return ResponseEntity.ok(protectedPdf);
    }

}

