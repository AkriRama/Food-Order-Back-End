package com.tujusembilan.foodorder.repositories;

import com.tujusembilan.foodorder.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findOrderById(int orderI);

    @Query("SELECT o FROM Order o  WHERE o.user.userId = :userId")
    List<Order> findOrderByUser(int userId);

    List<Order> findAll();
}
