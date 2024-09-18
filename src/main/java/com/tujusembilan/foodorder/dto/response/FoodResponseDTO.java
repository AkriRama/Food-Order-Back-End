package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponseDTO {
    private int food_id;
    private FoodCategoryDTO categories;
    private String food_name;
    private String image_filename;
    private Boolean is_favorite;
    private Boolean is_cart;
    private String ingredient;
    private int price;

}
