package com.example.aggregator.service;

class DownstreamServiceException extends RuntimeException {
    DownstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    DownstreamServiceException(Throwable cause) {
        super("Downstream call failed", cause);
    }
}
