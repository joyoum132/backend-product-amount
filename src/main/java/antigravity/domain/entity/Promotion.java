package antigravity.domain.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 타입 (쿠폰 : 금액 할인, 코드 : %할인)")
    private PromotionType promotion_type;

    @Column(nullable = false)
    @Comment("쿠폰 이름")
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("WON : 금액 할인, PERCENT : %할인")
    public DiscountType discount_type;

    @Column(nullable = false)
    @Comment("할인 금액 or 할인 %")
    private int discount_value;

    @Column(nullable = false)
    @Comment("쿠폰 사용가능 시작 기간")
    private LocalDate use_started_at;

    @Column(nullable = false)
    @Comment("쿠폰 사용가능 종료 기간")
    private LocalDate use_ended_at;

    public enum PromotionType{
        CODE, COUPON
    }

    public enum DiscountType{
        PERCENT, WON
    }

    public Promotion(PromotionType promotion_type, String name, DiscountType discount_type, int discount_value, LocalDate use_started_at, LocalDate use_ended_at) {
        this.promotion_type = promotion_type;
        this.name = name;
        this.discount_type = discount_type;
        this.discount_value = discount_value;
        this.use_started_at = use_started_at;
        this.use_ended_at = use_ended_at;
    }
}
