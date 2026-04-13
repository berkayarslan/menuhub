package com.menuhub.api.restaurant;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    public AdminRestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Restaurant> list() {
        return restaurantService.listAdmin();
    }

    @PostMapping
    public Restaurant create(@Valid @RequestBody CreateRestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @Valid @RequestBody UpdateRestaurantRequest request) {
        return restaurantService.update(id, request);
    }

    @PostMapping("/{id}/soft-delete")
    public Restaurant softDelete(@PathVariable Long id) {
        return restaurantService.softDelete(id);
    }

    @PostMapping("/{id}/restore")
    public Restaurant restore(@PathVariable Long id) {
        return restaurantService.restore(id);
    }
}