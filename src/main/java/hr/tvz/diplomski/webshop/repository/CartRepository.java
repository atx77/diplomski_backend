package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
