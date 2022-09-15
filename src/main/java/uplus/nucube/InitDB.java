package uplus.nucube;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import uplus.nucube.domain.*;
import uplus.nucube.domain.item.Book;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 주문 2개
 *  userA
 *    JPA1 BOOK
 *    JPA2 BOOK
 *  userB
 *    SPRING1 BOOK
 *    SPRING2 BOOK
 */

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        initService.dnInit1();
        initService.dnInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dnInit1() {
            Member member = createMember("userA","서울","1","1111");
            em.persist( member );

            Book book1 = new Book( "JPA1 BOOK", 10000, 100, "author1", "isbn1" );
            em.persist( book1 );
            Book book2= new Book( "JPA2 BOOK", 20000, 100, "author1", "isbn1" );
            em.persist( book2 );

            OrderItem orderItem1 = OrderItem.createOrderItem( book1, 10000, 1 );
            OrderItem orderItem2 = OrderItem.createOrderItem( book2, 20000, 2 );

            Delivery delivery = new Delivery(new Address( member.getAddress().getCity(),
                    member.getAddress().getStreet(),member.getAddress().getZipcode()));
            Order order1 = Order.createOrder( member, delivery, orderItem1, orderItem2 );

            em.persist( order1 );

        }
        public void dnInit2() {
            Member member = createMember("userB","진주","2","2222");
            em.persist( member );

            Book book1 = new Book( "SPRING1 BOOK", 20000, 200, "author1", "isbn1" );
            em.persist( book1 );
            Book book2= new Book( "SPRING2 BOOK", 40000, 300, "author1", "isbn1" );
            em.persist( book2 );

            OrderItem orderItem1 = OrderItem.createOrderItem( book1, 20000, 3 );
            OrderItem orderItem2 = OrderItem.createOrderItem( book2, 40000, 4 );

            Delivery delivery = new Delivery(new Address( member.getAddress().getCity(),
                    member.getAddress().getStreet(),member.getAddress().getZipcode()));
            Order order1 = Order.createOrder( member, delivery, orderItem1, orderItem2 );

            em.persist( order1 );

        }

        private Member createMember(String name,String city,String street,String zipcode) {
            Member member = new Member(name,
                                new Address( city,street,zipcode ) );
            return member;
        }
    }
}


