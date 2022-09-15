package uplus.nucube.repository.order.simplequery;

import lombok.Data;
import uplus.nucube.domain.Address;

import uplus.nucube.domain.OrderStatus;

import java.time.LocalDateTime;
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus status, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.status = status;
        this.address = address;
    }
}
