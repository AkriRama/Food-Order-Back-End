package com.tujusembilan.foodorder.repositories;

import com.tujusembilan.foodorder.models.FavoriteFood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, Integer>, JpaSpecificationExecutor<FavoriteFood> {
    @Query("SELECT f FROM FavoriteFood f WHERE f.user.userId = :userId AND f.food.foodId = :foodId")
    Optional<FavoriteFood> findFavoriteByFoodAndUser(int foodId, int userId);

    @Query("SELECT f FROM FavoriteFood f WHERE f.user.userId = :userId AND f.isFavorite = true")
    List<FavoriteFood> findFavoriteByUserAndIsFavorite(int userId);
    @Query("SELECT f FROM FavoriteFood f WHERE f.user.userId = :userId AND f.isFavorite = true")
    Page<FavoriteFood> findFavoriteByUser(int userId, Pageable page);
}
