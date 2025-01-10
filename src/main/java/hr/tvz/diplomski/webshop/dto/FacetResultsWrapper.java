package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class FacetResultsWrapper {
    private List<FacetDto> brands;
    private List<FacetDto> categories;
}
