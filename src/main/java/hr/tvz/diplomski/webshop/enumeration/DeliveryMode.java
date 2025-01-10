package hr.tvz.diplomski.webshop.enumeration;

public enum DeliveryMode {
    COURIER("Kurirska dostava"),
    EXPRESS("Ekspresna dostava"),
    PERSONAL_PICKUP("Osobno preuzimanje");

    private String description;

    DeliveryMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
