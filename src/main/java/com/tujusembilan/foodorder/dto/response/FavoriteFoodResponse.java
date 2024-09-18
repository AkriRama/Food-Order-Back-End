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
public class FavoriteFoodResponse {
    private long total;
    private List<FavoriteFoodResponseDTO> data;
    private String message;
    private int statusCode;
    private String status;
}
