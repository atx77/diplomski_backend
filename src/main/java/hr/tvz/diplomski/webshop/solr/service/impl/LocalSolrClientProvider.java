package hr.tvz.diplomski.webshop.solr.service.impl;

import hr.tvz.diplomski.webshop.solr.service.SolrClientProvider;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalSolrClientProvider implements SolrClientProvider {

    @Value("${product.search.solr.url}")
    private String solrUrl;

    @Override
    public SolrClient getSolrClient() {
        SolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        return solrClient;
    }
}
