package com.samtech.carapp;

public class CompactCarView extends CarView {
    @Override
    public String render(Car car) {
        return render(car, false, "");
    }

    public String render(Car car, boolean isFavorite, String brandLogoUrl) {
        String soldBadge = "SOLD".equalsIgnoreCase(car.getStatus())
                ? "<span class='sold-tag-badge'>SOLD</span>"
                : "";

        String heartSvg = """
                <svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.2' stroke-linecap='round' stroke-linejoin='round'>
                    <path d='M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z'/>
                </svg>
                """;

        String heartButton = """
                <form method='post' action='/favorites/%d/toggle' onclick='event.stopPropagation()' style='margin:0;'>
                    <button type='submit' class='heart-button %s' aria-label='Toggle favorite'>
                        %s
                    </button>
                </form>
                """.formatted(car.getId(), isFavorite ? "active" : "", heartSvg);

        String compareIcon = "<a class='compare-mini-icon' title='Compare' href='/compare' onclick='event.stopPropagation()'>&#9881;</a>";

        return """
                <div class="car-card">
                    <a class="car-card-link" href="/cars/%d">
                        <div class="car-image">
                            <img src="%s" alt="%s" onerror="this.src='https://via.placeholder.com/640x420?text=No+Image'"/>
                            %s
                            <span class="photo-count">📷 1</span>
                        </div>
                    </a>
                    %s
                    %s
                    <a class="car-card-link" href="/cars/%d">
                        <div class="car-card-content">
                            <h3>%s <span class="menu-dots">⋮</span></h3>
                            <div class="price-line">
                                <span class="price-actual">Rs %,.0f</span>
                            </div>
                            <p class="car-meta">
                                <strong>%s</strong> · %d · %s · %,d mi<br/>
                                %s · %s
                            </p>
                        </div>
                    </a>
                </div>
                """.formatted(
                car.getId(),
                escape(getImageSrc(car.getImagePath())),
                escape(car.getTitle()),
                soldBadge,
                heartButton,
                compareIcon,
                car.getId(),
                escape(car.getTitle()),
                car.getPrice(),
                escape(blankToDash(car.getModel())),
                car.getYear(),
                escape(blankToDash(car.getTransmission())),
                car.getMileage(),
                escape(blankToDash(car.getFuelType())),
                escape(blankToDash(car.getCity()))
        );
    }

    private static String blankToDash(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        return value;
    }

    private static String getImageSrc(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return "https://via.placeholder.com/640x420?text=No+Image";
        }
        return imagePath;
    }
}
