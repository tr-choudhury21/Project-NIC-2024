package com.projectapi.Project_NIC.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "document")

public class ClientDocument {

    @Id
    private UUID documentId;
    private Instant createdOn;
    private Long applicationTransactionId;
    private Application application;
    private CreatedBy createdBy;
    private CreatedFor createdFor;
    private DocumentContent document;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Application {
        private String applicationId;
        private String applicationName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Module {
        private String moduleId;
        private String moduleName;
    }




    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatedBy {
        private String employeeCode;
        private String employeeName;
        private String designation;
        private String organization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatedFor {
        private int personId;
        private String name;
        private String gender;
        private int age;
        private long mobileNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentContent {
        private String actualDocumentBase64;
    }



}

