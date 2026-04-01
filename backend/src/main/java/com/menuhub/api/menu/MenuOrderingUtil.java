package com.menuhub.api.menu;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MenuOrderingUtil {

    private MenuOrderingUtil() {
    }

    private static final Map<String, Integer> CATEGORY_PRIORITY = Map.ofEntries(
                    Map.entry("kebaplar", 10),
                    Map.entry("izgaralar", 20),
                    Map.entry("donerler", 30),
                    Map.entry("pideler", 40),
                    Map.entry("lahmacunlar", 50),
                    Map.entry("burgerler", 60),
                    Map.entry("pizzalar", 70),
                    Map.entry("corbalar", 80),
                    Map.entry("salatalar", 90),
                    Map.entry("ara sicaklar", 100),
                    Map.entry("baslangiclar", 110),
                    Map.entry("tatlilar", 190),
                    Map.entry("icecekler", 200)
    );

    public static List<MenuItem> sortMenuItems(List<MenuItem> items) {
        return items.stream()
                        .sorted((a, b) -> {
                            int categoryCompare = Integer.compare(priorityOf(a.getCategory()), priorityOf(b.getCategory()));
                            if (categoryCompare != 0) {
                                return categoryCompare;
                            }

                            int normalizedCategoryCompare =
                                            normalize(a.getCategory()).compareTo(normalize(b.getCategory()));
                            if (normalizedCategoryCompare != 0) {
                                return normalizedCategoryCompare;
                            }

                            return safe(a.getName()).compareToIgnoreCase(safe(b.getName()));
                        })
                        .toList();
    }

    private static int priorityOf(String category) {
        return CATEGORY_PRIORITY.getOrDefault(normalize(category), 150);
    }

    private static String normalize(String value) {
        if (value == null) return "";

        String text = value.trim().toLowerCase(new Locale("tr", "TR"));

        text = text
                        .replace("ı", "i")
                        .replace("ğ", "g")
                        .replace("ü", "u")
                        .replace("ş", "s")
                        .replace("ö", "o")
                        .replace("ç", "c");

        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "");

        return text.replaceAll("\\s+", " ").trim();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}