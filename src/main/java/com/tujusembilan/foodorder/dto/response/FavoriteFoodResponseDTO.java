package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteFoodResponseDTO {
    private int foodId;
    private FoodCategoryDTO categories;
    private String namamakanan;
    private String urlgambar;
    private int harga;
    private Boolean is_favorite;
    private Boolean is_cart;
}
