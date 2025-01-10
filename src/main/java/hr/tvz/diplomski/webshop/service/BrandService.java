package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.domain.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getBrandsForNames(List<String> brandNames);
}
