package com.samtech.carapp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CompactCarView extends CarView {

    /**
     * Dark-theme SVG placeholders for listings without an uploaded image.
     * They mimic the colourful car illustrations from the design mockup.
     */
    private static final String[] PLACEHOLDER_SVGS = new String[]{
            buildPlaceholder("#0f172a", "#1e293b", "#ef4444", "🚗"),
            buildPlaceholder("#0a0f1d", "#172554", "#3b82f6", "🚙"),
            buildPlaceholder("#0f172a", "#1c1917", "#f59e0b", "🏎️")
    };

    @Override
    public String render(Car car) {
        return render(car, false, "");
    }

    public String render(Car car, boolean isFavorite, String brandLogoUrl) {
        boolean isFeatured = car.getId() % 5 == 0;

        String topBadge = "SOLD".equalsIgnoreCase(car.getStatus())
                ? "<span class='sold-tag-badge'>SOLD</span>"
                : (isFeatured
                    ? "<span class='featured-badge'>★ Featured</span>"
                    : "<span class='photo-count'>📷 1 photo</span>");

        String heartSvg = """
                <svg viewBox='0 0 24 24' fill='%s' stroke='currentColor' stroke-width='2.2' stroke-linecap='round' stroke-linejoin='round'>
                    <path d='M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z'/>
                </svg>
                """.formatted(isFavorite ? "currentColor" : "none");

        String heartButton = """
                <form method='post' action='/favorites/%d/toggle' onclick='event.stopPropagation()' style='margin:0;'>
                    <button type='submit' class='heart-button %s' aria-label='Toggle favorite'>%s</button>
                </form>
                """.formatted(car.getId(), isFavorite ? "active" : "", heartSvg);

        String imageSrc = getImageSrc(car);
        String fallback = PLACEHOLDER_SVGS[Math.abs(car.getId()) % PLACEHOLDER_SVGS.length];

        return """
                <div class="car-card">
                    <a class="car-card-link" href="/cars/%d">
                        <div class="car-image">
                            %s
                            <img src="%s" alt="%s" onerror="this.onerror=null;this.src='%s'"/>
                        </div>
                    </a>
                    %s
                    <a class="car-card-link" href="/cars/%d">
                        <div class="car-card-content">
                            <h3>%s</h3>
                            <div class="price-line">
                                <span class="price-actual">Rs %,.0f</span>
                            </div>
                            <div class="car-meta-pills">
                                <span class="car-meta-pill">%d</span>
                                <span class="car-meta-pill">%s</span>
                                <span class="car-meta-pill">%,d mi</span>
                            </div>
                            <div class="car-card-footer">
                                <span class="loc">%s</span>
                                <span class="view-link">View →</span>
                            </div>
                        </div>
                    </a>
                </div>
                """.formatted(
                car.getId(),
                topBadge,
                escape(imageSrc),
                escape(car.getTitle()),
                fallback,
                heartButton,
                car.getId(),
                escape(car.getTitle()),
                car.getPrice(),
                car.getYear(),
                escape(blankToDash(car.getTransmission())),
                car.getMileage(),
                escape(blankToDash(car.getCity()))
        );
    }

    private static String blankToDash(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        return value;
    }

    private static String getImageSrc(Car car) {
        String imagePath = car.getImagePath();
        if (imagePath == null || imagePath.isBlank()) {
            return PLACEHOLDER_SVGS[Math.abs(car.getId()) % PLACEHOLDER_SVGS.length];
        }
        return imagePath;
    }

    /** Builds a dark-gradient SVG placeholder with a centred emoji for the no-image state. */
    private static String buildPlaceholder(String bgFrom, String bgTo, String accent, String emoji) {
        String svg = """
                <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 640 400'>
                  <defs>
                    <radialGradient id='glow' cx='50%%' cy='55%%' r='55%%'>
                      <stop offset='0%%' stop-color='%s' stop-opacity='0.45'/>
                      <stop offset='100%%' stop-color='%s' stop-opacity='0'/>
                    </radialGradient>
                    <linearGradient id='bg' x1='0' y1='0' x2='0' y2='1'>
                      <stop offset='0%%' stop-color='%s'/>
                      <stop offset='100%%' stop-color='%s'/>
                    </linearGradient>
                  </defs>
                  <rect width='640' height='400' fill='url(#bg)'/>
                  <circle cx='320' cy='220' r='160' fill='url(#glow)'/>
                  <text x='50%%' y='55%%' text-anchor='middle' dominant-baseline='middle' font-size='150'>%s</text>
                </svg>
                """.formatted(accent, accent, bgFrom, bgTo, emoji);
        String encoded = URLEncoder.encode(svg, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "data:image/svg+xml;charset=UTF-8," + encoded;
    }
}
