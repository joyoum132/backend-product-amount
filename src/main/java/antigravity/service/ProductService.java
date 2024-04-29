package antigravity.service;

import antigravity.config.exception.BadRequestException;
import antigravity.config.exception.NotFoundException;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.model.Validation;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.strategy.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PromotionProductsRepository promotionProductsRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request){
        // 1. product 가져온다. 없으면 throw BadRequest
        // 2. 상품 가격 범위 확인
        // 3. 상품의 프로모션 조회 (기간 체크 필수)
        // 4. 프로모션 적용 시 percent -> won 순서로 적용
        // 5. return 전에 천원단위로 절삭

        if(Validation.isNull(request.getProductId())
                || Validation.isNegative(request.getProductId())
                || Validation.isEmptyList(request.getCouponIds())) {
            throw new BadRequestException("요청 파라미터 값을 확인해주세요");
        }

        long productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        if(!product.isValidRange())
            throw new BadRequestException("상품 정보가 잘못되었습니다.","금액은 1000원 10_000_000 사이");

        List<Promotion> promotions = promotionProductsRepository
                .getAvailablePromotionByProduct(productId, request.getCouponIds(), LocalDate.now())
                .stream()
                .map(PromotionProducts::getPromotion)
                .sorted(
                        Comparator.comparing(
                                Promotion::getDiscount_type)
                            .thenComparing(Comparator.comparingInt(Promotion::getDiscount_value).reversed())
                ).toList();

        if(promotions.isEmpty()) throw new BadRequestException("적용 가능한 프로모션이 없습니다.");

        int promotionPrice = applyPromotion(product, promotions);
        int finalPrice = cutPrice(promotionPrice);

        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(product.getPrice()-finalPrice)
                .finalPrice(finalPrice)
                .build();
    }

    int applyPromotion(Product product, List<Promotion> promotions) {
        int price = product.getPrice();
        for (Promotion promotion : promotions) {
            DiscountStrategy discountStrategy = promotion.getDiscountStrategy();
            price = discountStrategy.applyDiscount(price, promotion.getDiscount_value());
        }
        return price;
    }

    int cutPrice(int discountedPrice) {
        return discountedPrice >= 1000 ? discountedPrice / 1000 * 1000 : discountedPrice;
    }
}
