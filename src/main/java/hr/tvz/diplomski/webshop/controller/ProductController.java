package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.dto.ProductDto;
import hr.tvz.diplomski.webshop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public ProductDto getProduct(@PathVariable("productId") Long productId) {
        return productService.getForId(productId);
    }

    @RequestMapping(value = "/newest", method = RequestMethod.GET)
    public List<ProductDto> getNewestProducts() {
        return productService.findNewestProducts();
    }
}
