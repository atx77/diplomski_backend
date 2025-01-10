package hr.tvz.diplomski.webshop.converter;

import hr.tvz.diplomski.webshop.domain.Order;
import hr.tvz.diplomski.webshop.dto.OrderDto;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class OrderToOrderDtoConverter implements Converter<Order, OrderDto> {

    @Override
    public OrderDto convert(Order source) {
        OrderDto orderDto = new OrderDto();

        OrderItemToOrderItemDtoConverter orderItemToOrderItemDtoConverter = new OrderItemToOrderItemDtoConverter();
        orderDto.setOrderItems(source.getItems()
                .stream()
                .filter(orderItem -> orderItem.getOrder() != null)
                .map(orderItem -> orderItemToOrderItemDtoConverter.convert(orderItem))
                .collect(Collectors.toList())
        );
        orderDto.setCode(source.getCode());

        AddressToAddressDtoConverter addressToAddressDtoConverter = new AddressToAddressDtoConverter();
        orderDto.setAddress(addressToAddressDtoConverter.convert(source.getShippingAddress()));
        orderDto.setCreationDate(source.getCreationDate());
        orderDto.setTotalPrice(source.getTotalPrice());
        orderDto.setDeliveryMode(source.getDeliveryMode() != null ? source.getDeliveryMode().getDescription() : "");
        orderDto.setPaymentMethod(source.getPaymentMethod() != null ? source.getPaymentMethod().getDescription() : "");

        return orderDto;
    }
}
