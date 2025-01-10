package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductSearchResult {
    private FacetResultsWrapper facets = new FacetResultsWrapper();
    private List<String> sortCodes;
    private List<ProductDto> products;
    private long totalResults;
    private int totalPages;
    private int currentPage;
}
