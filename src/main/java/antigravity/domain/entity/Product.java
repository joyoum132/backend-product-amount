package antigravity.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
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
}
