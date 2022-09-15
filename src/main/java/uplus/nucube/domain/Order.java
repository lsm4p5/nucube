package uplus.nucube.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ ORDER, CANCEL ]

    //== 연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add( this );
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add( orderItem );
        orderItem.setOrder( this );
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder( this );
    }

    public void setOrderDate(LocalDateTime localDateTime) {
        this.orderDate = localDateTime;
    }

    public void setOrderStatus(OrderStatus status) {
        this.status = status;
    }

//    public void change_member(Member member) {
//        if(this.member != null)
//        {
//            this.member.getOrders().remove( this );
//        }
//        this.member = member;
//        member.getOrders().add( this );
//    }
//
//    public void change_delivery(Delivery delivery) {
//        if(this.delivery !=null){
//            this.delivery.setOrder( null );;
//        }
//        this.delivery = delivery;
//        delivery.setOrder( this );
//    }

    //== 생성 메서드 ==//



    /**
     *   order 생성 순서
     *   1. member, delivery 정보를 사용
     *   2. orderItem 정보를 사용 -> orderItem은 item.removeStock()을 사용하여
     *                             Item의 StockQuantiry를 -시키고 만듬. 반영하여 생성됨
     *                          -> Item의 재고 반영됨,
     *   --> order를 persist를 하면, delivery, orderIte은 CaseCade에 의해 DB에 같이 저장됨.(member는 아님)
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {

        Order order = new Order();
        order.setMember(member);   //연관관계 메서드
        order.setDelivery( delivery ); //연관관계 메서드
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem( orderItem ); //연관관계메서드
        }
        order.setOrderStatus( OrderStatus.ORDER );
        order.setOrderDate( LocalDateTime.now());

        return order;
    }

    //비지니스 로직
    /**
     * 주문 취소
     *     1. 배송이 완료된 상태이면, 취소할수 없음 -> exception throw
     *     2. order의 배송 상태를 주문상태로 변경
     *     3. order가 가지고 있는 각각의 orderItem의 cancel메소드를 통해
     *        item의 재고 수량을 늘려주어야 함.
     *
     */

    public void cancel() {
        if (delivery.getStatus() == DelivertStatus.COMP) {
            throw new IllegalStateException( "이미 배송완료된 상품은 취소가 불가능합니다" );
        }
        this.setOrderStatus( OrderStatus.CANCEL );
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //== 조회 로직 ==//

    /**
     * 전체 주문 가격 조회
     * OrderItem의 전체 주문 가격 조회
     */

    public int getTotalPrice() {

        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
         }
         return totalPrice;

//        return orderItems.stream()
//                .mapToInt( OrderItem::getTotalPrice )
//                .sum();

        /**
         * int totalPrice = 0
         * for (OrderItem orderItem : orderItems) {
         *             totalPrice += orderItem.getTotalPrice();
         *}
         * retrun totalPrice
         * ==> return orderItems.stream().
         *                mapToInt( OrderItem::getTotalPrice )
         *                .sum();
         **/

    }

}
