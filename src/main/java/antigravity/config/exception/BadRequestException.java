package antigravity.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadRequestException extends RuntimeException {
    private String message;
    private String detail;

    public BadRequestException(String message) {
        this.message = message;
    }
}
