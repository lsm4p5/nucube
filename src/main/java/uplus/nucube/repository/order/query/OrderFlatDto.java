package uplus.nucube.repository.order.query;

import lombok.Data;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private Long orderItemId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long orderId, String name,
                        LocalDateTime orderDate, OrderStatus orderStatus,
                        Address address, Long orderItemId,
                        String itemName, int orderPrice, int count) {

        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderItemId = orderItemId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
