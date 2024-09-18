package com.tujusembilan.foodorder.services;

import com.tujusembilan.foodorder.dto.request.*;
import com.tujusembilan.foodorder.dto.response.*;
import com.tujusembilan.foodorder.models.*;
import com.tujusembilan.foodorder.models.OrderDetail;
import com.tujusembilan.foodorder.repositories.*;
import com.tujusembilan.foodorder.services.specifications.FavoriteFoodSpesification;
import com.tujusembilan.foodorder.services.specifications.FoodSpecification;
import com.tujusembilan.foodorder.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FavoriteFoodRepository favoriteFoodRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<FoodListResponse> getFoods(FoodListRequestDTO foodListRequestDTO, Pageable page) {
        try {
            Specification<Food> foodSpec = FoodSpecification.foodFilter(foodListRequestDTO);

            Page<Food> foods = foodRepository.findAll(foodSpec, page);

            if(foods.isEmpty()) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new FoodListResponse(0, null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_GATEWAY.getReasonPhrase()));
            }

            int userId = JwtUtil.getCurrentUser().getUserId();

            List<FoodListResponseDTO> foodsDTO = foods.stream().map(food ->
                    new FoodListResponseDTO(
                        food.getFoodId(),
                        new FoodCategoryDTO(food.getCategory().getCategoryId(), food.getCategory().getCategoryName()),
                        food.getFoodName(),
                        food.getPrice(),
                        food.getImageFilename(),
                        getIsCart(food.getFoodId(), userId),
                        getIsFavorite(food.getFoodId(), userId)
                    )
            ).collect(Collectors.toList());

            long totalData = foods.getTotalElements();

            String message = messageSource.getMessage("food.found", null, Locale.getDefault());

            return ResponseEntity
                    .ok()
                    .body(new FoodListResponse(totalData, foodsDTO, message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new FoodListResponse(0, null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<FoodListResponse> toggleFavorite(Integer foodId) {
        try {
            if (foodId == null) {
                String message = messageSource. getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new FoodListResponse(0, null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Optional<Food> optionalFood = foodRepository.findById(foodId);
            if (!optionalFood.isPresent()) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new FoodListResponse(0, null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            int userId = JwtUtil.getCurrentUser().getUserId();

            Optional<FavoriteFood> optionalFavoriteFood = favoriteFoodRepository.findFavoriteByFoodAndUser(foodId, userId);

            if (optionalFavoriteFood.isPresent()) {
                FavoriteFood favoriteFood = optionalFavoriteFood.get();
                favoriteFood.setIsFavorite(!favoriteFood.getIsFavorite());
                favoriteFoodRepository.save(favoriteFood);
            } else {
                FavoriteFood newFavoriteFood = FavoriteFood.builder()
                        .food(foodRepository.findById(foodId).orElseThrow())
                        .user(userRepository.findById(userId).orElseThrow())
                        .isFavorite(true)
                        .build();
                favoriteFoodRepository.save(newFavoriteFood);
            }

            String message = messageSource.getMessage(getIsFavorite(foodId, userId) ? "add.favorite" : "delete.favorite", null, Locale.getDefault());
            String formateMessage = MessageFormat.format(message, optionalFood.get().getFoodName());
            return ResponseEntity
                    .ok()
                    .body(new FoodListResponse(1, null, formateMessage, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new FoodListResponse(0, null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
        }
    }

    public ResponseEntity<CartResponse> addCart(CartRequest request) {
        try {
            if (request.getFoodId() == null) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new CartResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Optional<Food> optionalFood = foodRepository.findById(request.getFoodId());
            if (!optionalFood.isPresent()) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new CartResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Food food = optionalFood.get();

            int userId = JwtUtil.getCurrentUser().getUserId();
            User user = userRepository.findByUserId(userId);


            Optional<Cart> optionalCart = cartRepository.findCartByFoodAndUser(food.getFoodId(), userId);

            if (optionalCart.isEmpty()) {
                Cart cart = Cart.builder()
                        .food(food)
                        .createdBy(user.getUsername())
                        .modifiedBy(user.getUsername())
                        .isDeleted(false)
                        .qty(1)
                        .user(User.builder().userId(userId).build()
                ).build();
                cartRepository.save(cart);
            } else {
                Cart cart = optionalCart.get();
                boolean deleted = cart.getIsDeleted();
                cart.setIsDeleted(!deleted);
                if (!deleted) {
                    cart.setQty(0);
                } else {
                    cart.setQty(1);
                }
                cartRepository.save(cart);
            }



            FoodListResponseDTO foodDTO = new FoodListResponseDTO(
                    food.getFoodId(),
                    new FoodCategoryDTO(food.getCategory().getCategoryId(), food.getCategory().getCategoryName()),
                    food.getFoodName(),
                    food.getPrice(),
                    food.getImageFilename(),
                    getIsCart(food.getFoodId(), userId),
                    getIsFavorite(food.getFoodId(), userId)
            );

            String message = messageSource.getMessage(getIsCart(food.getFoodId(), userId) ? "add.cart.success" : "delete.cart.success", null, Locale.getDefault());
            String formatMessage = MessageFormat.format(message, optionalFood.get().getFoodName());
            return ResponseEntity
                    .ok()
                    .body(new CartResponse(null, formatMessage, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new CartResponse(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(),  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<CartResponse> deleteCart(Integer foodId) {
        try {
            if (foodId == null) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new CartResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            int userId = JwtUtil.getCurrentUser().getUserId();

            Optional<Cart> optionalCart = cartRepository.findCartByFoodAndUser(foodId, userId);

            if (!optionalCart.isPresent()) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new CartResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            cartRepository.delete(optionalCart.get());

            Food food = optionalCart.get().getFood();
            FoodListResponseDTO foodDTO = new FoodListResponseDTO(
                    food.getFoodId(),
                    new FoodCategoryDTO(food.getCategory().getCategoryId(), food.getCategory().getCategoryName()),
                    food.getFoodName(),
                    food.getPrice(),
                    food.getImageFilename(),
                    getIsCart(food.getFoodId(), userId),
                    getIsFavorite(food.getFoodId(), userId)
            );

            String message = messageSource.getMessage("delete.cart.success", null, Locale.getDefault());
            String formatMessage = MessageFormat.format(message, food.getFoodName());
            return ResponseEntity
                    .ok()
                    .body(new CartResponse(foodDTO, formatMessage, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new CartResponse(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(),  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<FoodResponse> getFoodById(Integer foodId) {
        try {
            if (foodId == null) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .badRequest()
                        .body(new FoodResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Optional<Food> optionalFood = foodRepository.findById(foodId);
            if (optionalFood.isEmpty()) {
                String message = messageSource.getMessage("food.by.id.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new FoodResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Food food = optionalFood.get();
            int userId = JwtUtil.getCurrentUser().getUserId();

            FoodResponseDTO foodDTO = new FoodResponseDTO(
                    food.getFoodId(),
                    new FoodCategoryDTO(food.getCategory().getCategoryId(), food.getCategory().getCategoryName()),
                    food.getFoodName(),
                    food.getImageFilename(),
                    getIsFavorite(foodId, userId),
                    getIsCart(foodId, userId),
                    food.getIngridient(),
                    food.getPrice()
            );

            String message = messageSource.getMessage("food.by.id.found", null, Locale.getDefault());
            String formatMessage = MessageFormat.format(message, optionalFood.get().getFoodName());
            return ResponseEntity
                    .ok()
                    .body(new FoodResponse(foodDTO, formatMessage, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new FoodResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
        }
    }


    public ResponseEntity<CartListResponse> getAllCart() {
        try {
            int userId = JwtUtil.getCurrentUser().getUserId();
            List<Cart> carts = cartRepository.findCartByUserId(userId);

            if (carts.isEmpty()) {
                String message = messageSource.getMessage("cart.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new CartListResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            List<CartListResponseDTO> cartDTO = carts.stream().map(cart -> new CartListResponseDTO(
                    cart.getCartId(),
                    cart.getUser().getUserId(),
                    cart.getUser().getUsername(),
                    cart.getQty(),
                    cart.getIsDeleted(),
                    cart.getCreatedBy(),
                    cart.getCreatedTime(),
                    cart.getModifiedBy(),
                    cart.getModifiedTime(),
                    new CartFoodListResponseDTO(
                            cart.getFood().getFoodId(),
                            cart.getFood().getCategory().getCategoryId(),
                            cart.getFood().getFoodName(),
                            cart.getFood().getImageFilename(),
                            cart.getFood().getPrice(),
                            cart.getFood().getIngridient(),
                            cart.getFood().getCreatedBy(),
                            cart.getFood().getCreatedTime(),
                            cart.getFood().getModifiedBy(),
                            cart.getFood().getModifiedTime(),
                            new CartCategoryDTO(
                                    cart.getFood().getCategory().getCategoryId(),
                                    cart.getFood().getCategory().getCategoryName(),
                                    cart.getFood().getCategory().getIsDeleted(),
                                    cart.getFood().getCategory().getCreatedBy(),
                                    cart.getFood().getCategory().getCreatedTime(),
                                    cart.getFood().getCategory().getModifiedBy(),
                                    cart.getFood().getCategory().getModifiedTime()
                                )
                            )
            )).collect(Collectors.toList());

            String message = messageSource.getMessage("cart.found", null, Locale.getDefault());
            return ResponseEntity
                    .ok()
                    .body(new CartListResponse(cartDTO, message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new CartListResponse(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<CartUpdateResponse> updateQtyCart(CartUpdateRequest request, Integer cartId) {
        try {
            int userId = JwtUtil.getCurrentUser().getUserId();
            Optional<Cart> optionalCart = cartRepository.findCartByUserAndCart(userId, cartId);

            Cart cart = optionalCart.get();
            cart.setQty(cart.getQty()+request.getQty());
            cartRepository.save(cart);

            String message = messageSource.getMessage("cart.update", null, Locale.getDefault());
            return ResponseEntity
                    .ok()
                    .body(new CartUpdateResponse(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new CartUpdateResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<CartUpdateResponse> checkoutCart(CheckOutRequest request) {
        try {
            int userId = JwtUtil.getCurrentUser().getUserId();
            User user = userRepository.findByUserId(userId);
            int orderId = 0;
            int totalQty, totalPrice;
            for (CartDataDTO cartDataDTO : request.getCartData()) {
                for (Integer cartId : cartDataDTO.getCartId()) {
                    Optional<Cart> optionalCart = cartRepository.findCartByUserAndCart(userId, cartId);
                    totalQty = optionalCart.get().getQty();
                    totalPrice = optionalCart.get().getFood().getPrice()*totalQty;
                    if (orderId == 0) {
                        Order order = Order.builder()
                                .user(user)
                                .totalItem(totalQty)
                                .createdBy(user.getUsername())
                                .totalOrderPrice(totalPrice)
                                .build();
                        Order saveOrder = orderRepository.save(order);
                        orderId = saveOrder.getOrderId();
                    } else {
                        Optional<Order> optionalOrder = orderRepository.findById(orderId);
                        Order order = optionalOrder.get();
                        order.setTotalItem(order.getTotalItem()+totalQty);
                        order.setTotalOrderPrice(order.getTotalOrderPrice()+totalPrice);
                        orderRepository.save(order);
                    }

                    OrderDetail orderDetail = OrderDetail.builder()
                                    .food(optionalCart.get().getFood())
                                    .order(Order.builder().orderId(orderId).build())
                                    .qty(totalQty)
                                    .totalPrice(totalPrice)
                                    .createdBy(user.getUsername())
                                    .createdBy(user.getUsername())
                                    .build();
                    orderDetailRepository.save(orderDetail);

                    cartRepository.delete(optionalCart.get());
                }
            }

            /*carts.stream()
                    .map(cartId -> cartRepository.findCartByUserAndCart(userId, cartId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(cart -> {
                        Order order = Order.builder()
                                .user(User.builder().userId(userId).build())
                                .totalOrderPrice(cart.getQty())
                                .totalOrderPrice(cart.getFood().getPrice()*cart.getQty())
                                .build();
                                orderRepository.save(order);

                                List<Order> orders = orderRepository.findAll();
                                OrderDetail orderDetail = OrderDetail.builder()
                                        .food(Food.builder().foodId(cart.getFood().getFoodId()).build())
                                        .order(Order.builder().orderId(order.getOrderId()).build())
                                        .qty(order.getTotalItem())
                                        .totalPrice(order.getTotalOrderPrice())
                                        .build();

                                cartRepository.delete(cart);
                                return order;
                    });*/

            String message = messageSource.getMessage("cart.checkout", null, Locale.getDefault());
            return ResponseEntity
                    .ok()
                    .body(new CartUpdateResponse(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new CartUpdateResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ResponseEntity<HistoryResponse> getHistoryOrder(Integer userId) {
        try {
            List<Order> orders = orderRepository.findOrderByUser(userId);

            if (orders.isEmpty()) {
                String message = messageSource.getMessage("history.not.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new HistoryResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            List<HistoryResponseDTO> historyResponseDTO = orders.stream()
                    .map(order -> new HistoryResponseDTO(
                            order.getOrderId(),
                            order.getTotalItem(),
                            order.getOrderDate(),
                            order.getTotalOrderPrice(),
                            orderDetailRepository.findOrderDetailByOrder(order.getOrderId()).stream()
                                    .map(orderDetail -> new HistoryOrderDetailResponseDTO(
                                            orderDetail.getFood().getImageFilename(),
                                            orderDetail.getFood().getFoodName(),
                                            orderDetail.getOrderDetailId(),
                                            orderDetail.getQty(),
                                            orderDetail.getTotalPrice()
                                    ))
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());


            /*for (Order order : orders) {
                List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrder(order.getOrderId());
                HistoryOrderDetailResponseDTO historyOrderDetail;
                for (OrderDetail orderDetail : orderDetails) {
                    historyOrderDetail = new HistoryOrderDetailResponseDTO(
                            orderDetail.getOrderDetailId(),
                            orderDetail.getQty(),
                            orderDetail.getTotalPrice()
                    );
                }
                List<HistoryResponseDTO> historyResponseDTO = new HistoryResponseDTO(
                        order.getOrderId(),
                        order.getTotalItem(),
                        order.getOrderDate(),
                        order.getTotalOrderPrice(),
                        historyOrderDetail.getOrderDetailid();

                        )
                )
            }*/



            /*List<HistoryResponseDTO> historyResponseDTO = orders.stream().map(history -> new HistoryResponseDTO (
                    history.getOrderId(),
                    history.getTotalItem(),
                    history.getOrderDate(),
                    history.getTotalOrderPrice(),
                    List<HistoryOrderDetailResponseDTO> history = orderDetails.stream().map(orderDetail -> new HistoryOrderDetailResponseDTO(
                            orderDetail.getOrderDetailId(),
                            orderDetail.getQty(),
                            orderDetail.getTotalPrice()
                    )).collect(Collectors.toList())

            )).collect(Collectors.toList());*/
            String message = messageSource.getMessage("history.found", null, Locale.getDefault());
            return ResponseEntity
                    .ok()
                    .body(new HistoryResponse(historyResponseDTO, message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));

        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new HistoryResponse(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }
    public ResponseEntity<FavoriteFoodResponse> getFavoriteFood(FoodListRequestDTO foodListRequestDTO, Pageable page) {
            try {
                int userId = JwtUtil.getCurrentUser().getUserId();
//                List<FavoriteFood> favoriteFoods = favoriteFoodRepository.findFavoriteByUserAndIsFavorite(userId);

                /*if (favoriteFoods.isEmpty()) {
                    String message = messageSource.getMessage("food.not.found", null, Locale.getDefault());
                    return ResponseEntity
                            .ok()
                            .body(new FavoriteFoodResponse(0, null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
                }*/

                Specification<FavoriteFood> favSpec = FavoriteFoodSpesification.favoriteFoodFilter(foodListRequestDTO);
                Page<FavoriteFood> favoriteFoodPage = favoriteFoodRepository.findAll(favSpec, page);
//                Page<FavoriteFood> favoriteFoodPage = favoriteFoodRepository.findFavoriteByUser(userId, page);
                long totalData = favoriteFoodPage.getTotalElements();

                List<FavoriteFoodResponseDTO> favoriteFoodDTO = favoriteFoodPage.stream().map(favoriteFood -> new FavoriteFoodResponseDTO(
                                favoriteFood.getFood().getFoodId(),
                                new FoodCategoryDTO(
                                        favoriteFood.getFood().getCategory().getCategoryId(),
                                        favoriteFood.getFood().getCategory().getCategoryName()
                                ),
                                favoriteFood.getFood().getFoodName(),
                                favoriteFood.getFood().getImageFilename(),
                                favoriteFood.getFood().getPrice(),
                                favoriteFood.getIsFavorite(),
                                getIsCart(favoriteFood.getFood().getFoodId(), userId)
                        )
                ).collect(Collectors.toList());

                String message = messageSource.getMessage("favorite.found", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new FavoriteFoodResponse(totalData,favoriteFoodDTO, message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
            } catch (Exception e) {
                log.error(null, e);
                String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
                return ResponseEntity
                        .internalServerError()
                        .body(new FavoriteFoodResponse(0,null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }
    }


    private Boolean getIsCart(Integer foodId, Integer userId) {
        Optional<Cart> cart = cartRepository.findCartByFoodAndUser(foodId, userId);
        if (cart.isPresent() && !cart.get().getIsDeleted()) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean getIsFavorite(Integer foodId, Integer userId) {
        Optional<FavoriteFood> favoriteFood = favoriteFoodRepository.findFavoriteByFoodAndUser(foodId, userId);
        if (favoriteFood.isPresent()) {
            return favoriteFood.get().getIsFavorite();
        } else {
            return false;
        }
    }



}
