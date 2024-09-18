package com.tujusembilan.foodorder.repositories;

import com.tujusembilan.foodorder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    User findByUserId(int userId);

    Boolean existsByUsername(String username);
}
