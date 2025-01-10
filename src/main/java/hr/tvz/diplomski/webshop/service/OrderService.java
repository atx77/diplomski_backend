package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.dto.OrderDto;
import hr.tvz.diplomski.webshop.enumeration.DeliveryMode;
import hr.tvz.diplomski.webshop.enumeration.PaymentMethod;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(String firstName, String lastName, String street, String city, String postcode,
                         DeliveryMode deliveryMode, PaymentMethod paymentMethod);

    OrderDto getByCode(String orderCode);

    List<OrderDto> getOrdersForLoggedCustomer();
}
