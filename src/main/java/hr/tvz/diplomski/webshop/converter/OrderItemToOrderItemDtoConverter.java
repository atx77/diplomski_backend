package hr.tvz.diplomski.webshop.converter;

import hr.tvz.diplomski.webshop.domain.OrderItem;
import hr.tvz.diplomski.webshop.dto.OrderItemDto;
import org.springframework.core.convert.converter.Converter;

public class OrderItemToOrderItemDtoConverter implements Converter<OrderItem, OrderItemDto> {

    @Override
    public OrderItemDto convert(OrderItem source) {
        OrderItemDto orderItemDto = new OrderItemDto();
        ProductToProductDtoConverter productToProductDtoConverter = new ProductToProductDtoConverter();
        orderItemDto.setProduct(productToProductDtoConverter.convert(source.getProduct()));
        orderItemDto.setQuantity(source.getQuantity());
        orderItemDto.setSellingPrice(source.getSellingPrice());
        orderItemDto.setRegularPrice(source.getRegularPrice());
        orderItemDto.setTotalPrice(source.getTotalPrice());
        orderItemDto.setDiscountPercentage(source.getDiscountPercentage());
        return orderItemDto;
    }

}
