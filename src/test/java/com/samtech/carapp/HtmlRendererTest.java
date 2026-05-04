package com.samtech.carapp;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlRendererTest {

    private static Car sampleCar(int id, String title, String make, String model,
                                 int year, double price, int mileage) {
        return new Car(
                id,
                title,
                make,
                model,
                "Black",
                "Karachi",
                year,
                price,
                mileage,
                "Clean car",
                "/toyota.jpg",
                2,
                "seller1",
                "AVAILABLE",
                "Car",
                "Automatic",
                "Petrol",
                "Local",
                "Genuine",
                "",
                "Sedan"
        );
    }

    @Test
    void homePageShouldRenderFilterAndCardElements() {
        Car car = sampleCar(1, "Toyota Corolla GLi", "Toyota", "Corolla", 2021, 4_700_000, 30_000);
        String html = HtmlRenderer.homePage(
                List.of(car),
                Map.of("status", "AVAILABLE"),
                new User(3, "buyer1", "BUYER"),
                Set.of(1),
                ""
        );
        assertTrue(html.contains("Apply Filters"));
        assertTrue(html.contains("Latest Listings") || html.contains("Cars for sale"));
        assertTrue(html.contains("Model"));
    }

    @Test
    void carDetailsPageShouldShowFairPriceEstimateForKnownModel() {
        Car car = sampleCar(11, "Toyota Corolla Altis Grande", "Toyota", "Corolla", 2018, 4_200_000, 80_000);
        CarDetails details = new CarDetails(
                car,
                new User(2, "seller1", "SELLER", "seller1@example.com", "03001234567"),
                List.of("/toyota.jpg")
        );
        String html = HtmlRenderer.carDetailsPage(details, new User(3, "buyer1", "BUYER"), false, "");
        assertTrue(html.contains("Estimated fair price"),
                "Car details page must include the fair-price badge for a recognised model.");
        assertTrue(html.contains("fair-estimate"),
                "Car details page must include the fair-estimate CSS hook.");
    }

    @Test
    void carDetailsShouldFallBackWhenAskingPriceIsWildlyOff() {
        // Honda City 2020 listed at Rs 50 lacs is well above realistic market;
        // the estimator should kick into fallback ("≈ Rs N Lacs below seller's price").
        Car car = sampleCar(99, "Honda City for sale", "Honda", "City", 2020, 5_000_000, 80_000);
        CarDetails details = new CarDetails(
                car,
                new User(2, "seller1", "SELLER", "seller1@example.com", "03001234567"),
                List.of("/honda.jpg")
        );
        String html = HtmlRenderer.carDetailsPage(details, null, false, "");
        assertTrue(html.contains("below seller"),
                "Fallback verdict ('below seller's price') must appear when calc is >50% off.");
        assertTrue(html.contains("delta-fallback"),
                "Fallback CSS class must be applied so the badge renders in conservative styling.");
    }

    @Test
    void homePageShouldNotShowFairPriceEstimate() {
        Car car = sampleCar(12, "Toyota Corolla Altis", "Toyota", "Corolla", 2018, 4_200_000, 80_000);
        String html = HtmlRenderer.homePage(
                List.of(car),
                Map.of(),
                null,
                Set.of(),
                ""
        );
        assertFalse(html.contains("Estimated fair price"),
                "Fair-price estimate must NOT appear on the dashboard listing.");
    }
}
