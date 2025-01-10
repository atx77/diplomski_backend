package hr.tvz.diplomski.webshop.solr.data;

import lombok.Data;

@Data
public class FieldQueryProperties {
    private boolean enabled;
    private FreeTextQueryProperties freeTextQuery;
    private FreeTextFuzzyQueryProperties freeTextFuzzyQuery;
    private FreeTextWildcardQueryProperties freeTextWildcardQuery;
    private FreeTextPhraseQueryProperties freeTextPhraseQuery;
}
