package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
