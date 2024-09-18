package com.tujusembilan.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResponseDTO {
    private int orderid;
//    private String imageUrl;
    private int totalItem;
    private Date tanggalOrder;
    private int totalOrder;
    private List<HistoryOrderDetailResponseDTO> orderDetails;

}
