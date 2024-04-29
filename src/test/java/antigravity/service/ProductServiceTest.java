package antigravity.service;

import antigravity.config.exception.BadRequestException;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PromotionProductsRepository promotionProductsRepository;

    @Autowired
    PromotionRepository promotionRepository;

    Product product = null;
    List<Promotion> expiredPromotions = new ArrayList<>();
    List<Promotion> validPromotions = new ArrayList<>();
    List<PromotionProducts> promotionProducts = new ArrayList<>();

    @BeforeEach
    void before() {
        product = productRepository.save(new Product("상품1", 365500));

        //3,4 : 기간 만료된 프로모션
        //5,6 : 사용 가능한 프로모션
        expiredPromotions = promotionRepository.saveAll(
                List.of(
                        new Promotion(Promotion.PromotionType.CODE, "15% 할인 쿠폰", Promotion.DiscountType.PERCENT, 15, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 12, 31)),
                        new Promotion(Promotion.PromotionType.COUPON, "5만원 할인 쿠폰", Promotion.DiscountType.WON, 50000, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 12, 31))
                )
        );

        validPromotions = promotionRepository.saveAll(
                List.of(
                        new Promotion(Promotion.PromotionType.CODE, "10% 할인 쿠폰", Promotion.DiscountType.PERCENT, 10, LocalDate.of(2020, 1, 1), LocalDate.of(2025, 12, 31)),
                        new Promotion(Promotion.PromotionType.COUPON, "10만원 할인 쿠폰", Promotion.DiscountType.WON, 100000, LocalDate.of(2020, 1, 1), LocalDate.of(2025, 12, 31))
                )
        );

        promotionProducts = promotionProductsRepository.saveAll(
                List.of(
                        new PromotionProducts(expiredPromotions.get(0)  , product),
                        new PromotionProducts(expiredPromotions.get(1)  , product),
                        new PromotionProducts(validPromotions.get(0)  , product),
                        new PromotionProducts(validPromotions.get(1)  , product)
                )
        );
    }

    @AfterEach
    void after() {
        promotionProductsRepository.deleteAllByIdInBatch(
                promotionProducts.stream().map(PromotionProducts::getId).toList()
        );
        promotionRepository.deleteAllByIdInBatch(
                expiredPromotions.stream().map(Promotion::getId).toList()
        );
        promotionRepository.deleteAllByIdInBatch(
                validPromotions.stream().map(Promotion::getId).toList()
        );
        productRepository.deleteById(product.getId());
    }


    @Test
    @DisplayName("사용 가능한 프로모션 조회")
    void 사용_가능한_프로모션_조회() {

        List<Long> ids = expiredPromotions.stream().map(Promotion::getId).toList();
        List<PromotionProducts> noPromotions = promotionProductsRepository
                .getAvailablePromotionByProduct(product.getId(), ids, LocalDate.now());
        Assertions.assertTrue(noPromotions.isEmpty());


        ids = validPromotions.stream().map(Promotion::getId).toList();
        List<PromotionProducts> validPromotions = promotionProductsRepository
                .getAvailablePromotionByProduct(product.getId(), ids, LocalDate.now());

        Assertions.assertFalse(validPromotions.isEmpty());
    }

    @Test
    @DisplayName("상품 가격은 천원 ~ 천만원")
    void 상품_금액_확인() {
        List<Product> validProducts = List.of(
                new Product(1L, "상품1", 1000),
                new Product(2L, "상품2", 23456),
                new Product(3L, "상품3", 10000000)
        );

        validProducts.forEach(it ->
            Assertions.assertTrue(it.isValidRange())
        );

        List<Product> invalidProducts = List.of(
                new Product(4L, "상품1", 999),
                new Product(5L, "상품3", 10000001)
        );

        invalidProducts.forEach(it ->
                Assertions.assertFalse(it.isValidRange())
        );
    }


    @Test
    @DisplayName("프로모션 적용 후 가격 비교")
    void 프로모션_적용_후_가격_비교() {
        int discountedPrice = productService.applyPromotion(product, validPromotions);
        Assertions.assertEquals(discountedPrice, 228950);
    }

    @Test
    @DisplayName("상품 가격 검증 실패")
    void 상품가격_검증_실패() {
        product.setPrice(1);
        productRepository.save(product);

        List<Long> promotionIds = validPromotions.stream().map(Promotion::getId).toList();

        ProductInfoRequest req = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(promotionIds)
                .build();

        Throwable t = Assertions.assertThrows(
        BadRequestException.class,
                () -> productService.getProductAmount(req)
        );
        Assertions.assertEquals(t.getMessage(), "상품 정보가 잘못되었습니다.");
    }


    @Test
    @DisplayName("사용가능한 프로모션이 없어서 실패")
    void 사용_가능한_프로모션이_없어서_실패() {
        List<Long> ids = expiredPromotions.stream().map(Promotion::getId).toList();

        ProductInfoRequest req = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(ids)
                .build();

        Throwable t = Assertions.assertThrows(
                BadRequestException.class,
                () -> productService.getProductAmount(req)
        );
        Assertions.assertEquals(t.getMessage(), "적용 가능한 프로모션이 없습니다.");
    }

    @Test
    @DisplayName("모든 프로모션 적용")
    void 모든_프로모션_적용_성공() {
        List<Long> ids = validPromotions.stream().map(Promotion::getId).toList();

        ProductInfoRequest req = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(ids)
                .build();

        ProductAmountResponse res = productService.getProductAmount(req);

        Assertions.assertEquals(res.getDiscountPrice(), 137500);
        Assertions.assertEquals(res.getFinalPrice(), 228000);
    }

    @Test
    @DisplayName("일부 프로모션 적용")
    void 일부_프로모션_적용_성공() {
        List<Long> ids = List.of(
                expiredPromotions.get(0).getId(),
                validPromotions.get(0).getId()
        );

        ProductInfoRequest req = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(ids)
                .build();

        ProductAmountResponse res = productService.getProductAmount(req);

        Assertions.assertEquals(res.getDiscountPrice(), 37500);
        Assertions.assertEquals(res.getFinalPrice(), 328000);
    }


    @Test
    @DisplayName("파라미터 검증")
    void 요청_파라미터_검증() {
        ProductInfoRequest idIsNull = ProductInfoRequest.builder()
                .productId(null)
                .couponIds(List.of(1L, 2L))
                .build();

        ProductInfoRequest isIsNegative = ProductInfoRequest.builder()
                .productId(-1L)
                .couponIds(List.of(1L, 2L))
                .build();

        ProductInfoRequest couponIsEmpty = ProductInfoRequest.builder()
                .productId(1L)
                .couponIds(List.of())
                .build();

        Assertions.assertThrows(
                BadRequestException.class,
                () -> productService.getProductAmount(idIsNull)
        );

        Assertions.assertThrows(
                BadRequestException.class,
                () -> productService.getProductAmount(isIsNegative)
        );

        Assertions.assertThrows(
                BadRequestException.class,
                () -> productService.getProductAmount(couponIsEmpty)
        );
    }
}