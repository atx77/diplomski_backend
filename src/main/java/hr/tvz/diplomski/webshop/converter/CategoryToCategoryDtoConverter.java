package hr.tvz.diplomski.webshop.converter;

import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.dto.CategoryDto;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryToCategoryDtoConverter implements Converter<Category, CategoryDto> {

    @Override
    public CategoryDto convert(Category source) {
        CategoryDto dto = new CategoryDto();
        dto.setCode(source.getCode());
        dto.setActive(source.isActive());
        dto.setName(source.getName());
        dto.setSubCategories(
                source.getSubCategories()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(this::convert)
                        .collect(Collectors.toList()));
        return dto;
    }
}
