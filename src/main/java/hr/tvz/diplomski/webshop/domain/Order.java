package hr.tvz.diplomski.webshop.domain;

import hr.tvz.diplomski.webshop.enumeration.DeliveryMode;
import hr.tvz.diplomski.webshop.enumeration.PaymentMethod;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"items", "shippingAddress", "user"})
@EqualsAndHashCode(exclude = {"items", "shippingAddress", "user"})
@Entity(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date creationDate;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private DeliveryMode deliveryMode;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
