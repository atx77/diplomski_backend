package hr.tvz.diplomski.webshop.dto.request;

import lombok.Data;

@Data
public class AddProductToCartRequest {
    Long productCode;
    Integer quantity;
}
