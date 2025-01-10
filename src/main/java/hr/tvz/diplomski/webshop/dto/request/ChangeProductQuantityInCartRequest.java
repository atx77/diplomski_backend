package hr.tvz.diplomski.webshop.dto.request;

import lombok.Data;

@Data
public class ChangeProductQuantityInCartRequest {
    Long productCode;
    Integer quantity;
}
