package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import hr.tvz.diplomski.webshop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/search")
public class SearchController {

    @Resource
    private ProductService productService;

    @RequestMapping(value = "/{text}", method = RequestMethod.GET)
    public ProductSearchResult searchProducts(@PathVariable("text") final String searchText,
                                              @RequestParam(value = "brand", required = false) final List<String> brands,
                                              @RequestParam(value = "categories", required = false) final List<String> categories,
                                              @RequestParam(value = "minPrice", required = false) final BigDecimal minPrice,
                                              @RequestParam(value = "maxPrice", required = false) final BigDecimal maxPrice,
                                              @RequestParam(value = "isOnSale", required = false, defaultValue = "false") final boolean isOnSale,
                                              @RequestParam(value = "sort", required = false) final SortType sortType,
                                              @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "25") final int pageSize) {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0.");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than 0.");
        }
        long startTime = System.currentTimeMillis();

        ProductSearchResult productSearchResult = productService.findAllProductsByTextAndFilter(searchText, brands, categories,
                minPrice, maxPrice, isOnSale, sortType, page, pageSize);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Text search method execution time: " + executionTime + " milliseconds");

        return productSearchResult;
    }
}
