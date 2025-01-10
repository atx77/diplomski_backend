package hr.tvz.diplomski.webshop.converter;

import hr.tvz.diplomski.webshop.domain.Cart;
import hr.tvz.diplomski.webshop.dto.CartDto;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class CartToCartDtoConverter implements Converter<Cart, CartDto> {

    @Override
    public CartDto convert(Cart source) {
        CartDto cartDto = new CartDto();
        cartDto.setTotalPrice(source.getTotalPrice());

        CartItemToCartItemDtoConverter cartItemToCartItemDtoConverter = new CartItemToCartItemDtoConverter();
        cartDto.setCartItems(source.getItems()
                .stream()
                .filter(cartItem -> cartItem.getCart() != null)
                .map(cartItem -> cartItemToCartItemDtoConverter.convert(cartItem))
                .collect(Collectors.toList())
        );
        return cartDto;
    }
}
