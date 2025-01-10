package hr.tvz.diplomski.webshop.enumeration;

public enum SortType {
    PRICE_ASC("Od najjeftinijeg"),
    PRICE_DESC("Od najskupljeg"),
    DATE_ADDED_ASC("Od najstarijeg"),
    DATE_ADDED_DESC("Od najnovijeg"),
    RELEVANCE("Po relevantnosti");

    private String description;

    SortType(String descritpion) {
        this.description = descritpion;
    }

    public String getDescription() {
        return description;
    }
}
