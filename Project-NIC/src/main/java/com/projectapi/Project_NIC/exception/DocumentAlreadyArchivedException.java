package com.projectapi.Project_NIC.exception;

public class DocumentAlreadyArchivedException extends RuntimeException{

    public DocumentAlreadyArchivedException(String message) {
        super(message);
    }
}
