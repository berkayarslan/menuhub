package com.menuhub.api.restaurant;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public List<Restaurant> list(@RequestParam(required = false) String q) {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<Restaurant> visibleRestaurants = restaurants.stream()
                        .filter(r -> !r.isDeleted())
                        .toList();

        if (q == null || q.isBlank()) {
            return visibleRestaurants;
        }

        String query = q.toLowerCase();
        return visibleRestaurants.stream()
                        .filter(r ->
                                                (r.getName() != null && r.getName().toLowerCase().contains(query)) ||
                                                (r.getDistrict() != null && r.getDistrict().toLowerCase().contains(query))
                        )
                        .toList();
    }

    @GetMapping("/{id}")
    public Restaurant detail(@PathVariable Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.isDeleted()) {
            throw new RuntimeException("Restaurant not found");
        }

        return restaurant;
    }
}