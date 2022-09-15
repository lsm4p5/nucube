package uplus.nucube.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uplus.nucube.domain.Order;
import uplus.nucube.domain.OrderStatus;
import uplus.nucube.domain.QMember;
import uplus.nucube.domain.QOrder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist( order );
    }

    public Order findOne(Long id) {
        return em.find( Order.class, id );
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        JPAQueryFactory query = new JPAQueryFactory( em );
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        return query
                .select( order )
                .from( order )
                .join( order.member, member )
                .where(statusEq(orderSearch.getOrderStatus()),nameLike(orderSearch.getMemberName()))
                .limit( 1000 )
                .fetch();

    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq( statusCond );
    }
    private BooleanExpression nameLike(String memberName) {
        if (!StringUtils.hasText(memberName)) {
            return null;
        }
        return QMember.member.name.like(memberName);
    }
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery( "select o from Order o" +
                        " join fetch o.member m " +
                        " join fetch o.delivery d", Order.class )
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        // Order - OrderItem 일대다 관계이므로 페이징 처리가 불가능하다.
        // 페이징 처리를 하면 장애가 발생할수 있다.
        return em.createQuery( "select distinct o from Order o " +
                " join fetch o.member m " +
                " join fetch o.delivery d " +
                " join fetch o.orderItems oi " +
                " join fetch oi.item i ", Order.class)
                .getResultList();

    }


    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        //Order - Member  ManyToOne
        //Ordeer - Delivery  OneToOne 이므로 페이징 처리 가능하다.
        //Order - OrderItem OneToMany이므로 fetch join에 포함하지 않고,
        //지연로딩으로 가져오도록 한다.  이런방식으로 페이징 처리를 해야 한다.
        return em.createQuery( "select o from Order o" +
                        " join fetch o.member m " +
                        " join fetch o.delivery d", Order.class )
                .setFirstResult( offset )
                .setMaxResults( limit )
                .getResultList();
    }
}
