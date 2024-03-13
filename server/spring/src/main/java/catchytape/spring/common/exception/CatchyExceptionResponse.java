package catchytape.spring.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CatchyExceptionResponse {
    private int statusCode;
    private String message;
}
