package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodListResponse {
    private long total;
    private List<FoodListResponseDTO> data;
    private String message;
    private int statusCode;
    private String status;
}
