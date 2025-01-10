package hr.tvz.diplomski.webshop.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parentCategory", "subCategories", "products"})
@EqualsAndHashCode(exclude = {"parentCategory", "subCategories", "products"})
@Entity(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private String name;
    private Date creationDate;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    public Category parentCategory;

    @OneToMany(mappedBy="parentCategory", cascade = CascadeType.ALL)
    public List<Category> subCategories;


    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
