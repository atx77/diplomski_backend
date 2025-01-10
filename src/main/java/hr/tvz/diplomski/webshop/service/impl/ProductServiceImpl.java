package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Product;
import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import hr.tvz.diplomski.webshop.repository.ProductRepository;
import hr.tvz.diplomski.webshop.service.ProductSearchService;
import hr.tvz.diplomski.webshop.service.ProductService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductRepository productRepository;

    @Resource
    private ConversionService conversionService;

    @Resource
    private ProductSearchService productSearchService;

    @Override
    public ProductDto getForId(Long productId) {
        return conversionService.convert(productRepository.findById(productId).orElse(null), ProductDto.class);
    }

    @Override
    public Optional<Product> getModelForId(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public ProductSearchResult findAllProductsInCategoryAndFilter(String categoryId, List<String> brandNames, List<String> categories,
                                                                  BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                                  SortType sortType, int page, int pageSize) {
        return productSearchService.findAllProductsInCategoryAndFilter(categoryId, brandNames, categories, minPrice, maxPrice,
                isOnSale, sortType, page, pageSize);
    }

    @Override
    public ProductSearchResult findAllProductsByTextAndFilter(String searchText, List<String> brandNames, List<String> categories,
                                                              BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                              SortType sortType, int page, int pageSize) {
        return productSearchService.findAllProductsByTextAndFilter(searchText, brandNames, categories, minPrice, maxPrice,
                isOnSale, sortType, page, pageSize);
    }

    @Override
    public List<ProductDto> findNewestProducts() {
        return (List<ProductDto>) conversionService.convert(productRepository.findTop5ByActiveIsTrueOrderByCreationDateDesc(),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Product.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ProductDto.class)));
    }
}
