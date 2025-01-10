package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.dto.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDto> getAllParentCategories();

    Optional<Category> getCategoryForId(Long id);

    Optional<Category> getCategoryForCode(String code);
}
