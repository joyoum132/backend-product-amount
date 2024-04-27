package antigravity.repository;

import antigravity.domain.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product, Long> {


//    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    public Product getProduct(int id) {
//        String query = "SELECT * FROM `product` WHERE id = :id ";
//
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("id", id);
//
//        return namedParameterJdbcTemplate.queryForObject(
//                query,
//                params,
//                (rs, rowNum) -> Product.builder()
//                        .id(rs.getInt("id"))
//                        .name(rs.getString("name"))
//                        .price(rs.getInt("price"))
//                        .build()
//        );
//    }
}
