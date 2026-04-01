package com.menuhub.api.menu;

import com.menuhub.api.restaurant.Restaurant;
import com.menuhub.api.restaurant.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants/{restaurantId}/menu-items")
public class AdminMenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public AdminMenuController(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    public MenuItem create(@PathVariable Long restaurantId, @Valid @RequestBody CreateMenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        MenuItem item = MenuItem.builder()
                        .restaurant(restaurant)
                        .category(request.category())
                        .name(request.name())
                        .descriptionText(request.descriptionText())
                        .priceAmount(request.priceAmount())
                        .currency(request.currency())
                        .build();

        return menuItemRepository.save(item);
    }

    @PutMapping("/{menuItemId}")
    public MenuItem update(@PathVariable Long restaurantId,
                           @PathVariable Long menuItemId,
                           @Valid @RequestBody UpdateMenuItemRequest request) {

        MenuItem item = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (item.getRestaurant() == null || !item.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Menu item does not belong to restaurant");
        }

        item.setCategory(request.category());
        item.setName(request.name());
        item.setDescriptionText(request.descriptionText());
        item.setPriceAmount(request.priceAmount());
        item.setCurrency(request.currency());

        return menuItemRepository.save(item);
    }

    @DeleteMapping("/{menuItemId}")
    public void delete(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (item.getRestaurant() == null || !item.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Menu item does not belong to restaurant");
        }

        menuItemRepository.delete(item);
    }
}