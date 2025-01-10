package hr.tvz.diplomski.webshop.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"items", "user"})
@EqualsAndHashCode(exclude = {"items", "user"})
@Entity(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> items;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date creationDate;

    private BigDecimal totalPrice;
}
