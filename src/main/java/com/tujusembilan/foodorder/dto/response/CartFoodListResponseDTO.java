package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartFoodListResponseDTO {
    private int food_id;
    private int category_id;
    private String food_name;
    private String image_filename;
    private int price;
    private String ingredient;
    private String created_by;
    private Timestamp created_time;
    private String modified_by;
    private Timestamp modified_time;
    private CartCategoryDTO category;

}
