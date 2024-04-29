package antigravity.model.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
public class ProductInfoRequest {
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    private List<Long> couponIds;
}
