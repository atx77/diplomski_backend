package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.domain.Product;
import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getModelForId(Long productId);

    ProductSearchResult findAllProductsInCategoryAndFilter(String categoryId, List<String> brandNames, List<String> categories,
                                                           BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                           SortType sortType, int page, int pageSize);

    ProductSearchResult findAllProductsByTextAndFilter(String searchText, List<String> brandNames, List<String> categories,
                                                       BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                       SortType sortType, int page, int pageSize);

    ProductDto getForId(Long productId);

    List<ProductDto> findNewestProducts();
}
