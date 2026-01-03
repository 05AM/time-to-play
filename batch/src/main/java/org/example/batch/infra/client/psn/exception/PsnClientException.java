package org.example.batch.infra.client.psn.exception;

public class PsnClientException extends RuntimeException {
    public PsnClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
