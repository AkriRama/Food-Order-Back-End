package com.tujusembilan.foodorder.services.specifications;

import com.tujusembilan.foodorder.dto.request.FoodListRequestDTO;
import com.tujusembilan.foodorder.models.FavoriteFood;
import com.tujusembilan.foodorder.models.Food;
import com.tujusembilan.foodorder.utils.jwt.JwtUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFoodSpesification {
    public static Specification<FavoriteFood> favoriteFoodFilter(FoodListRequestDTO foodListRequestDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            int userId = JwtUtil.getCurrentUser().getUserId();

            predicates.add(criteriaBuilder.equal(root.get("id").get("user").get("userId"), userId));
            predicates.add(criteriaBuilder.equal(root.get("isFavorite"), true));
            Join<FavoriteFood, Food> foodJoin = root.join("food");


            if (foodListRequestDTO.getFoodName() != null) {

                String foodNameValue = "%" + foodListRequestDTO.getFoodName().toLowerCase() + "%";
                Predicate foodNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(foodJoin.get("foodName")), foodNameValue);
                predicates.add(foodNamePredicate);
            }

            if (foodListRequestDTO.getCategoryId() != null) {
                Predicate categoryPredicate = criteriaBuilder.equal(
                        foodJoin.get("category").get("categoryId"), foodListRequestDTO.getCategoryId()
                );
                predicates.add(categoryPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}
