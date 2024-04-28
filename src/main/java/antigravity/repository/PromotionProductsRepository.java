package antigravity.repository;

import antigravity.domain.entity.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PromotionProductsRepository extends JpaRepository<PromotionProducts, Long> {

    @Query(value = """
        select p from PromotionProducts p
        join fetch p.promotion
        where p.product.id=:productId 
            and p.promotion.id in :promotionIds
            and p.promotion.use_ended_at >=:date 
            and p.promotion.use_started_at <=:date
    """)
    List<PromotionProducts> getAvailablePromotionByProduct(long productId, List<Long> promotionIds, LocalDate date);
}
