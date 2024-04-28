package antigravity.controller;

import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    //상품 가격 추출 api
    @GetMapping("/amount")
    public ResponseEntity<ProductAmountResponse> getProductAmount() {

        ProductAmountResponse response = service.getProductAmount(getParam());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ProductInfoRequest getParam() {
        return ProductInfoRequest.builder()
                .productId(1L)
                .couponIds(List.of(1L, 2L))
                .build();
    }
}
