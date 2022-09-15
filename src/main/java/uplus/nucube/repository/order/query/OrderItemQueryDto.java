package uplus.nucube.repository.order.query;

import lombok.Data;

@Data
public class OrderItemQueryDto {

    private Long orderItemId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderItemId,String itemName, int orderPrice, int count) {
        this.orderItemId = orderItemId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
