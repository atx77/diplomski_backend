package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>, ProductRepositoryCustom {
    List<Product> findTop5ByActiveIsTrueOrderByCreationDateDesc();

    @Query(value = "SELECT p.*, " +
            "MATCH(p.name) AGAINST(:searchText IN BOOLEAN MODE) AS relevance " +
            "FROM product p " +
            "WHERE MATCH(p.name) AGAINST(:searchText IN BOOLEAN MODE) " +
            "ORDER BY relevance DESC " +
            "LIMIT :offset, :pageSize", nativeQuery = true)
    List<Product> findFullTextSearchResults(@Param("searchText") String searchText,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    @Query(value = " SELECT COUNT(*) " +
            "FROM product p " +
            "WHERE MATCH(p.name) AGAINST(:searchText IN BOOLEAN MODE)", nativeQuery = true)
    long countFullTextSearchResults(@Param("searchText") String searchText);
}
