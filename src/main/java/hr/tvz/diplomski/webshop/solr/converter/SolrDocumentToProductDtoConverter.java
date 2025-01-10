package hr.tvz.diplomski.webshop.solr.converter;

import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.solr.data.SolrFieldsEnum;
import org.apache.solr.common.SolrDocument;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class SolrDocumentToProductDtoConverter implements Converter<SolrDocument, ProductDto> {
    @Override
    public ProductDto convert(SolrDocument doc) {
        ProductDto product = new ProductDto();
        product.setCode(Long.valueOf((String) doc.getFieldValue(SolrFieldsEnum.ID.getName())));
        product.setName((String) doc.getFieldValue(SolrFieldsEnum.NAME.getName()));
        product.setBrand((String) doc.getFieldValue(SolrFieldsEnum.BRAND.getName()));
        product.setAvailableQuantity((Integer) doc.getFieldValue(SolrFieldsEnum.STOCK.getName()));
        product.setRegularPrice(new BigDecimal(doc.getFieldValue(SolrFieldsEnum.REGULAR_PRICE.getName()).toString()));
        product.setActionPrice(doc.getFieldValue(SolrFieldsEnum.ACTION_PRICE.getName()) != null
                ? new BigDecimal(doc.getFieldValue(SolrFieldsEnum.ACTION_PRICE.getName()).toString()) : null);
        product.setImageUrl((String) doc.getFieldValue(SolrFieldsEnum.IMAGE_URL.getName()));
        return product;
    }
}
