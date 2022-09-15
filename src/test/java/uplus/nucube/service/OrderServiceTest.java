package uplus.nucube.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Member;
import uplus.nucube.domain.Order;
import uplus.nucube.domain.OrderStatus;
import uplus.nucube.exception.NotEnoughStockException;
import uplus.nucube.domain.item.Book;
import uplus.nucube.repository.OrderRepository;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @Commit
    public void 상품주문() throws Exception{
        //given

        Member member = new Member( "회원1", new Address( "서울", "강가", "123-123" ) );
        em.persist( member );

        Book book = new Book("시골 JPA",10000,10,"kim","isbn");
        em.persist( book );
        int orderCount=2;
        //when
        Long orderId = orderService.order( member.getId(), book.getId(), orderCount );

        //then
        Order order = orderRepository.findOne( orderId );


        assertThat( OrderStatus.ORDER ).isEqualTo( order.getStatus() );
        assertThat( order.getOrderItems().size() ).isEqualTo( 1 );
        assertThat(order.getTotalPrice()).isEqualTo( 10000*orderCount);
        assertThat( book.getStockQuantity() ).isEqualTo( 8 );

    }

    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = new Member( "회원1", new Address( "서울", "강가", "123-123" ) );
        em.persist( member );

        Book book = new Book("시골 JPA",10000,10,"kim","isbn");
        em.persist( book );

        int orderCount=2;
        Long orderId = orderService.order( member.getId(), book.getId(), orderCount );

        //when
        orderService.cancelOrder( orderId );

        //then
        Order getOrder = orderRepository.findOne( orderId );

        assertThat( getOrder.getStatus() ).isEqualTo( OrderStatus.CANCEL );
        assertThat( book.getStockQuantity() ).isEqualTo( 10 );

    }


    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = new Member( "회원1", new Address( "서울", "강가", "123-123" ) );
        em.persist( member );
        Book book = new Book("시골 JPA",10000,10,"kim","isbn");
        em.persist( book );
        int orderCount=11;
        //when
        //Long orderId = orderService.order( member.getId(), book.getId(), orderCount );

        Assertions.assertThatThrownBy( () -> orderService.order( member.getId(), book.getId(), orderCount ) )
                .isInstanceOf( NotEnoughStockException.class );

        //then
    }
}