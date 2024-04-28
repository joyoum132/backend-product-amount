package antigravity.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("상품명")
    private String name;

    @Column(nullable = false)
    @Comment("금액")
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public boolean isValidRange() {
        return price >= 1000 && price <= 10000000;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
