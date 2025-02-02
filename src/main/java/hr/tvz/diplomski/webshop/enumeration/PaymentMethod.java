package hr.tvz.diplomski.webshop.enumeration;

public enum PaymentMethod {
    CREDIT_CARD("Credit card"),
    PAYPAL("PayPal"),
    CASH_ON_DELIVERY("Cash on delivery");

    private String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
