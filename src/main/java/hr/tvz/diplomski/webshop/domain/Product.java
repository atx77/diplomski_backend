package hr.tvz.diplomski.webshop.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"category", "brand"})
@EqualsAndHashCode(exclude = {"category", "brand"})
@Entity(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String summary;
    private BigDecimal regularPrice;
    private BigDecimal actionPrice;
    private BigDecimal discountPercentage;
    private Integer availableQuantity;
    private String imageUrl;
    private Date creationDate;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
