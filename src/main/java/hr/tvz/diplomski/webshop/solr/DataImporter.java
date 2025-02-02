package hr.tvz.diplomski.webshop.solr;

import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.domain.Product;
import hr.tvz.diplomski.webshop.solr.data.SolrFieldsEnum;
import hr.tvz.diplomski.webshop.solr.service.SolrClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/solr")
public class DataImporter {

    @Resource
    private SolrClientProvider solrClientProvider;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public void update() throws Exception {
        SolrClient solrClient = solrClientProvider.getSolrClient();

        int batchSize = 1000;
        Long lastProcessedId = 0L;
        boolean hasMoreProducts = true;

        try {
            while (hasMoreProducts) {
                List<Product> products = fetchProductsBatch(lastProcessedId, batchSize);

                if (products.isEmpty()) {
                    hasMoreProducts = false;
                    break;
                }

                List<SolrInputDocument> solrDocs = new ArrayList<>();
                for (Product product : products) {
                    SolrInputDocument doc = createSolrDocument(product);
                    solrDocs.add(doc);
                    lastProcessedId = product.getId();
                }

                solrClient.add(solrDocs);
                solrClient.commit();
                entityManager.clear();
                log.info("Processed up to product ID: {}", lastProcessedId);
            }
        } catch (Exception e) {
            log.error("Error during Solr indexing: ", e);
            throw e;
        } finally {
            solrClient.close();
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete() throws SolrServerException, IOException {
        SolrClient solrClient = solrClientProvider.getSolrClient();

        solrClient.deleteByQuery("*:*");

        solrClient.commit();
        System.out.println("Documents deleted");
    }

    private List<Product> fetchProductsBatch(Long lastProcessedId, int batchSize) {
        String jpql = "SELECT p FROM product p WHERE p.id > :lastId ORDER BY p.id ASC";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("lastId", lastProcessedId);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }

    private SolrInputDocument createSolrDocument(Product product) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(SolrFieldsEnum.ID.getName(), product.getId().toString());
        doc.addField(SolrFieldsEnum.NAME.getName(), product.getName());
        doc.addField(SolrFieldsEnum.BRAND.getName(), product.getBrand() != null ? product.getBrand().getName() : null);
        doc.addField(SolrFieldsEnum.REGULAR_PRICE.getName(), product.getRegularPrice().doubleValue());
        if (product.getActionPrice() != null) {
            doc.addField(SolrFieldsEnum.ACTION_PRICE.getName(), product.getActionPrice().doubleValue());
        }
        doc.addField(SolrFieldsEnum.IMAGE_URL.getName(), product.getImageUrl());
        doc.addField(SolrFieldsEnum.CREATION_DATE.getName(), getFormattedDate(product.getCreationDate()));

        doc.addField(SolrFieldsEnum.CATEGORY_FACET.getName(),  product.getCategory() != null ?
                product.getCategory().getName() : null);
        doc.addField(SolrFieldsEnum.CATEGORY_CODE.getName(), getCategoryCodes(product));
        doc.addField(SolrFieldsEnum.CATEGORY.getName(), getCategoryNames(product));
        return doc;
    }

    private String getFormattedDate(Date creationDate) {
        if (creationDate != null) {
            ZonedDateTime creationDateTime = creationDate.toInstant().atZone(ZoneId.of("UTC"));
            String creationDateStr = creationDateTime.format(DateTimeFormatter.ISO_INSTANT);
            return creationDateStr;
        }
        return null;
    }

    private List<String> getCategoryCodes(Product product) {
        List<String> categoryCodes = new ArrayList<>();
        if (product.getCategory() != null) {
            Category category = product.getCategory();
            categoryCodes.add(category.getCode());
            if (category.getParentCategory() != null) {
                categoryCodes.add(category.getParentCategory().getCode());
            }
            return categoryCodes;
        }
        return null;
    }

    private List<String> getCategoryNames(Product product) {
        List<String> categoryNames = new ArrayList<>();
        if (product.getCategory() != null) {
            Category category = product.getCategory();
            categoryNames.add(category.getName());
            if (category.getParentCategory() != null) {
                categoryNames.add(category.getParentCategory().getName());
            }
            return categoryNames;
        }
        return null;
    }
}
