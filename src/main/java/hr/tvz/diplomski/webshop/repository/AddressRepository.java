package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
