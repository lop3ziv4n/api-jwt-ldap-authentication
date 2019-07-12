package ar.org.example.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BadRequestAlertException extends RuntimeException {

    private String entityName;

    private String errorKey;

    public BadRequestAlertException(String exception) {
        super(exception);
    }

    public BadRequestAlertException(String exception, String entityName, String errorKey) {
        super(exception);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

}
