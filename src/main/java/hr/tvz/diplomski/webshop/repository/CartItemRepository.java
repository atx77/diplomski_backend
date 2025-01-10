package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
