package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request){
        System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");
        try {
            Product product = productRepository.findById((long) request.getProductId()).orElseThrow(Exception::new);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
