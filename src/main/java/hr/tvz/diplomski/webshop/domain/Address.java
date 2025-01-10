package hr.tvz.diplomski.webshop.domain;

import hr.tvz.diplomski.webshop.enumeration.CountryEnum;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Entity(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postcode;

    @Enumerated(EnumType.STRING)
    private CountryEnum country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
