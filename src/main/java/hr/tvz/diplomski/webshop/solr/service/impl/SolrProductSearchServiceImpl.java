package hr.tvz.diplomski.webshop.solr.service.impl;

import hr.tvz.diplomski.webshop.dto.FacetDto;
import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.dto.ProductSearchResult;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import hr.tvz.diplomski.webshop.service.ProductSearchService;
import hr.tvz.diplomski.webshop.solr.data.SolrFieldsEnum;
import hr.tvz.diplomski.webshop.solr.helper.SolrQueryBuilder;
import hr.tvz.diplomski.webshop.solr.service.SolrClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "product.search.implementation", havingValue = "solr")
public class SolrProductSearchServiceImpl implements ProductSearchService {

    @Resource
    private SolrClientProvider solrClientProvider;

    @Resource
    private SolrQueryBuilder solrQueryBuilder;

    @Resource
    private ConversionService conversionService;

    @Override
    public ProductSearchResult findAllProductsInCategoryAndFilter(String categoryId, List<String> brandNames,
                                                                  List<String> categories, BigDecimal minPrice,
                                                                  BigDecimal maxPrice, boolean isOnSale,
                                                                  SortType sortType, int page, int pageSize) {
        try {
            SolrClient solrClient = solrClientProvider.getSolrClient();
            SolrQuery solrQuery = new SolrQuery();

            String q = solrQueryBuilder.buildMainCategoryQuery(categoryId);
            solrQuery.setQuery(q);

            List<String> filterQueries = buildFilterQueries(brandNames, categories, minPrice, maxPrice, isOnSale);
            if (!filterQueries.isEmpty()) {
                solrQuery.setFilterQueries(filterQueries.toArray(new String[0]));
            }

            setSorting(solrQuery, sortType);

            setFacets(solrQuery);

            setPagination(solrQuery, page, pageSize);

            solrQuery.setFields(Arrays.stream(SolrFieldsEnum.values()).map(SolrFieldsEnum::getName)
                    .collect(Collectors.joining(",")));

//            solrQuery.setShowDebugInfo(true);

            return getAndProcessResults(solrClient, solrQuery, page, pageSize);
        } catch (Exception e) {
            log.error(null, e);
        }

        return new ProductSearchResult();
    }

    @Override
    public ProductSearchResult findAllProductsByTextAndFilter(String searchText, List<String> brandNames,
                                                              List<String> categories, BigDecimal minPrice,
                                                              BigDecimal maxPrice, boolean isOnSale, SortType sortType,
                                                              int page, int pageSize) {
        try {
            SolrClient solrClient = solrClientProvider.getSolrClient();
            SolrQuery solrQuery = new SolrQuery();

            String q = solrQueryBuilder.buildMainSearchQuery(searchText);
            solrQuery.setQuery(q);

            List<String> filterQueries = buildFilterQueries(brandNames, categories, minPrice, maxPrice, isOnSale);
            if (!filterQueries.isEmpty()) {
                solrQuery.setFilterQueries(filterQueries.toArray(new String[0]));
            }

            setSorting(solrQuery, sortType);

            setFacets(solrQuery);

            setPagination(solrQuery, page, pageSize);

            solrQuery.setFields(Arrays.asList(SolrFieldsEnum.ID, SolrFieldsEnum.NAME, SolrFieldsEnum.BRAND,
                            SolrFieldsEnum.REGULAR_PRICE, SolrFieldsEnum.ACTION_PRICE, SolrFieldsEnum.CATEGORY,
                            SolrFieldsEnum.IMAGE_URL, SolrFieldsEnum.CATEGORY_CODE, SolrFieldsEnum.SCORE).stream().map(SolrFieldsEnum::getName)
                    .collect(Collectors.joining(",")));

            return getAndProcessResults(solrClient, solrQuery, page, pageSize);
        } catch (Exception e) {
            log.error(null, e);
        }

        return new ProductSearchResult();
    }

    private ProductSearchResult getAndProcessResults(SolrClient solrClient, SolrQuery solrQuery, int page, int pageSize)
            throws SolrServerException, IOException {
        ProductSearchResult searchResult = new ProductSearchResult();
        List<ProductDto> products = new ArrayList<>();
        long totalResults = 0;
        int totalPages = 0;

        QueryResponse response = solrClient.query(solrQuery);

        totalResults = response.getResults().getNumFound();

        totalPages = (int) Math.ceil((double) totalResults / pageSize);

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            solrQuery.setStart((page - 1) * pageSize);
            response = solrClient.query(solrQuery);
        }

