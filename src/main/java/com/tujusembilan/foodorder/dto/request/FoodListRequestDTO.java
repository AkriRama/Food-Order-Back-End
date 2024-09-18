package com.tujusembilan.foodorder.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodListRequestDTO {
    private String foodName;
    private Integer categoryId;
}
