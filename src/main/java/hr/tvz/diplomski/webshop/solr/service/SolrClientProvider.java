package hr.tvz.diplomski.webshop.solr.service;

import org.apache.solr.client.solrj.SolrClient;

public interface SolrClientProvider {
    SolrClient getSolrClient();
}
