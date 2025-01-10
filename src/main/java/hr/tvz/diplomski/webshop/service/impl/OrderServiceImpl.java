package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.*;
import hr.tvz.diplomski.webshop.dto.OrderDto;
import hr.tvz.diplomski.webshop.enumeration.CountryEnum;
import hr.tvz.diplomski.webshop.enumeration.DeliveryMode;
import hr.tvz.diplomski.webshop.enumeration.PaymentMethod;
import hr.tvz.diplomski.webshop.repository.OrderItemRepository;
import hr.tvz.diplomski.webshop.repository.OrderRepository;
import hr.tvz.diplomski.webshop.service.AddressService;
import hr.tvz.diplomski.webshop.service.CartService;
import hr.tvz.diplomski.webshop.service.OrderService;
import hr.tvz.diplomski.webshop.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private OrderItemRepository orderItemRepository;

    @Resource
    private UserService userService;

    @Resource
    private AddressService addressService;

    @Resource
    private CartService cartService;

    @Resource
    private ConversionService conversionService;

    @Transactional
    @Override
    public OrderDto createOrder(String firstName, String lastName, String street, String city, String postcode,
                                DeliveryMode deliveryMode, PaymentMethod paymentMethod) {
        User user = userService.getLoggedUserModel();
        Cart cart = user.getCart();
        Assert.notNull(user);
        Assert.notNull(cart);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(addressService.createNewAddressForUser(firstName, lastName, street, city, postcode, CountryEnum.CROATIA, user));
        order.setCreationDate(new Date());
        order.setDeliveryMode(deliveryMode);
        order.setPaymentMethod(paymentMethod);

        order.setItems(new ArrayList<>());
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSellingPrice(cartItem.getProduct().getActionPrice() != null ? cartItem.getProduct().getActionPrice() : cartItem.getProduct().getRegularPrice());
            orderItem.setRegularPrice(cartItem.getProduct().getRegularPrice());
            orderItem.setTotalPrice(calculateTotalPriceForProduct(cartItem.getProduct(), cartItem.getQuantity()));
            orderItem.setDiscountPercentage(cartItem.getProduct().getDiscountPercentage());
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        }
        order.setTotalPrice(order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderRepository.save(order);

        order.setCode(StringUtils.leftPad(order.getId().toString(), 6, "0"));
        orderRepository.save(order);

        cartService.clearCart(cart);
        return conversionService.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto getByCode(String orderCode) {
        Order order = orderRepository.findByCodeEquals(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("No order with code " + orderCode));
        User user = userService.getLoggedUserModel();
        if (!user.equals(order.getUser())) {
            throw new IllegalArgumentException("No order with code " + orderCode);
        }
        return conversionService.convert(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersForLoggedCustomer() {
        User user = userService.getLoggedUserModel();
        return (List<OrderDto>) conversionService.convert(orderRepository.findAllByUserEqualsOrderByCreationDateDesc(user),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Order.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(OrderDto.class)));
    }

    private BigDecimal calculateTotalPriceForProduct(Product product, Integer quantity) {
        BigDecimal productPrice = product.getActionPrice() != null ? product.getActionPrice() : product.getRegularPrice();
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

}
