package com.wojet.pmtool.exception;

public class ResourceNotFoundException extends RuntimeException {

    String resourceName;
    String fieldName;
    String fieldValue;
    Long fieldId;
    
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' not found !!!", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldValue, Long fieldId) {
        super(String.format("%s with %s '%s' not found !!!", resourceName, fieldValue, fieldId));
        this.resourceName = resourceName;
        this.fieldName = fieldValue;
        this.fieldId = fieldId;
    }
}
