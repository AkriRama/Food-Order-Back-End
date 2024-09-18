package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private FoodListResponseDTO data;
    private String message;
    private int statusCode;
    private String status;
}
