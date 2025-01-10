package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Category;
import hr.tvz.diplomski.webshop.dto.CategoryDto;
import hr.tvz.diplomski.webshop.repository.CategoryRepository;
import hr.tvz.diplomski.webshop.service.CategoryService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private ConversionService conversionService;

    @Override
    public List<CategoryDto> getAllParentCategories() {
        return (List<CategoryDto>) conversionService.convert(categoryRepository.findAllByParentCategoryIsNullAndActiveIsTrue(),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Category.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(CategoryDto.class)));
    }

    @Override
    public Optional<Category> getCategoryForId(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> getCategoryForCode(String code) {
        return categoryRepository.findByCode(code);
    }
}
