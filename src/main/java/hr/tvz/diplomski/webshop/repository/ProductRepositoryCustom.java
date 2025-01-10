package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Object[]> findBrandFacet(Specification<Product> spec);
    List<Object[]> findCategoryFacet(Specification<Product> spec);
}
