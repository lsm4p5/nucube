package uplus.nucube.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDto(){
        List<OrderQueryDto> result = findOrders();
        result.forEach( o->{
            List<OrderItemQueryDto> orderItems= findOrderItems( o.getOrderId() );
            o.setOrderItems( orderItems );
        } );
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = toOrderIds( result );

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap( orderIds );

        result.forEach( o-> o.setOrderItems(orderItemMap.get(o.getOrderId())) );

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        return em.createQuery( "select" +
                        " new uplus.nucube.repository.order.query.OrderItemQueryDto(oi.id,i.name,oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class )
                .setParameter( "orderIds", orderIds )
                .getResultList().stream()
                .collect( Collectors.groupingBy( orderItemQueryDto -> orderItemQueryDto.getOrderItemId() ) );
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map( o -> o.getOrderId() )
                .collect( Collectors.toList() );
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select" +
         " new uplus.nucube.repository.order.query.OrderItemQueryDto(oi.id,i.name,oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter( "orderId", orderId )
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
       return em.createQuery(
                "select new uplus.nucube.repository.order.query.OrderQueryDto" +
                        "(o.id,m.name,o.orderDate,o.status,o.delivery.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d",
                OrderQueryDto.class).getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new " +
                        " uplus.nucube.repository.order.query.OrderFlatDto" +
                        "(o.id,m.name,o.orderDate,o.status,o.delivery.address,oi.id,i.name,oi.orderPrice, oi.count)" +
                        " from Order o " +
                        " join o.member m " +
                        " join o.delivery d " +
                        " join o.orderItems oi " +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
