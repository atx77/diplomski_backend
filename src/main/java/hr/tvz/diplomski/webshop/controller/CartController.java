package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.dto.CartDto;
import hr.tvz.diplomski.webshop.dto.request.AddProductToCartRequest;
import hr.tvz.diplomski.webshop.dto.request.ChangeProductQuantityInCartRequest;
import hr.tvz.diplomski.webshop.dto.request.RemoveProductFromCartRequest;
import hr.tvz.diplomski.webshop.service.CartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public CartDto getCartForLoggedUser() {
        return cartService.getCartForLoggedUser();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void addProductToCart(@RequestBody AddProductToCartRequest addProductToCartRequest) {
        cartService.addProductToCart(addProductToCartRequest.getProductCode(), addProductToCartRequest.getQuantity());
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public CartDto removeProductFromCart(@RequestBody RemoveProductFromCartRequest removeProductFromCartRequest) {
       cartService.removeProductFromCart(removeProductFromCartRequest.getProductCode());
       return cartService.getCartForLoggedUser();
    }

    @RequestMapping(value = "/change-quantity", method = RequestMethod.POST)
    public CartDto changeProductQuantityInCart(@RequestBody ChangeProductQuantityInCartRequest changeProductQuantityInCartRequest) {
        return cartService.changeProductQuantityInCart(changeProductQuantityInCartRequest.getProductCode(),
                changeProductQuantityInCartRequest.getQuantity());
    }
}
