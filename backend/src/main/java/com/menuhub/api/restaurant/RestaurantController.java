package com.menuhub.api.restaurant;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Restaurant> list(@RequestParam(required = false) String q) {
        return restaurantService.listPublic(q);
    }

    @GetMapping("/{id}")
    public Restaurant detail(@PathVariable Long id) {
        return restaurantService.detailPublic(id);
    }
}