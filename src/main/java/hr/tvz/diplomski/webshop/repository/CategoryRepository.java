package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByParentCategoryIsNullAndActiveIsTrue();
    Optional<Category> findByCode(String code);
}
