package uplus.nucube.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uplus.nucube.domain.item.Item;

import javax.persistence.*;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public void setItem(Item item) {
        this.item = item;   //연관관계 메서드는 아님.
    }

    public void setOrder(Order order) {
        this.order = order; //연관관계 메서드 아님.
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setCount(int count) {
        this.count = count;
    }

    //== 비지니스 로직 ==/
    public void cancel() {
        getItem().addStock(count);
    }

    //= 조회 로직 : 주문상품 전체 가격 조회 ==/
    public int getTotalPrice() {

        return getOrderPrice() * getCount();
    }


    protected OrderItem() {
    }

    /**
     * OrderItem 생성시
     * 1. item을 받아서 item의 재고를 줄여주고,
     *    orderItem을 생성한다.
     */

    //== 생성 메스드 ==//


    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem( item );
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount( count );
        item.removeStock( count );

        return orderItem;

    }
}
