package uplus.nucube.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Order;
import uplus.nucube.domain.OrderStatus;
import uplus.nucube.repository.OrderRepository;
import uplus.nucube.repository.OrderSearch;
import uplus.nucube.repository.order.simplequery.OrderSimpleQueryDto;
import uplus.nucube.repository.order.simplequery.OrderSimpleQueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString( new OrderSearch() );
        for (Order order : all) {
            order.getMember().getName();    //Lazy 강제 초기화
            order.getDelivery().getStatus(); // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2(){

        List<Order> orders = orderRepository.findAllByString( new OrderSearch() );

        List<SimpleOrderDto> collect = orders.stream().map( o -> new SimpleOrderDto( o ) )
                .collect( Collectors.toList() );

        return new Result( collect.size(), collect );

    }

    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3(){

        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream().map( o -> new SimpleOrderDto( o ) )
                .collect( Collectors.toList() );

        return new Result( collect.size(), collect );
    }

    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4(){
        List<OrderSimpleQueryDto> orderDtos = orderSimpleQueryRepository.findOrderDtos();
        return new Result(orderDtos.size(),orderDtos);
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //Lazy 초기화
            orderDate = order.getOrderDate();
            status = order.getStatus();
            address = order.getDelivery().getAddress();//Lazy 초기화
        }
    }
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;

    }
}