        SolrDocumentList results = response.getResults();
        products = (List<ProductDto>) conversionService.convert(results,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(SolrDocument.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ProductDto.class)));

        searchResult.setProducts(products);
        searchResult.setTotalResults(totalResults);
        searchResult.setTotalPages(totalPages);
        searchResult.setCurrentPage(page);

        if (response.getFacetFields() != null) {
            for (FacetField facetField : response.getFacetFields()) {
                if (SolrFieldsEnum.BRAND_FACET.getName().equals(facetField.getName())) {
                    searchResult.getFacets().setBrands(processFacet(facetField));
                } else if (SolrFieldsEnum.CATEGORY_FACET.getName().equals(facetField.getName())) {
                    searchResult.getFacets().setCategories(processFacet(facetField));
                }
            }
        }

        searchResult.setSortCodes(Arrays.stream(SortType.values()).map(SortType::name).distinct().collect(Collectors.toList()));

        return searchResult;
    }

    private void setPagination(SolrQuery solrQuery, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        solrQuery.setStart(start);
        solrQuery.setRows(pageSize);
    }

    private void setFacets(SolrQuery solrQuery) {
        solrQuery.setFacet(true);
        solrQuery.addFacetField(SolrFieldsEnum.CATEGORY_FACET.getName());
        solrQuery.addFacetField(SolrFieldsEnum.BRAND_FACET.getName());
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacetSort("count");
        solrQuery.setFacetMinCount(1);
    }

    private List<FacetDto> processFacet(FacetField facetField) {
        List<FacetDto> facetDtos = new ArrayList<>();
        for (FacetField.Count count : facetField.getValues()) {
            FacetDto facetDto = new FacetDto();
            facetDto.setValue(count.getName());
            facetDto.setCount(count.getCount());
            facetDtos.add(facetDto);
        }
        return facetDtos;
    }

    private List<String> buildFilterQueries(List<String> brandNames, List<String> categories, BigDecimal minPrice,
                                            BigDecimal maxPrice, boolean isOnSale) {
        List<String> filterQueries = new ArrayList<>();

        if (brandNames != null && !brandNames.isEmpty()) {
            String brands = brandNames.stream()
                    .map(brand -> "\"" + ClientUtils.escapeQueryChars(brand) + "\"")
                    .collect(Collectors.joining(" OR "));
            filterQueries.add("brand:(" + brands + ")");
        }

        if (categories != null && !categories.isEmpty()) {
            String brands = categories.stream()
                    .map(category -> "\"" + ClientUtils.escapeQueryChars(category) + "\"")
                    .collect(Collectors.joining(" OR "));
            filterQueries.add("category:(" + brands + ")");
        }

        if (minPrice != null || maxPrice != null) {
            StringBuilder priceFilter = new StringBuilder("regular_price:[");
            priceFilter.append(minPrice != null ? minPrice.toString() : "*");
            priceFilter.append(" TO ");
            priceFilter.append(maxPrice != null ? maxPrice.toString() : "*");
            priceFilter.append("]");
            filterQueries.add(priceFilter.toString());
        }

        if (isOnSale) {
            filterQueries.add("action_price:[* TO *]");
        }

        return filterQueries;
    }

    private void setSorting(SolrQuery solrQuery, SortType sortType) {
        if (sortType != null) {
            switch (sortType) {
                case PRICE_ASC:
                    solrQuery.addSort("if(exists(action_price), action_price, regular_price)", SolrQuery.ORDER.asc);
                    break;
                case PRICE_DESC:
                    solrQuery.addSort("if(exists(action_price), action_price, regular_price)", SolrQuery.ORDER.desc);
                    break;
                case DATE_ADDED_ASC:
                    solrQuery.addSort("creation_date", SolrQuery.ORDER.asc);
                    break;
                case DATE_ADDED_DESC:
                    solrQuery.addSort("creation_date", SolrQuery.ORDER.desc);
                    break;
                case RELEVANCE:
                    solrQuery.addSort("score", SolrQuery.ORDER.desc);
                    break;
                default:
                    solrQuery.addSort("score", SolrQuery.ORDER.desc);
                    break;
            }
        }
    }
}
