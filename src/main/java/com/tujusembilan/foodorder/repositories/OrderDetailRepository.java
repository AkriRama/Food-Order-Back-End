package com.tujusembilan.foodorder.repositories;

import com.tujusembilan.foodorder.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
//    List<OrderDetail> findAll();
    @Query("SELECT o FROM OrderDetail o WHERE o.order.orderId = :orderId")
    List<OrderDetail> findOrderDetailByOrder(int orderId);
}
