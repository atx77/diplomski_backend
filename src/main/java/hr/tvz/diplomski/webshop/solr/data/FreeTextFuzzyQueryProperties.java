package hr.tvz.diplomski.webshop.solr.data;

import lombok.Data;

@Data
public class FreeTextFuzzyQueryProperties {
    private boolean enabled;
    private int minTermLength;
    private float boost;
    private int fuzziness;
}
