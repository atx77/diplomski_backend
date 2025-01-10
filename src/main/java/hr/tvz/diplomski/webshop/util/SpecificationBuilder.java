package hr.tvz.diplomski.webshop.util;

import hr.tvz.diplomski.webshop.domain.Brand;
import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public interface SpecificationBuilder {
    Specification<Product> build(List<Category> categories, List<String> categoryNames, List<Brand> brands,
                                 BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale, String searchString);
}
