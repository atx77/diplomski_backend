package hr.tvz.diplomski.webshop.solr.helper;

import hr.tvz.diplomski.webshop.solr.config.SolrSearchProperties;
import hr.tvz.diplomski.webshop.solr.data.FieldQueryProperties;
import hr.tvz.diplomski.webshop.solr.data.SolrFieldsEnum;
import hr.tvz.diplomski.webshop.solr.data.WildcardQueryType;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SolrQueryBuilder {

    @Resource
    public SolrSearchProperties solrSearchProperties;

    public String buildMainCategoryQuery(String categoryId) {
        return SolrFieldsEnum.CATEGORY_CODE.getName() + ":" + categoryId;
    }

    public String buildMainSearchQuery(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "*:*";
        }

        StringBuilder mainQuery = new StringBuilder();
        List<String> fieldQueries = new ArrayList<>();

        List<String> fields = Arrays.asList(SolrFieldsEnum.NAME.getName(), SolrFieldsEnum.BRAND.getName(),
                SolrFieldsEnum.CATEGORY.getName());

        for (String field : fields) {
            FieldQueryProperties fieldProps = solrSearchProperties.getFieldProperties(field);
            if (fieldProps != null && fieldProps.isEnabled()) {

                List<String> queriesForField = buildQueriesForField(field, fieldProps, searchText);
                if (!queriesForField.isEmpty()) {

                    String fieldQuery = String.join(" OR ", queriesForField);
                    fieldQueries.add(fieldQuery);
                }
            }
        }

        if (!fieldQueries.isEmpty()) {
            mainQuery.append("(");
            mainQuery.append(String.join(" OR ", fieldQueries));
            mainQuery.append(")");
        } else {
            mainQuery.append("*:*");
        }

        return mainQuery.toString();
    }

    private List<String> buildQueriesForField(String fieldName, FieldQueryProperties fieldProps, String searchText) {
        List<String> queries = new ArrayList<>();
        String escapedSearchText = ClientUtils.escapeQueryChars(searchText);

        // Free Text Query
        if (fieldProps.getFreeTextQuery().isEnabled()
                && searchText.length() >= fieldProps.getFreeTextQuery().getMinTermLength()) {

            float boost = fieldProps.getFreeTextQuery().getBoost();
            String query = String.format("%s:%s^%.1f", fieldName, escapedSearchText, boost);
            queries.add(query);
        }

        // Free Text Fuzzy Query
        if (fieldProps.getFreeTextFuzzyQuery().isEnabled()
                && searchText.length() >= fieldProps.getFreeTextFuzzyQuery().getMinTermLength()) {

            int fuzziness = fieldProps.getFreeTextFuzzyQuery().getFuzziness();
            float boost = fieldProps.getFreeTextFuzzyQuery().getBoost();
            String query = String.format("%s:%s~%d^%.1f", fieldName, escapedSearchText, fuzziness, boost);
            queries.add(query);
        }

        // Free Text Wildcard Query
        if (fieldProps.getFreeTextWildcardQuery().isEnabled()
                && searchText.length() >= fieldProps.getFreeTextWildcardQuery().getMinTermLength()) {

            WildcardQueryType wildcardType = fieldProps.getFreeTextWildcardQuery().getType();
            float boost = fieldProps.getFreeTextWildcardQuery().getBoost();
            String wildcardSearchText = getWildcardSearchText(escapedSearchText, wildcardType);
            String query = String.format("%s:%s^%.1f", fieldName, wildcardSearchText, boost);
            queries.add(query);
        }

        // Free Text Phrase Query
        if (fieldProps.getFreeTextPhraseQuery().isEnabled()) {

            float boost = fieldProps.getFreeTextPhraseQuery().getBoost();
            int slop = fieldProps.getFreeTextPhraseQuery().getSlop();
            String query = String.format("%s:\"%s\"~%d^%.1f", fieldName, escapedSearchText, slop, boost);
            queries.add(query);
        }

        return queries;
    }

    private String getWildcardSearchText(String searchText, WildcardQueryType wildcardQueryType) {
        switch (wildcardQueryType) {
            case LEADING:
                return "*" + searchText;
            case TRAILING:
                return searchText + "*";
            case LEADING_AND_TRAILING:
                return "*" + searchText + "*";
            default:
                return searchText + "*";
        }
    }
}
