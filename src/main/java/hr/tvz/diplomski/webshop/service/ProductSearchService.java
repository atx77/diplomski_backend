package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchService {
    ProductSearchResult findAllProductsInCategoryAndFilter(String categoryId, List<String> brandNames, List<String> categories,
                                                           BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                           SortType sortType, int page, int pageSize);

    ProductSearchResult findAllProductsByTextAndFilter(String searchText, List<String> brandNames, List<String> categories,
                                                       BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                       SortType sortType, int page, int pageSize);
}
