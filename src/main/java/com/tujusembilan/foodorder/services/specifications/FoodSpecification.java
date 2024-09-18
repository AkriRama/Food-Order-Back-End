package com.tujusembilan.foodorder.services.specifications;

import com.tujusembilan.foodorder.dto.request.FoodListRequestDTO;
import com.tujusembilan.foodorder.models.Food;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class FoodSpecification {
    public static Specification<Food> foodFilter(FoodListRequestDTO foodListRequestDTO) {
        return (root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (foodListRequestDTO.getFoodName() != null) {
            String foodNameValue = "%" + foodListRequestDTO.getFoodName().toLowerCase() + "%";
            Predicate foodNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("foodName")), foodNameValue);
            predicates.add(foodNamePredicate);
        }

        if (foodListRequestDTO.getCategoryId() != null) {
            Predicate categoryPredicate = criteriaBuilder.equal(
                    root.get("category").get("categoryId"), foodListRequestDTO.getCategoryId()
            );
            predicates.add(categoryPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }
}
