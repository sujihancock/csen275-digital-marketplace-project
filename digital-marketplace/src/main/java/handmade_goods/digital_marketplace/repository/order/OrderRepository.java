package handmade_goods.digital_marketplace.repository.order;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(Buyer buyer);

    @Query("SELECT DISTINCT o " +
            "FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.product p " +
            "WHERE p.seller = :seller")
    List<Order> findOrdersWithItemsBySeller(Seller seller);
}
