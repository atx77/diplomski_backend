package hr.tvz.diplomski.webshop.solr.data;

import lombok.Data;

@Data
public class FreeTextPhraseQueryProperties {
    private boolean enabled;
    private float boost;
    private int slop;
}
