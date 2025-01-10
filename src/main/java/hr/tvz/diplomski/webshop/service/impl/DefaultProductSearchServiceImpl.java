package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Brand;
import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.domain.Product;
import hr.tvz.diplomski.webshop.dto.FacetDto;
import hr.tvz.diplomski.webshop.dto.FacetResultsWrapper;
import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import hr.tvz.diplomski.webshop.repository.ProductRepository;
import hr.tvz.diplomski.webshop.service.BrandService;
import hr.tvz.diplomski.webshop.service.CategoryService;
import hr.tvz.diplomski.webshop.service.ProductSearchService;
import hr.tvz.diplomski.webshop.util.ProductSearchSortBuilder;
import hr.tvz.diplomski.webshop.util.SpecificationBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "product.search.implementation", havingValue = "db")
public class DefaultProductSearchServiceImpl implements ProductSearchService {

    @Resource
    private ProductRepository productRepository;

    @Resource
    private BrandService brandService;

    @Resource
    private ConversionService conversionService;

    @Resource
    private SpecificationBuilder specificationBuilder;

    @Resource
    private ProductSearchSortBuilder productSearchSortBuilder;

    @Resource
    private CategoryService categoryService;

    @Override
    public ProductSearchResult findAllProductsInCategoryAndFilter(String categoryId, List<String> brandNames, List<String> categories,
                                                           BigDecimal minPrice, BigDecimal maxPrice, boolean isOnSale,
                                                           SortType sortType, int page, int pageSize) {
        Optional<Category> category = categoryService.getCategoryForCode(categoryId);
        if (!category.isPresent()) {
            throw new IllegalArgumentException("No category with code " + categoryId);
        }

        List<Brand> brands = new ArrayList<>();
        if (brandNames != null) {
            brands = brandService.getBrandsForNames(brandNames);
        }

        List<Category> parentAndChildCategories = new ArrayList<>();
        parentAndChildCategories.add(category.get());
        parentAndChildCategories.addAll(category.get().getSubCategories());
        Specification<Product> productSpecification = specificationBuilder.build(parentAndChildCategories, categories,
                brands, minPrice, maxPrice, isOnSale, null);
        return executeAndGetProductSearchResult(sortType, page, pageSize, productSpecification);
    }

    @Override
    public ProductSearchResult findAllProductsByTextAndFilter(String searchText, List<String> brandNames,
                                                              List<String> categories, BigDecimal minPrice,
                                                              BigDecimal maxPrice, boolean isOnSale, SortType sortType,
                                                              int page, int pageSize) {
        List<Brand> brands = new ArrayList<>();
        if (brandNames != null) {
            brands = brandService.getBrandsForNames(brandNames);
        }

        Specification<Product> productSpecification = specificationBuilder.build(null, categories,
                brands, minPrice, maxPrice, isOnSale, searchText);
        return executeAndGetProductSearchResult(sortType, page, pageSize, productSpecification);
    }

    private ProductSearchResult executeAndGetProductSearchResult(SortType sortType, int page, int pageSize, Specification<Product> productSpecification) {
        Sort sort = productSearchSortBuilder.build(sortType);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> productPage = productRepository.findAll(productSpecification, pageable);
        List<ProductDto> products = (List<ProductDto>) conversionService.convert(
                productPage.getContent(),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Product.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ProductDto.class)));

        List<Object[]> brandFacetResults = productRepository.findBrandFacet(productSpecification);
        List<Object[]> categoryFacetResults = productRepository.findCategoryFacet(productSpecification);

        List<FacetDto> brandFacets = brandFacetResults.stream()
                .map(result -> new FacetDto((String) result[0], ((Long) result[1])))
                .sorted(Comparator.comparing(FacetDto::getCount).reversed())
                .collect(Collectors.toList());

        List<FacetDto> categoryFacets = categoryFacetResults.stream()
                .map(result -> new FacetDto((String) result[0], ((Long) result[1])))
                .sorted(Comparator.comparing(FacetDto::getCount).reversed())
                .collect(Collectors.toList());

        FacetResultsWrapper facets = new FacetResultsWrapper();
        facets.setBrands(brandFacets);
        facets.setCategories(categoryFacets);

        ProductSearchResult productSearchResult = new ProductSearchResult();
        productSearchResult.setCurrentPage(productPage.getNumber() + 1);
        productSearchResult.setTotalResults(productPage.getTotalElements());
        productSearchResult.setTotalPages(productPage.getTotalPages());
        productSearchResult.setProducts(products);
        productSearchResult.setFacets(facets);
        productSearchResult.setSortCodes(Arrays.stream(SortType.values()).map(SortType::name).distinct().collect(Collectors.toList()));

        return productSearchResult;
    }
}
