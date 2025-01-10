package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductFilterDto {
    private Set<String> brands;
    private List<String> sortCodes;
}
