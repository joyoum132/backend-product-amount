package antigravity.config.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private String message;
    private String detail;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public ExceptionResponse(String message, String detail) {
        this.message = message;
        this.detail = detail;
    }
}
