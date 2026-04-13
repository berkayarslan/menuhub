package com.menuhub.api.menu;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants/{restaurantId}/menu-items")
public class AdminMenuController {

    private final MenuService menuService;

    public AdminMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public MenuItem create(@PathVariable Long restaurantId, @Valid @RequestBody CreateMenuItemRequest request) {
        return menuService.create(restaurantId, request);
    }

    @PutMapping("/{menuItemId}")
    public MenuItem update(@PathVariable Long restaurantId,
                           @PathVariable Long menuItemId,
                           @Valid @RequestBody UpdateMenuItemRequest request) {
        return menuService.update(restaurantId, menuItemId, request);
    }

    @DeleteMapping("/{menuItemId}")
    public void delete(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        menuService.delete(restaurantId, menuItemId);
    }

    @GetMapping("/{menuItemId}/history")
    public Object getItemHistory(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        return menuService.getItemHistory(restaurantId, menuItemId);
    }
}