package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}
