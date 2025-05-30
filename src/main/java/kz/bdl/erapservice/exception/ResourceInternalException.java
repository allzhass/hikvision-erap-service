package kz.bdl.erapservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceInternalException extends RuntimeException {
    public ResourceInternalException(String message) {
        super(message);
    }
}
