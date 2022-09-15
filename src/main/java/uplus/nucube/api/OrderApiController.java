package uplus.nucube.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Order;
import uplus.nucube.domain.OrderItem;
import uplus.nucube.domain.OrderStatus;
import uplus.nucube.repository.OrderRepository;
import uplus.nucube.repository.OrderSearch;
import uplus.nucube.repository.order.query.OrderFlatDto;
import uplus.nucube.repository.order.query.OrderItemQueryDto;
import uplus.nucube.repository.order.query.OrderQueryDto;
import uplus.nucube.repository.order.query.OrderQueryRepository;


import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {


    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public Result ordersV1() {
        List<Order> orders = orderRepository.findAllByString( new OrderSearch() );
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getStatus();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach( o->o.getItem().getName() );
        }
        return new Result( orders.size(), orders );
    }
    @GetMapping("/api/v2/orders")
    public Result ordersV2(){
        List<Order> orders = orderRepository.findAllByString( new OrderSearch() );

        List<OrderDto> collect = orders.stream()
                .map( o -> new OrderDto( o ) )
                .collect( toList() );

        return new Result( collect.size(), collect );

    }
    @GetMapping("/api/v3/orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream().map( o -> new OrderDto( o ) )
                .collect( toList() );
        return new Result( collect.size(),collect );
    }

    @GetMapping("/api/v3.1/orders")
    public Result ordersV3_page(
            @RequestParam(value="offset",defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit) {

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> collect = orders.stream().map( o -> new OrderDto( o ) )
                .collect( toList() );
        return new Result( collect.size(),collect );
    }

    @GetMapping("/api/v4/orders")
    public Result ordersV4() {

        List<OrderQueryDto> orders = orderQueryRepository.findOrderQueryDto();

        return new Result( orders.size(),orders );
    }

    @GetMapping("/api/v5/orders")
    public Result ordersV5(){

        List<OrderQueryDto> orders = orderQueryRepository.findAllByDto_optimization();

        return new Result( orders.size(), orders );
    }

    @GetMapping("/api/v6/orders")
    public Result ordersV6(){

        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        List<OrderQueryDto> collect = flats.stream()
                .collect( groupingBy( o -> new OrderQueryDto( o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress() ),
                        mapping( o -> new OrderItemQueryDto( o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount() ), toList() )
                ) ).entrySet().stream()
                .map( e -> new OrderQueryDto( e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue() ) )
                .collect( toList() );

        return new Result( collect.size(), collect );
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            status = order.getStatus();
            address = order.getDelivery().getAddress();
            ;
            orderItems = order.getOrderItems().stream()
                    .map( oi -> new OrderItemDto( oi ) )
                    .collect( toList() );
        }
    }

    @Data
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;

    }
}
