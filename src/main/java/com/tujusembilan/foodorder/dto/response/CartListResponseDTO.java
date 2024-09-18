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
public class CartListResponseDTO {
    private int cart_id;
    private int user_id;
    private String user_name;
    private int qty;
    private Boolean is_deleted;
    private String created_by;
    private Timestamp created_time;
    private String modified_by;
    private Timestamp modified_time;
    private CartFoodListResponseDTO foods;

}
