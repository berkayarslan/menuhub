package com.menuhub.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.menuhub.api.menu.MenuItem;
import com.menuhub.api.menu.MenuItemRepository;
import com.menuhub.api.restaurant.Restaurant;
import com.menuhub.api.restaurant.RestaurantRepository;

@SpringBootApplication
public class MenuhubApplication {
    public static void main(String[] args) {
        SpringApplication.run(MenuhubApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(RestaurantRepository restaurants, MenuItemRepository menuItems) {
        return args -> {
            if (restaurants.count() == 0) {
                Restaurant r1 = restaurants.save(Restaurant.builder()
                                                                 .name("Kebapçı Halil")
                                                                 .city("İstanbul")
                                                                 .district("Kadıköy")
                                                                 .address("Moda Caddesi No:10")
                                                                 .cuisineType("Kebap")
                                                                 .verified(false)
                                                                 .build());

                Restaurant r2 = restaurants.save(Restaurant.builder()
                                                                 .name("Pide Bahçesi")
                                                                 .city("İstanbul")
                                                                 .district("Beşiktaş")
                                                                 .address("Çarşı Sokak No:5")
                                                                 .cuisineType("Pide")
                                                                 .verified(true)
                                                                 .build());

                menuItems.save(MenuItem.builder().restaurant(r1).category("Kebaplar").name("Adana Kebap").priceAmount(340.0).currency("TRY").build());
                menuItems.save(MenuItem.builder().restaurant(r1).category("Kebaplar").name("Urfa Kebap").priceAmount(340.0).currency("TRY").build());
                menuItems.save(MenuItem.builder().restaurant(r1).category("İçecekler").name("Ayran").priceAmount(40.0).currency("TRY").build());

                menuItems.save(MenuItem.builder().restaurant(r2).category("Pideler").name("Kuşbaşılı Pide").priceAmount(280.0).currency("TRY").build());
                menuItems.save(MenuItem.builder().restaurant(r2).category("Pideler").name("Karışık Pide").priceAmount(310.0).currency("TRY").build());
            }
        };
    }
}