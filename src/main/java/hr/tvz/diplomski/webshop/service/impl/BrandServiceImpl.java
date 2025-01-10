package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Brand;
import hr.tvz.diplomski.webshop.repository.BrandRepository;
import hr.tvz.diplomski.webshop.service.BrandService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandRepository brandRepository;

    @Override
    public List<Brand> getBrandsForNames(List<String> brandNames) {
        return brandRepository.findAllByNameIn(brandNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.toUpperCase())
                .collect(Collectors.toList()));
    }
}
