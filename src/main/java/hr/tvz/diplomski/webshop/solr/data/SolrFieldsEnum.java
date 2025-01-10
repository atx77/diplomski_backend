package hr.tvz.diplomski.webshop.solr.data;

public enum SolrFieldsEnum {
    ID("id"),
    NAME("name"),
    BRAND("brand"),
    STOCK("stock"),
    REGULAR_PRICE("regular_price"),
    ACTION_PRICE("action_price"),
    CATEGORY("category"),
    IMAGE_URL("image_url"),
    CREATION_DATE("creation_date"),
    SCORE("score"),
    BRAND_FACET("brand_facet"),
    CATEGORY_FACET("category_facet"),
    CATEGORY_CODE("category_code");

    private String name;

    SolrFieldsEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
