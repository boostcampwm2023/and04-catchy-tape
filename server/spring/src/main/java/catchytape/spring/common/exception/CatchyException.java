package catchytape.spring.common.exception;

import catchytape.spring.common.domain.ErrorCode;
import catchytape.spring.common.domain.HttpStatusCode;
import lombok.Getter;

@Getter
public class CatchyException extends Exception {
    private int statusCode;
    private int errorCode;

    public CatchyException(String statusMessage, String errorMessage) {
        super(errorMessage);
        this.statusCode = HttpStatusCode.valueOf(statusMessage).getCode();
        this.errorCode = ErrorCode.valueOf(errorMessage).getCode();
    }
}
