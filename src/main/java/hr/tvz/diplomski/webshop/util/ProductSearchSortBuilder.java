package hr.tvz.diplomski.webshop.util;

import hr.tvz.diplomski.webshop.domain.Product_;
import hr.tvz.diplomski.webshop.enumeration.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductSearchSortBuilder {

    private final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, Product_.CREATION_DATE);

    public Sort build(SortType sortType) {
        if (sortType == null) {
            return DEFAULT_SORT;
        }
        switch (sortType) {
            case PRICE_ASC:
                return Sort.by(Sort.Direction.ASC, Product_.REGULAR_PRICE);
            case PRICE_DESC:
                return Sort.by(Sort.Direction.DESC, Product_.REGULAR_PRICE);
            case DATE_ADDED_ASC:
                return Sort.by(Sort.Direction.ASC, Product_.CREATION_DATE);
            case DATE_ADDED_DESC:
                return Sort.by(Sort.Direction.DESC, Product_.CREATION_DATE);
            default:
                return DEFAULT_SORT;
        }
    }
}
