package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryOrderDetailResponseDTO {
    //TAMBAHAN
    private String imageUrl;
    private String foodName;

    //DEFAULT
    private int orderDetailid;
    private int quantity;
    private int totalHarga;
}
