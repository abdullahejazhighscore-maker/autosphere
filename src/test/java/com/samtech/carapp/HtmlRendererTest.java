package com.samtech.carapp;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlRendererTest {
    @Test
    void homePageShouldRenderFilterAndStatusElements() {
        Car car = new Car(1, "Toyota Corolla", "Toyota", "Corolla", "Black", "Karachi",
                2021, 23000, 30000, "Clean car", "/toyota.jpg", 2, "seller1", "AVAILABLE");
        String html = HtmlRenderer.homePage(List.of(car), Map.of("status", "AVAILABLE"), new User(3, "buyer1", "BUYER"), Set.of(1), "");
        assertTrue(html.contains("Apply Filters"));
        assertTrue(html.contains("Any Status"));
        assertTrue(html.contains("Model"));
    }
}
