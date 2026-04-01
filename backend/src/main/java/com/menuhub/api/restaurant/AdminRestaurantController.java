package com.menuhub.api.restaurant;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    private final RestaurantRepository restaurantRepository;

    public AdminRestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public List<Restaurant> list() {
        return restaurantRepository.findAll();
    }

    @PostMapping
    public Restaurant create(@Valid @RequestBody CreateRestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                        .name(request.name())
                        .city(request.city())
                        .district(request.district())
                        .address(request.address())
                        .cuisineType(request.cuisineType())
                        .verified(request.verified())
                        .deleted(false)
                        .build();

        return restaurantRepository.save(restaurant);
    }

    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @Valid @RequestBody UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setName(request.name());
        restaurant.setCity(request.city());
        restaurant.setDistrict(request.district());
        restaurant.setAddress(request.address());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setVerified(request.verified());

        return restaurantRepository.save(restaurant);
    }

    @PostMapping("/{id}/soft-delete")
    public Restaurant softDelete(@PathVariable Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setDeleted(true);
        restaurant.setDeletedAt(LocalDateTime.now());

        return restaurantRepository.save(restaurant);
    }

    @PostMapping("/{id}/restore")
    public Restaurant restore(@PathVariable Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setDeleted(false);
        restaurant.setDeletedAt(null);

        return restaurantRepository.save(restaurant);
    }
}