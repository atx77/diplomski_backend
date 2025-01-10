package hr.tvz.diplomski.webshop.solr.config;

import hr.tvz.diplomski.webshop.solr.data.FieldQueryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "solr.search.product")
public class SolrSearchProperties {
    private Map<String, FieldQueryProperties> fields = new HashMap<>();

    public FieldQueryProperties getFieldProperties(String fieldName) {
        return fields.get(fieldName);
    }

    public Map<String, FieldQueryProperties> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldQueryProperties> fields) {
        this.fields = fields;
    }
}
