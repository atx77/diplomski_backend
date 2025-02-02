package hr.tvz.diplomski.webshop.enumeration;

public enum DeliveryMode {
    COURIER("Address delivery"),
    EXPRESS("Express delivery"),
    PERSONAL_PICKUP("Personal pickup");

    private String description;

    DeliveryMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
