package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long code;
    private String name;
    private String description;
    private String summary;
    private BigDecimal regularPrice;
    private BigDecimal actionPrice;
    private BigDecimal discountPercentage;
    private Integer availableQuantity;
    private String imageUrl;
    private String brand;
}
