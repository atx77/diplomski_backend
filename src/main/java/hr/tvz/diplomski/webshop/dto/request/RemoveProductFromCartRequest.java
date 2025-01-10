package hr.tvz.diplomski.webshop.dto.request;

import lombok.Data;

@Data
public class RemoveProductFromCartRequest {
    private Long productCode;
}
