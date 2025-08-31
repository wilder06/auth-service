package pe.com.creditya.model.common.exceptions;

import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {
    private final String field;

    public BusinessValidationException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

}
