package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CategoryDto {
    private String code;
    private String name;
    private Date creationDate;
    private boolean active;
    public CategoryDto parentCategory;
    public List<CategoryDto> subCategories;
}
