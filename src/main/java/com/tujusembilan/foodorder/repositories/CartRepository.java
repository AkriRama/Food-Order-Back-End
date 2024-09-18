package com.tujusembilan.foodorder.repositories;

import com.tujusembilan.foodorder.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.food.foodId = :foodId")
    Optional<Cart> findCartByFoodAndUser(int foodId, int userId);
    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.isDeleted = false")
    List<Cart> findCartByUserIdANDIsDeletedFalse(int userId);

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.cartId = :cartId AND c.isDeleted = false")
    Optional<Cart> findCartByUserAndCart(int userId, int cartId);

//    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.cartId = :cartId")
//    Optional<Cart> findCartByUserAndCart(int userId, int cartId);

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    List<Cart> findCartByUserId(int userId);

    List<Cart> findAll();
}
