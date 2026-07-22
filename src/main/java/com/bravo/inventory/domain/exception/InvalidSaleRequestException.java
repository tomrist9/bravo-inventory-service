package com.bravo.inventory.domain.exception;

public class InvalidSaleRequestException extends RuntimeException {

    public InvalidSaleRequestException(String message) {

        super(message);
    }
}