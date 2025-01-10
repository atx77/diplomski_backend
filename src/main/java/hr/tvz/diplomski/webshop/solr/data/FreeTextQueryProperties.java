package hr.tvz.diplomski.webshop.solr.data;

import lombok.Data;

@Data
public class FreeTextQueryProperties {
    private boolean enabled;
    private int minTermLength;
    private float boost;
}
