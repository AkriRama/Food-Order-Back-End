package com.tujusembilan.foodorder.controllers;

import com.tujusembilan.foodorder.dto.request.*;
import com.tujusembilan.foodorder.dto.response.*;
import com.tujusembilan.foodorder.services.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food-order")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/foods")
    public ResponseEntity<FoodListResponse> getFoods(
            @ModelAttribute FoodListRequestDTO foodListRequestDTO, @PageableDefault(sort = "foodName")
            PageRequest pageRequest
            ) {
        return foodService.getFoods(foodListRequestDTO, pageRequest.getPage());
    }

    @PutMapping("/foods/{foodId}/favorites")
    public ResponseEntity<FoodListResponse> toggleFavorite(@PathVariable Integer foodId) {
        return foodService.toggleFavorite(foodId);
    }

    @PostMapping("/cart")
    public ResponseEntity<CartResponse> addCart(@RequestBody CartRequest request) {
        return foodService.addCart(request);
    }

    @DeleteMapping("/cart/{foodId}")
    public ResponseEntity<CartResponse> deleteCart(@PathVariable Integer foodId) {
        return foodService.deleteCart(foodId);
    }

    @GetMapping("/foods/{foodId}")
    public ResponseEntity<FoodResponse> getFoodById(Integer foodId) {
        return foodService.getFoodById(foodId);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartListResponse> getAllCart() {
        return foodService.getAllCart();
    }

    @GetMapping("/foods/my-favorite-foods")
    public ResponseEntity<FavoriteFoodResponse> getFavoriteFood(
            @ModelAttribute FoodListRequestDTO foodListRequestDTO,
            PageRequest pageRequest) {
        return foodService.getFavoriteFood(foodListRequestDTO, pageRequest.getPage());
    }

    @PutMapping("/cart/{cartId}")
    public ResponseEntity<CartUpdateResponse> updateCart(@RequestBody CartUpdateRequest request, @PathVariable Integer cartId) {
        return foodService.updateQtyCart(request, cartId);
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<CartUpdateResponse> checkoutCart(@RequestBody CheckOutRequest request) {
        return foodService.checkoutCart(request);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<HistoryResponse> getAllHistoryOrder(@PathVariable Integer userId) {
        return foodService.getHistoryOrder(userId);
    }
}
