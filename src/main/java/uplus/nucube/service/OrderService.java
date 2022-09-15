package uplus.nucube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.Delivery;
import uplus.nucube.domain.Member;
import uplus.nucube.domain.Order;
import uplus.nucube.domain.OrderItem;
import uplus.nucube.domain.item.Item;
import uplus.nucube.repository.ItemRepository;
import uplus.nucube.repository.MemberJpaRepository;
import uplus.nucube.repository.OrderRepository;
import uplus.nucube.repository.OrderSearch;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberJpaRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔터티 조회
        Member member = memberRepository.findOne( memberId );
        Item item = itemRepository.findOne( itemId );

        //배송 정보 생성
        Delivery delivery = new Delivery(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem( item, item.getPrice(), count );

        //주문 생성
        Order order = Order.createOrder( member, delivery, orderItem );

        //주문 저장
        orderRepository.save(order);

        return order.getId();

    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔터티 조회
        Order order = orderRepository.findOne( orderId );
        //주문 취소
        order.cancel();
    }


    //검색
    public List<Order> findOrders(OrderSearch orderSearch){

        return orderRepository.findAll( orderSearch );
    }
}
