package hr.tvz.diplomski.webshop.solr.data;

import lombok.Data;

@Data
public class FreeTextWildcardQueryProperties {
    private boolean enabled;
    private int minTermLength;
    private float boost;
    private WildcardQueryType type;
}
