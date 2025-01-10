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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "product.search.implementation", havingValue = "db_fulltext")
public class FullTextProductSearchServiceImpl implements ProductSearchService {

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

    @PersistenceContext
    private EntityManager entityManager;

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
        String booleanModeSearch = transformToBooleanMode(searchText);

        List<Brand> brands = brandNames != null && !brandNames.isEmpty() ? brandService.getBrandsForNames(brandNames) : Collections.emptyList();
        List<Long> brandIds = brands.stream().map(Brand::getId).collect(Collectors.toList());

        List<String> conditions = new ArrayList<>();
        conditions.add("p.active = 1");

        conditions.add("(" +
                "MATCH(p.name) AGAINST(:searchText IN BOOLEAN MODE) " +
                "OR MATCH(c.name) AGAINST(:searchText IN BOOLEAN MODE) " +
                "OR MATCH(b.name) AGAINST(:searchText IN BOOLEAN MODE)" +
                ")");

        if (categories != null && !categories.isEmpty()) {
            conditions.add("c.name IN :categoryNames");
        }

        if (isOnSale) {
            conditions.add("p.action_price IS NOT NULL");
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            conditions.add("p.regular_price >= :minPrice");
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            conditions.add("p.regular_price <= :maxPrice");
        }

        if (!brandIds.isEmpty()) {
            conditions.add("b.id IN :brandIds");
        }

        String whereClause = String.join(" AND ", conditions);

        String orderByClause;
        switch (sortType) {
            case PRICE_ASC:
                orderByClause = "ORDER BY p.regular_price ASC";
                break;
            case PRICE_DESC:
                orderByClause = "ORDER BY p.regular_price DESC";
                break;
            default:
                orderByClause = "ORDER BY (" +
                        "MATCH(p.name) AGAINST(:searchText IN BOOLEAN MODE)*1.5 + " +
                        "MATCH(c.name) AGAINST(:searchText IN BOOLEAN MODE)*1.2 + " +
                        "MATCH(b.name) AGAINST(:searchText IN BOOLEAN MODE)*1.0) DESC";
        }

        String mainQueryStr = "SELECT p.* " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "LEFT JOIN brand b ON p.brand_id = b.id " +
                "WHERE " + whereClause + " " +
                orderByClause + " " +
                "LIMIT :limit OFFSET :offset";

        Query mainQuery = entityManager.createNativeQuery(mainQueryStr, Product.class);
        mainQuery.setParameter("searchText", booleanModeSearch);
        mainQuery.setParameter("limit", pageSize);
        mainQuery.setParameter("offset", (page - 1) * pageSize);

        if (!brandIds.isEmpty()) {
            mainQuery.setParameter("brandIds", brandIds);
        }
        if (categories != null && !categories.isEmpty()) {
            mainQuery.setParameter("categoryNames", categories);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            mainQuery.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            mainQuery.setParameter("maxPrice", maxPrice);
        }

        List<Product> productList = mainQuery.getResultList();

        String countQueryStr = "SELECT COUNT(DISTINCT p.id) " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "LEFT JOIN brand b ON p.brand_id = b.id " +
                "WHERE " + whereClause;

        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        countQuery.setParameter("searchText", booleanModeSearch);

        if (!brandIds.isEmpty()) {
            countQuery.setParameter("brandIds", brandIds);
        }
        if (categories != null && !categories.isEmpty()) {
            countQuery.setParameter("categoryNames", categories);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            countQuery.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            countQuery.setParameter("maxPrice", maxPrice);
        }

        Number totalResults = (Number) countQuery.getSingleResult();

        List<ProductDto> productDtos = (List<ProductDto>) conversionService.convert(
                productList,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Product.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ProductDto.class))
        );

        String brandFacetQueryStr = "SELECT b.name, COUNT(DISTINCT p.id) " +
                "FROM product p " +
                "LEFT JOIN brand b ON p.brand_id = b.id " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "WHERE " + whereClause + " " +
                "GROUP BY b.name";
        Query brandFacetQuery = entityManager.createNativeQuery(brandFacetQueryStr);
        brandFacetQuery.setParameter("searchText", booleanModeSearch);

        if (!brandIds.isEmpty()) {
            brandFacetQuery.setParameter("brandIds", brandIds);
        }
        if (categories != null && !categories.isEmpty()) {
            brandFacetQuery.setParameter("categoryNames", categories);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            brandFacetQuery.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            brandFacetQuery.setParameter("maxPrice", maxPrice);
        }

        List<Object[]> brandFacetResults = brandFacetQuery.getResultList();
        List<FacetDto> brandFacets = brandFacetResults.stream()
                .map(r -> new FacetDto((String) r[0], ((Number) r[1]).longValue()))
                .sorted(Comparator.comparing(FacetDto::getCount).reversed())
                .collect(Collectors.toList());

        String categoryFacetQueryStr = "SELECT c.name, COUNT(DISTINCT p.id) " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "LEFT JOIN brand b ON p.brand_id = b.id " +
                "WHERE " + whereClause + " " +
                "GROUP BY c.name";
        Query categoryFacetQuery = entityManager.createNativeQuery(categoryFacetQueryStr);
        categoryFacetQuery.setParameter("searchText", booleanModeSearch);

        if (!brandIds.isEmpty()) {
            categoryFacetQuery.setParameter("brandIds", brandIds);
        }
        if (categories != null && !categories.isEmpty()) {
            categoryFacetQuery.setParameter("categoryNames", categories);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            categoryFacetQuery.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            categoryFacetQuery.setParameter("maxPrice", maxPrice);
        }

        List<Object[]> categoryFacetResults = categoryFacetQuery.getResultList();
        List<FacetDto> categoryFacets = categoryFacetResults.stream()
                .map(r -> new FacetDto((String) r[0], ((Number) r[1]).longValue()))
                .sorted(Comparator.comparing(FacetDto::getCount).reversed())
                .collect(Collectors.toList());

        FacetResultsWrapper facets = new FacetResultsWrapper();
        facets.setBrands(brandFacets);
        facets.setCategories(categoryFacets);

        ProductSearchResult productSearchResult = new ProductSearchResult();
        productSearchResult.setCurrentPage(page);
        productSearchResult.setTotalResults(totalResults.longValue());
        long totalPages = (long) Math.ceil((double) totalResults.longValue() / pageSize);
        productSearchResult.setTotalPages((int) totalPages);
        productSearchResult.setProducts(productDtos);
        productSearchResult.setFacets(facets);
        productSearchResult.setSortCodes(Arrays.stream(SortType.values()).map(Enum::name).distinct().collect(Collectors.toList()));

        return productSearchResult;
    }

    private String transformToBooleanMode(String searchText) {
        String[] terms = searchText.trim().split("\\s+");
        return Arrays.stream(terms)
                .collect(Collectors.joining(" "));
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
