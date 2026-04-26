package com.samtech.carapp;

public class CompactCarView extends CarView {
    @Override
    public String render(Car car) {
        return """
                <a class="car-card-link" href="/cars/%d">
                    <div class="car-card">
                        <img src="%s" alt="%s"/>
                        <div class="car-card-content">
                            <h3>%s</h3>
                            <p><strong>Model:</strong> %s</p>
                            <p><strong>Color:</strong> %s</p>
                            <p class="price">Rs %, .0f</p>
                        </div>
                    </div>
                </a>
                """.formatted(
                car.getId(),
                escape(car.getImagePath()),
                escape(car.getTitle()),
                escape(car.getTitle()),
                escape(car.getModel()),
                escape(car.getColor()),
                car.getPrice()
        );
    }
}
