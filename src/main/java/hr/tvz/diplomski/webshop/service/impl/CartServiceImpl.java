package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Cart;
import hr.tvz.diplomski.webshop.domain.CartItem;
import hr.tvz.diplomski.webshop.domain.Product;
import hr.tvz.diplomski.webshop.domain.User;
import hr.tvz.diplomski.webshop.dto.CartDto;
import hr.tvz.diplomski.webshop.repository.CartItemRepository;
import hr.tvz.diplomski.webshop.repository.CartRepository;
import hr.tvz.diplomski.webshop.service.CartService;
import hr.tvz.diplomski.webshop.service.ProductService;
import hr.tvz.diplomski.webshop.service.UserService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @Resource
    private CartRepository cartRepository;

    @Resource
    private CartItemRepository cartItemRepository;

    @Resource
    private ConversionService conversionService;

    @Override
    public CartDto getCartForLoggedUser() {
        User user = userService.getLoggedUserModel();
        return conversionService.convert(user.getCart(), CartDto.class);
    }

    @Transactional
    @Override
    public void addProductToCart(Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than 1!");
        }
        Product product = productService.getModelForId(productId)
                .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Product with id {0} does not exist!", productId)));
        User user = userService.getLoggedUserModel();
        Cart cart = user.getCart();
        removeInactiveProductsFromCart(cart);

        CartItem cartItem = null;
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null && productId.equals(ci.getProduct().getId()))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        cartItemRepository.save(cartItem);

        cart.getItems().add(cartItem);
        cart.setTotalPrice(calculateCartTotalPrice(cart));
        cartRepository.save(cart);
    }

    @Override
    public void recalculateCartTotalPrice() {
        Cart cart = userService.getLoggedUserModel().getCart();
        removeInactiveProductsFromCart(cart);
        cart.setTotalPrice(calculateCartTotalPrice(cart));
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long productId) {
        Product product = productService.getModelForId(productId)
                .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Product with id {0} does not exist!", productId)));

        User user = userService.getLoggedUserModel();
        for (CartItem cartItem : user.getCart().getItems()) {
            if (product.equals(cartItem.getProduct())) {
                cartItem.setCart(null);
                cartItemRepository.save(cartItem);
            }
        }
        recalculateCartTotalPrice();
    }

    @Override
    public CartDto changeProductQuantityInCart(Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            removeProductFromCart(productId);
        }

        User user = userService.getLoggedUserModel();
        Product product = productService.getModelForId(productId)
                .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Product with id {0} does not exist!", productId)));
        for (CartItem cartItem : user.getCart().getItems()) {
            if (product.equals(cartItem.getProduct())) {
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            }
        }
        recalculateCartTotalPrice();
        return conversionService.convert(user.getCart(), CartDto.class);
    }

    @Transactional
    @Override
    public void clearCart(Cart cart) {
        for (CartItem cartItem : cart.getItems()) {
            cartItem.setCart(null);
            cartItemRepository.save(cartItem);
        }
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private BigDecimal calculateCartTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getCart() != null)
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    BigDecimal productPrice = product.getActionPrice() != null ? product.getActionPrice() : product.getRegularPrice();
                    return productPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void removeInactiveProductsFromCart(Cart cart) {
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct() != null && !cartItem.getProduct().isActive()) {
                cartItem.setCart(null);
                cartItemRepository.save(cartItem);
            }
        }
    }
}
