package hr.tvz.diplomski.webshop.enumeration;

public enum CountryEnum {
    CROATIA("Hrvatska");

    private String description;

    CountryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
