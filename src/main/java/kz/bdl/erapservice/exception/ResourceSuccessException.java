package kz.bdl.erapservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK)
public class ResourceSuccessException extends RuntimeException {
    public ResourceSuccessException(String message) {
        super(message);
    }
}
