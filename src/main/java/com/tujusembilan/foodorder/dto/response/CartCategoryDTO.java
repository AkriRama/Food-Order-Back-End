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
public class CartCategoryDTO {
    private int category_id;
    private String category_name;
    private Boolean is_deleted;
    private String created_by;
    private Timestamp created_time;
    private String modified_by;
    private Timestamp modified_time;
}
