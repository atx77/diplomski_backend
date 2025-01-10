package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private ProductDto product;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal regularPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountPercentage;
}
