package catchytape.spring.common.domain;

import lombok.Getter;

@Getter
public enum HttpStatusCode {
    SUCCESS(200, "SUCCESS"), 
    CONFLICT(409, "DUPLICATED_NICKNAME"), 
    UNAUTHORIZED(401, "WRONG_TOKEN"), 
    NOT_FOUND(404, "NOT_FOUND"), 
    INTERNAL_SERVER_ERROR(500, "SERVER_ERROR"), 
    BAD_REQUEST(400, "BAD_REQUEST");

    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}


