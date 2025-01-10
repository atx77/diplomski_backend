package hr.tvz.diplomski.webshop.dto.request;

import hr.tvz.diplomski.webshop.enumeration.DeliveryMode;
import hr.tvz.diplomski.webshop.enumeration.PaymentMethod;
import lombok.Data;

@Data
public class CheckoutFormRequest {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postcode;
    private DeliveryMode deliveryMode;
    private PaymentMethod paymentMethod;
}
