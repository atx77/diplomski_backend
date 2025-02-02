package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.dto.CategoryDto;
import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import hr.tvz.diplomski.webshop.service.CategoryService;
import hr.tvz.diplomski.webshop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private ProductService productService;

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public List<CategoryDto> getAllParent() {
        return categoryService.getAllParentCategories();
    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET)
    public ProductSearchResult getAllProductForCategoryAndFilters(@PathVariable("categoryId") String categoryId,
                                                                  @RequestParam(value = "brand", required = false) final List<String> brands,
                                                                  @RequestParam(value = "categories", required = false) final List<String> categories,
                                                                  @RequestParam(value = "minPrice", required = false) final BigDecimal minPrice,
                                                                  @RequestParam(value = "maxPrice", required = false) final BigDecimal maxPrice,
                                                                  @RequestParam(value = "isOnSale", required = false, defaultValue = "false") final boolean isOnSale,
                                                                  @RequestParam(value = "sort", required = false) final SortType sortType,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") final int page,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "25") final int pageSize) {
        long startTime = System.currentTimeMillis();

        ProductSearchResult productSearchResult =  productService.findAllProductsInCategoryAndFilter(categoryId, brands,
                categories, minPrice, maxPrice, isOnSale, sortType, page, pageSize);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Category search method execution time: " + executionTime + " milliseconds");

        return productSearchResult;
    }
}
