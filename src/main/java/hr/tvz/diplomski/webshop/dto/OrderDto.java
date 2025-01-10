package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDto {
    private List<OrderItemDto> orderItems;
    private String code;
    private AddressDto address;
    private Date creationDate;
    private BigDecimal totalPrice;
    private String deliveryMode;
    private String paymentMethod;
}
