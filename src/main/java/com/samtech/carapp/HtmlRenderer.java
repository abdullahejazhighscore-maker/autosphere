package com.samtech.carapp;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HtmlRenderer {
    private static final CompactCarView COMPACT_VIEW = new CompactCarView();
    private static final int CURRENT_YEAR = Year.now().getValue();
    private static final int MAX_YEAR = Math.max(2026, CURRENT_YEAR);
    /** Inline-SVG placeholder shown when a listing has no working image. */
    private static final String NO_IMAGE_PLACEHOLDER =
            "data:image/svg+xml;utf8,"
                    + "%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 800 500'%3E"
                    + "%3Crect width='100%25' height='100%25' fill='%23e2e8f0'/%3E"
                    + "%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' "
                    + "font-family='Arial,sans-serif' font-size='28' fill='%2364748b'%3ENo image available%3C/text%3E"
                    + "%3C/svg%3E";

    /* ===== Pakistani brand catalog: logo URL + founding year + popular models ===== */
    private static final Map<String, String> BRAND_LOGOS = new LinkedHashMap<>();
    private static final Map<String, Integer> BRAND_FOUNDED = new LinkedHashMap<>();
    private static final Map<String, String> BRAND_COLORS = new LinkedHashMap<>();
    private static final Map<String, List<String>> BRAND_MODELS = new LinkedHashMap<>();

    static {
        BRAND_LOGOS.put("Suzuki", "/brand-logos/suzuki.svg");
        BRAND_LOGOS.put("Toyota", "/brand-logos/toyota.svg");
        BRAND_LOGOS.put("Honda", "/brand-logos/honda.svg");
        BRAND_LOGOS.put("Hyundai", "/brand-logos/hyundai.svg");
        BRAND_LOGOS.put("KIA", "/brand-logos/kia.svg");
        BRAND_LOGOS.put("Changan", "/brand-logos/changan.png");
        BRAND_LOGOS.put("MG", "/brand-logos/mg.svg");
        BRAND_LOGOS.put("Nissan", "/brand-logos/nissan.svg");
        BRAND_LOGOS.put("Mitsubishi", "/brand-logos/mitsubishi.svg");
        BRAND_LOGOS.put("Daihatsu", "/brand-logos/daihatsu.svg");
        BRAND_LOGOS.put("FAW", "/brand-logos/faw.png");
        BRAND_LOGOS.put("Proton", "/brand-logos/proton.svg");
        BRAND_LOGOS.put("Haval", "/brand-logos/haval.svg");
        BRAND_LOGOS.put("DFSK", "/brand-logos/dfsk.svg");
        BRAND_LOGOS.put("Mazda", "/brand-logos/mazda.svg");
        BRAND_LOGOS.put("Subaru", "/brand-logos/subaru.svg");

        BRAND_FOUNDED.put("Suzuki", 1909);
        BRAND_FOUNDED.put("Toyota", 1937);
        BRAND_FOUNDED.put("Honda", 1948);
        BRAND_FOUNDED.put("Hyundai", 1967);
        BRAND_FOUNDED.put("KIA", 1944);
        BRAND_FOUNDED.put("Changan", 1957);
        BRAND_FOUNDED.put("MG", 1924);
        BRAND_FOUNDED.put("Nissan", 1933);
        BRAND_FOUNDED.put("Mitsubishi", 1917);
        BRAND_FOUNDED.put("Daihatsu", 1907);
        BRAND_FOUNDED.put("FAW", 1953);
        BRAND_FOUNDED.put("Proton", 1983);
        BRAND_FOUNDED.put("Haval", 2013);
        BRAND_FOUNDED.put("DFSK", 1991);
        BRAND_FOUNDED.put("Mazda", 1920);
        BRAND_FOUNDED.put("Subaru", 1953);

        BRAND_COLORS.put("Suzuki", "#0a4ea0");
        BRAND_COLORS.put("Toyota", "#eb0a1e");
        BRAND_COLORS.put("Honda", "#cc0000");
        BRAND_COLORS.put("Hyundai", "#002c5f");
        BRAND_COLORS.put("KIA", "#bb162b");
        BRAND_COLORS.put("Changan", "#1f4e8c");
        BRAND_COLORS.put("MG", "#cc0000");
        BRAND_COLORS.put("Nissan", "#c3002f");
        BRAND_COLORS.put("Mitsubishi", "#e60012");
        BRAND_COLORS.put("Daihatsu", "#e60012");
        BRAND_COLORS.put("FAW", "#0066b3");
        BRAND_COLORS.put("Proton", "#003f87");
        BRAND_COLORS.put("Haval", "#f4002a");
        BRAND_COLORS.put("DFSK", "#003366");
        BRAND_COLORS.put("Mazda", "#1a1a1a");
        BRAND_COLORS.put("Subaru", "#0d3b73");

        BRAND_MODELS.put("Suzuki", List.of("Alto", "Cultus", "Wagon R", "Mehran", "Bolan", "Swift", "Ravi", "Ciaz", "Khyber", "Margalla"));
        BRAND_MODELS.put("Toyota", List.of("Corolla", "Yaris", "Fortuner", "Hilux", "Land Cruiser", "Hiace", "Rush", "Camry", "Prado", "Passo"));
        BRAND_MODELS.put("Honda", List.of("City", "Civic", "BR-V", "HR-V", "CR-V", "Vezel", "Accord", "Freed", "N-Box", "Insight"));
        BRAND_MODELS.put("Hyundai", List.of("Elantra", "Tucson", "Sonata", "Santa Fe", "Ioniq 5", "Porter H-100", "Staria", "Creta", "Genesis", "i20"));
        BRAND_MODELS.put("KIA", List.of("Sportage", "Picanto", "Sorento", "Stonic", "Cerato", "Carnival", "EV6", "Niro", "Rio", "Optima"));
        BRAND_MODELS.put("Changan", List.of("Alsvin", "Karvaan", "M9", "Oshan X7", "CS35 Plus", "Eado", "Hunter", "M8"));
        BRAND_MODELS.put("MG", List.of("HS", "ZS", "Gloster", "MG3", "MG5", "MG ZS EV", "MG6", "MG RX5"));
        BRAND_MODELS.put("Nissan", List.of("Sunny", "Patrol", "X-Trail", "Note", "Juke", "Sentra", "Maxima", "Leaf", "Dayz", "Caravan"));
        BRAND_MODELS.put("Mitsubishi", List.of("Lancer", "Pajero", "Outlander", "Mirage", "Eclipse Cross", "Triton", "ASX", "Xpander"));
        BRAND_MODELS.put("Daihatsu", List.of("Hijet", "Mira", "Move", "Cuore", "Terios", "Tanto", "Coure", "Charade"));
        BRAND_MODELS.put("FAW", List.of("V2", "X-PV", "Sirius", "Carrier", "Bestune", "Besturn", "X40"));
        BRAND_MODELS.put("Proton", List.of("Saga", "X70", "X50", "Persona", "Iriz", "Exora"));
        BRAND_MODELS.put("Haval", List.of("H6", "Jolion", "Dargo", "H9", "F7", "H2"));
        BRAND_MODELS.put("DFSK", List.of("Glory 580", "Glory 500", "Glory 330", "Mini Truck", "K01"));
        BRAND_MODELS.put("Mazda", List.of("Mazda 3", "Mazda 6", "CX-5", "CX-3", "MX-5", "BT-50"));
        BRAND_MODELS.put("Subaru", List.of("Forester", "Outback", "Impreza", "Legacy", "WRX", "XV"));
    }

    private static final List<String[]> BODY_TYPES = List.of(
            new String[]{"Sedan", "M5 4 11h14l2 5v3H3v-3l2-5z M7 11V8a4 4 0 0 1 4-4h2a4 4 0 0 1 4 4v3 M6 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4z M18 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"},
            new String[]{"Hatchback", "M3 12h14l2 4v4H3v-4l2-4 6-6h6l4 6"},
            new String[]{"SUV", "M3 12h17l1 5v3H3v-3l1-5 5-5h6l4 5"},
            new String[]{"Coupe", "M3 14h16l1 4v2H3v-2l2-4 5-5h6l4 5"},
            new String[]{"Convertible", "M3 14h16l1 4v2H3v-2l2-4 5-3h6"},
            new String[]{"Pickup", "M2 14h11V8h7l2 4v4H2v-2"},
            new String[]{"Minivan", "M3 13h17l1 4v3H3v-3l1-4 4-6h10l3 6"},
            new String[]{"Sport Car", "M3 14h17l1 3v3H3v-3l3-3 4-4h7"}
    );

    private HtmlRenderer() {
    }

    /** Body types available for selection / filtering, exposed for forms. */
    public static List<String> bodyTypeNames() {
        List<String> names = new ArrayList<>();
        for (String[] row : BODY_TYPES) names.add(row[0]);
        return names;
    }

    /** Pakistani brand names exposed for the seller make dropdown. */
    public static List<String> brandNames() {
        return new ArrayList<>(BRAND_LOGOS.keySet());
    }

    public static String authSelectionPage(String actionName, String actionPath) {
        boolean isLogin = "login".equalsIgnoreCase(actionPath);
        String adminButton = isLogin
                ? "<a class=\"button admin-btn\" href=\"/admin/login\">Admin</a>"
                : "";
        return layout(actionName, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">Select Account Type for %s</h2>
                    <div class="stack-form" style="margin-top:20px;">
                        <a class="button" href="/buyer/%s">Buyer</a>
                        <a class="button" href="/seller/%s">Seller</a>
                        %s
                    </div>
                </section>
                """.formatted(escape(actionName), actionPath, actionPath, adminButton));
    }

    public static String homePage(List<Car> cars, Map<String, String> filters, User viewer, Set<Integer> favoriteCarIds, String message) {
        StringBuilder cards = new StringBuilder();
        for (Car car : cars) {
            String logoUrl = BRAND_LOGOS.getOrDefault(canonicalBrand(car.getMake()), "");
            cards.append(COMPACT_VIEW.render(car, favoriteCarIds.contains(car.getId()), logoUrl));
        }
        if (cars.isEmpty()) {
            cards.append("<div class='panel' style='grid-column: 1 / -1; text-align:center;'>No cars match the current filters.</div>");
        }

        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        String topbar = renderTopbar(viewer, favoriteCarIds.size());

        String selectedMake = filters.getOrDefault("make", "");
        String selectedModelsCsv = filters.getOrDefault("model", "");
        Set<String> selectedModels = parseCsvSet(selectedModelsCsv);
        String selectedBody = filters.getOrDefault("body", "");
        String selectedCondition = filters.getOrDefault("condition", "");
        String selectedTransmission = filters.getOrDefault("transmission", "");
        String selectedType = filters.getOrDefault("type", "");
        String search = filters.getOrDefault("search", "");
        String minYearStr = filters.getOrDefault("minYear", "");
        String maxYearStr = filters.getOrDefault("maxYear", "");
        String minPrice = filters.getOrDefault("minPrice", "");
        String maxPrice = filters.getOrDefault("maxPrice", "");
        boolean showAllBrands = "1".equals(filters.getOrDefault("showAllBrands", ""));
        boolean showAllModels = "1".equals(filters.getOrDefault("showAllModels", ""));

        // Hero is shown only when the user is on the un-filtered home page
        boolean anyFilter = !search.isBlank() || !selectedMake.isBlank() || !selectedModelsCsv.isBlank()
                || !selectedBody.isBlank() || !selectedCondition.isBlank() || !selectedTransmission.isBlank()
                || !selectedType.isBlank() || !minPrice.isBlank() || !maxPrice.isBlank()
                || !minYearStr.isBlank() || !maxYearStr.isBlank()
                || !filters.getOrDefault("usage", "").isBlank();
        String heroSection = anyFilter ? "" : renderHero();

        String activeChips = anyFilter ? renderActiveFilterChips(filters) : "";
        String brandPillsRow = renderBrandPillsRow(filters, selectedMake);
        String quickFiltersRow = renderQuickFilters(filters);
        String brandGrid = renderBrandGrid(filters, selectedMake, showAllBrands);
        String modelGrid = renderModelGrid(filters, selectedMake, selectedModels, showAllModels);
        String bodyGrid = renderBodyGrid(filters, selectedBody);
        String conditionPills = renderConditionPills(filters, selectedCondition);
        String transmissionPills = renderTransmissionPills(filters, selectedTransmission);

        String hiddenFields = """
                <input type="hidden" name="type" value="%s"/>
                <input type="hidden" name="make" value="%s"/>
                <input type="hidden" name="body" value="%s"/>
                <input type="hidden" name="condition" value="%s"/>
                <input type="hidden" name="transmission" value="%s"/>
                """.formatted(
                escape(selectedType),
                escape(selectedMake),
                escape(selectedBody),
                escape(selectedCondition),
                escape(selectedTransmission)
        );

        String headingText = anyFilter ? "Cars for sale" : "Latest Listings";
        String resultsCount = cars.size() + " result" + (cars.size() == 1 ? "" : "s");

        return layout("AutoSphere — Buy Cars in Pakistan", """
                %s
                %s
                %s
                %s
                <div class="results-header-row">
                    <h2>%s <span class="count">%s</span></h2>
                    <a href="/" class="view-all">View all →</a>
                </div>
                %s
                %s
                <section class="home-layout">
                    <aside class="sidebar">
                        <h2>Filter</h2>
                        <form method="get" action="/" id="filterForm">
                            %s
                            <div class="sidebar-search">
                                <input type="text" name="search" placeholder="🔎 Keyword search…" value="%s"/>
                            </div>

                            <h3>Budget (Rs)</h3>
                            <div class="range-row">
                                <input type="number" name="minPrice" placeholder="Min" value="%s" step="50000"/>
                                <input type="number" name="maxPrice" placeholder="Max" value="%s" step="50000"/>
                            </div>

                            <h3>Model search</h3>
                            <div class="sidebar-search">
                                <input type="text" id="modelSearch" placeholder="Search models" oninput="filterModelList(this.value)"/>
                            </div>
                            <div class="model-grid" id="modelGrid">%s</div>
                            %s

                            <h3>Body type</h3>
                            <div class="body-grid">%s</div>

                            <h3>Year range</h3>
                            <div class="range-row">
                                <select name="minYear">%s</select>
                                <select name="maxYear">%s</select>
                            </div>

                            <h3>Transmission</h3>
                            <div class="pill-row">%s</div>

                            <h3>Condition</h3>
                            <div class="pill-row">%s</div>

                            <div class="sidebar-actions">
                                <button type="submit" class="button-primary">Apply Filters</button>
                                <a class="button-ghost" href="/">Reset filters</a>
                            </div>
                        </form>
                    </aside>
                    <section>
                        <div class="sort-bar">
                            <span class="results-text"><strong>%s</strong> %s found matching your search</span>
                            <select onchange="this.form && this.form.submit()">
                                <option>Sort: Newest First</option>
                                <option>Sort: Price ↓</option>
                                <option>Sort: Price ↑</option>
                                <option>Sort: Mileage ↑</option>
                            </select>
                        </div>
                        <section class="car-grid">%s</section>
                    </section>
                </section>
                %s
                <script>
                    function filterModelList(query) {
                        const q = (query || '').trim().toLowerCase();
                        document.querySelectorAll('#modelGrid .model-checkbox').forEach(function(el){
                            const txt = el.textContent.toLowerCase();
                            el.style.display = (!q || txt.indexOf(q) >= 0) ? 'flex' : 'none';
                        });
                    }
                </script>
                """.formatted(
                topbar,
                messageBox,
                heroSection,
                brandPillsRow,
                escape(headingText),
                resultsCount,
                activeChips,
                quickFiltersRow,
                hiddenFields,
                escape(search),
                escape(minPrice),
                escape(maxPrice),
                modelGrid,
                renderModelShowAllToggle(filters, selectedMake, showAllModels),
                bodyGrid,
                renderYearOptions(brandMinYear(selectedMake), MAX_YEAR, minYearStr, brandMinYear(selectedMake)),
                renderYearOptions(brandMinYear(selectedMake), MAX_YEAR, maxYearStr, MAX_YEAR),
                transmissionPills,
                conditionPills,
                cars.size(),
                cars.size() == 1 ? "car" : "cars",
                cards,
                renderWelcomeWizard(filters)
        ));
    }

    /** Top hero section with title, search bar, stats and the editor's-pick visual. */
    private static String renderHero() {
        return """
                <section class="hero">
                    <div class="hero-left">
                        <span class="hero-pill">★ Pakistan's #1 Car Marketplace</span>
                        <h1>Find Your <span class="accent">Perfect Drive.</span></h1>
                        <p class="hero-sub">Browse thousands of verified listings across Pakistan. From budget hatchbacks to luxury sedans — your next car is one search away.</p>
                        <form class="hero-search" method="get" action="/">
                            <span class="search-icon">🔎</span>
                            <input type="text" name="search" placeholder="Search by make, model, or city…"/>
                            <button type="submit">Search Cars</button>
                        </form>
                        <div class="hero-stats">
                            <div><strong>12,400+</strong><span>Active Listings</span></div>
                            <div><strong>8 Cities</strong><span>Across Pakistan</span></div>
                            <div><strong>98%</strong><span>Verified Sellers</span></div>
                        </div>
                    </div>
                    <div class="hero-right">
                        <span class="hero-tag-editor">★ Editor's Pick</span>
                        <span class="hero-glow"></span>
                        <div class="hero-car">🚗</div>
                        <div class="hero-tag-price">
                            <small>Starting from</small>
                            <strong>Rs 3,000,000</strong>
                        </div>
                    </div>
                </section>
                """;
    }

    /** Horizontal "Browse by Make" pill row visible directly under the hero. */
    private static String renderBrandPillsRow(Map<String, String> filters, String selectedMake) {
        String[] brands = { "Suzuki", "Toyota", "Honda", "Hyundai", "KIA", "Changan", "MG", "Nissan" };
        StringBuilder sb = new StringBuilder("<div class='brand-row'><span class='brand-row-label'>Browse by make</span>");
        for (String brand : brands) {
            boolean active = brand.equalsIgnoreCase(selectedMake);
            String url = active
                    ? buildUrl(filters, "make", "", "model", "")
                    : buildUrl(filters, "make", brand, "model", "", "showAllModels", "");
            sb.append("<a class='brand-pill ").append(active ? "active" : "").append("' href='")
                    .append(escape(url)).append("'>").append(escape(brand)).append("</a>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    /** Yellow quick-filter pills above the listings (All Cars / Under 5M / 2020+ / Sedan / SUV / etc.) */
    private static String renderQuickFilters(Map<String, String> filters) {
        StringBuilder sb = new StringBuilder("<div class='quick-filters'>");
        // "All Cars" — no filters
        boolean anyActive = !filters.getOrDefault("body", "").isBlank()
                || !filters.getOrDefault("maxPrice", "").isBlank()
                || !filters.getOrDefault("minYear", "").isBlank();
        sb.append("<a class='qf-pill ").append(anyActive ? "" : "active").append("' href='/'>All Cars</a>");

        // Under Rs 5M
        boolean under5m = "5000000".equals(filters.getOrDefault("maxPrice", ""));
        sb.append("<a class='qf-pill ").append(under5m ? "active" : "")
                .append("' href='").append(escape(buildUrl(filters, "maxPrice", under5m ? "" : "5000000")))
                .append("'>Under Rs 5M</a>");

        // 2020+
        boolean year2020 = "2020".equals(filters.getOrDefault("minYear", ""));
        sb.append("<a class='qf-pill ").append(year2020 ? "active" : "")
                .append("' href='").append(escape(buildUrl(filters, "minYear", year2020 ? "" : "2020")))
                .append("'>2020+</a>");

        // Body shortcuts
        for (String body : new String[]{"Sedan", "Hatchback", "SUV", "Sport Car"}) {
            boolean active = body.equalsIgnoreCase(filters.getOrDefault("body", ""));
            sb.append("<a class='qf-pill ").append(active ? "active" : "")
                    .append("' href='").append(escape(buildUrl(filters, "body", active ? "" : body)))
                    .append("'>").append(escape(body)).append("</a>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Welcome wizard popup that helps first-time visitors narrow down to a segment,
     * usage type and budget. Only auto-opens once per session (sessionStorage flag),
     * and is suppressed when the user already has filters applied.
     */
    private static String renderWelcomeWizard(Map<String, String> filters) {
        // If the user already has any filter applied, they don't need the helper.
        boolean hasFilters = !filters.getOrDefault("body", "").isBlank()
                || !filters.getOrDefault("minPrice", "").isBlank()
                || !filters.getOrDefault("maxPrice", "").isBlank()
                || !filters.getOrDefault("usage", "").isBlank()
                || !filters.getOrDefault("make", "").isBlank()
                || !filters.getOrDefault("model", "").isBlank()
                || !filters.getOrDefault("search", "").isBlank();

        return """
                <div id="wizardOverlay" class="wizard-overlay" aria-hidden="true" role="dialog">
                    <div class="wizard-modal" role="document">
                        <button type="button" class="wizard-close" aria-label="Close" onclick="closeWizard()">×</button>

                        <div class="wizard-step" data-step="0">
                            <div class="wizard-icon">🚗</div>
                            <h2>Do you want help choosing a car?</h2>
                            <p class="wizard-sub">Quick questions — we'll filter listings to suit you.</p>
                            <div class="wizard-actions">
                                <button type="button" class="wizard-btn wizard-btn-primary" onclick="goToStep(1)">Yes</button>
                                <button type="button" class="wizard-btn wizard-btn-secondary-fill" onclick="closeWizard()">No</button>
                            </div>
                            <div class="wizard-progress" aria-hidden="true">
                                <span class="dot active"></span>
                                <span class="dot"></span>
                                <span class="dot"></span>
                                <span class="dot"></span>
                            </div>
                        </div>

                        <div class="wizard-step" data-step="1">
                            <h2>Which car segment are you looking for?</h2>
                            <p class="wizard-sub">Choose SUVs, sedans, hatchbacks, or other segments.</p>
                            <div class="wizard-grid wizard-segment-grid" id="wizSegments">
                                <button type="button" data-value="SUV">🚙<span>SUV</span></button>
                                <button type="button" data-value="Sedan">🚗<span>Sedan</span></button>
                                <button type="button" data-value="Hatchback">🚘<span>Hatchback</span></button>
                                <button type="button" data-value="Coupe">🏎️<span>Coupe</span></button>
                                <button type="button" data-value="Pickup">🛻<span>Pickup</span></button>
                                <button type="button" data-value="Minivan">🚐<span>Minivan</span></button>
                                <button type="button" data-value="Convertible">🚖<span>Convertible</span></button>
                                <button type="button" data-value="Sport Car">🏁<span>Sport Car</span></button>
                            </div>
                            <div class="wizard-actions">
                                <button type="button" class="wizard-btn wizard-btn-ghost" onclick="goToStep(0)">Back</button>
                                <button type="button" class="wizard-btn wizard-btn-primary" id="seg-next" disabled onclick="goToStep(2)">Next</button>
                            </div>
                            <div class="wizard-progress"><span class="dot"></span><span class="dot active"></span><span class="dot"></span><span class="dot"></span></div>
                        </div>

                        <div class="wizard-step" data-step="2">
                            <h2>Are you looking for a family car or for personal use?</h2>
                            <p class="wizard-sub">Tap the option that best describes how you'll use it.</p>
                            <div class="wizard-grid wizard-usage-grid" id="wizUsage">
                                <button type="button" data-value="Family">
                                    <div class="usage-emoji">👨‍👩‍👧</div>
                                    <span>Family</span>
                                    <small>Spacious, safe, comfortable</small>
                                </button>
                                <button type="button" data-value="Personal">
                                    <div class="usage-emoji">🧑‍💼</div>
                                    <span>Personal</span>
                                    <small>Stylish, efficient, practical</small>
                                </button>
                            </div>
                            <div class="wizard-actions">
                                <button type="button" class="wizard-btn wizard-btn-ghost" onclick="goToStep(1)">Back</button>
                                <button type="button" class="wizard-btn wizard-btn-primary" id="use-next" disabled onclick="goToStep(3)">Next</button>
                            </div>
                            <div class="wizard-progress"><span class="dot"></span><span class="dot"></span><span class="dot active"></span><span class="dot"></span></div>
                        </div>

                        <div class="wizard-step" data-step="3">
                            <h2>What is your budget range?</h2>
                            <p class="wizard-sub">Pick a preset or enter custom values (in PKR).</p>
                            <div class="wizard-grid wizard-budget-grid" id="wizBudget">
                                <button type="button" data-min="0"        data-max="1500000">Under Rs 15 L</button>
                                <button type="button" data-min="1500000"  data-max="3000000">Rs 15 – 30 L</button>
                                <button type="button" data-min="3000000"  data-max="5000000">Rs 30 – 50 L</button>
                                <button type="button" data-min="5000000"  data-max="8000000">Rs 50 – 80 L</button>
                                <button type="button" data-min="8000000"  data-max="15000000">Rs 80 L – 1.5 Cr</button>
                                <button type="button" data-min="15000000" data-max="0">Above Rs 1.5 Cr</button>
                            </div>
                            <div class="wizard-budget-custom">
                                <label>Min Rs <input type="number" id="wizMin" min="0" step="50000" placeholder="0"/></label>
                                <label>Max Rs <input type="number" id="wizMax" min="0" step="50000" placeholder="any"/></label>
                            </div>
                            <div class="wizard-actions">
                                <button type="button" class="wizard-btn wizard-btn-ghost" onclick="goToStep(2)">Back</button>
                                <button type="button" class="wizard-btn wizard-btn-primary" onclick="finishWizard()">Find cars</button>
                            </div>
                            <div class="wizard-progress"><span class="dot"></span><span class="dot"></span><span class="dot"></span><span class="dot active"></span></div>
                        </div>
                    </div>
                </div>

                <script>
                    (function() {
                        var SHOW = !%s && sessionStorage.getItem('autosphere_wizard_seen') !== '1';
                        var overlay = document.getElementById('wizardOverlay');
                        if (!overlay) return;

                        var state = { segment: '', usage: '', minPrice: '', maxPrice: '' };

                        window.openWizard = function() {
                            overlay.classList.add('open');
                            overlay.setAttribute('aria-hidden', 'false');
                            document.body.classList.add('wizard-open');
                            goToStep(0);
                        };
                        window.closeWizard = function() {
                            overlay.classList.remove('open');
                            overlay.setAttribute('aria-hidden', 'true');
                            document.body.classList.remove('wizard-open');
                            try { sessionStorage.setItem('autosphere_wizard_seen', '1'); } catch (e) {}
                        };
                        window.goToStep = function(idx) {
                            overlay.querySelectorAll('.wizard-step').forEach(function(el) {
                                var on = String(idx) === el.getAttribute('data-step');
                                el.classList.toggle('active', on);
                                if (on) {
                                    el.style.animation = 'none';
                                    void el.offsetWidth;
                                    el.style.animation = '';
                                }
                            });
                        };
                        window.finishWizard = function() {
                            var min = document.getElementById('wizMin').value || state.minPrice || '';
                            var max = document.getElementById('wizMax').value || state.maxPrice || '';
                            var params = new URLSearchParams();
                            if (state.segment) params.set('body', state.segment);
                            if (state.usage)   params.set('usage', state.usage);
                            if (min)           params.set('minPrice', min);
                            if (max)           params.set('maxPrice', max);
                            try { sessionStorage.setItem('autosphere_wizard_seen', '1'); } catch (e) {}
                            var qs = params.toString();
                            window.location.href = qs ? ('/?' + qs) : '/';
                        };

                        function bindGrid(gridId, onPick) {
                            var grid = document.getElementById(gridId);
                            if (!grid) return;
                            grid.addEventListener('click', function(ev) {
                                var btn = ev.target.closest('button[data-value], button[data-min]');
                                if (!btn) return;
                                grid.querySelectorAll('button').forEach(function(b){ b.classList.remove('selected'); });
                                btn.classList.add('selected');
                                onPick(btn);
                            });
                        }

                        bindGrid('wizSegments', function(btn) {
                            state.segment = btn.getAttribute('data-value') || '';
                            document.getElementById('seg-next').disabled = !state.segment;
                        });
                        bindGrid('wizUsage', function(btn) {
                            state.usage = btn.getAttribute('data-value') || '';
                            document.getElementById('use-next').disabled = !state.usage;
                        });
                        bindGrid('wizBudget', function(btn) {
                            state.minPrice = btn.getAttribute('data-min') || '';
                            state.maxPrice = btn.getAttribute('data-max') || '';
                            if (state.maxPrice === '0') state.maxPrice = '';
                            document.getElementById('wizMin').value = state.minPrice;
                            document.getElementById('wizMax').value = state.maxPrice;
                        });

                        // Esc to close
                        document.addEventListener('keydown', function(ev) {
                            if (ev.key === 'Escape' && overlay.classList.contains('open')) closeWizard();
                        });
                        // Click outside the modal closes it
                        overlay.addEventListener('click', function(ev) {
                            if (ev.target === overlay) closeWizard();
                        });

                        if (SHOW) setTimeout(openWizard, 200);
                    })();
                </script>
                """.formatted(hasFilters ? "true" : "false");
    }

    /* ========== Helper renderers ========== */

    private static String renderTopbar(User viewer, int favCount) {
        String profileBlock;
        if (viewer == null) {
            profileBlock = """
                    <a href='/auth/login' class='btn-outline'>Sign In</a>
                    <a href='/seller/login' class='btn-primary'>List Your Car</a>
                    """;
        } else {
            String letter = viewer.getUsername().substring(0, 1).toUpperCase();
            String roleLabel = capitalize(viewer.getRole().toLowerCase());
            String email = viewer.getEmail() == null || viewer.getEmail().isBlank() ? "—" : viewer.getEmail();
            String phone = viewer.getPhone() == null || viewer.getPhone().isBlank() ? "—" : viewer.getPhone();
            String listCarHref = "SELLER".equalsIgnoreCase(viewer.getRole()) ? "/seller/" + viewer.getUsername() : "/seller/login";
            profileBlock = """
                    <a href='%s' class='btn-primary'>List Your Car</a>
                    <div class='profile-dropdown' tabindex='0'>
                        <a class='profile-chip' href='/profile'>
                            <span class='avatar'>%s</span>
                            <span>%s</span>
                        </a>
                        <div class='menu'>
                            <div class='row'><strong>Username</strong>%s</div>
                            <div class='row'><strong>Role</strong>%s</div>
                            <div class='row'><strong>Email</strong>%s</div>
                            <div class='row'><strong>Phone</strong>%s</div>
                            <a class='menu-item' href='/profile'>View Full Profile</a>
                            <a class='menu-item' href='/favorites'>My Favorites</a>
                            <a class='menu-item' href='%s' onclick="return confirm('Are you sure you want to log out?');">Sign Out</a>
                        </div>
                    </div>
                    """.formatted(
                    listCarHref,
                    escape(letter),
                    escape(viewer.getUsername()),
                    escape(viewer.getUsername()),
                    escape(roleLabel),
                    escape(email),
                    escape(phone),
                    "/" + viewer.getRole().toLowerCase() + "/logout"
            );
        }

        return """
                <header class="topbar">
                    <a href="/" class="brand">
                        <span class="brand-mark">A</span>
                        <span>AutoSphere</span>
                    </a>
                    <nav>
                        <a href="/" class="active">Home</a>
                        <a href="/?type=Car">Inventory</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare">Compare</a>
                        <a href="/favorites">Favourites%s</a>
                    </nav>
                    <div class="topbar-actions">
                        %s
                    </div>
                </header>
                """.formatted(
                favCount > 0 ? " (" + favCount + ")" : "",
                profileBlock
        );
    }

    private static String renderActiveFilterChips(Map<String, String> filters) {
        List<String[]> chips = new ArrayList<>();
        addChip(chips, filters, "search", "Search: " + filters.getOrDefault("search", ""));
        addChip(chips, filters, "make", filters.getOrDefault("make", ""));
        for (String model : parseCsvSet(filters.getOrDefault("model", ""))) {
            chips.add(new String[]{"model:" + model, "Model: " + model});
        }
        addChip(chips, filters, "body", "Body: " + filters.getOrDefault("body", ""));
        addChip(chips, filters, "usage", "Use: " + filters.getOrDefault("usage", ""));
        addChip(chips, filters, "condition", "Condition: " + filters.getOrDefault("condition", ""));
        addChip(chips, filters, "transmission", "Transmission: " + filters.getOrDefault("transmission", ""));
        if (!filters.getOrDefault("minYear", "").isBlank() || !filters.getOrDefault("maxYear", "").isBlank()) {
            String label = "Year: " + filters.getOrDefault("minYear", "any") + " - " + filters.getOrDefault("maxYear", "any");
            chips.add(new String[]{"year", label});
        }
        if (!filters.getOrDefault("minPrice", "").isBlank() || !filters.getOrDefault("maxPrice", "").isBlank()) {
            String label = "Price: " + filters.getOrDefault("minPrice", "any") + " - " + filters.getOrDefault("maxPrice", "any");
            chips.add(new String[]{"price", label});
        }
        addChip(chips, filters, "type", "Type: " + filters.getOrDefault("type", ""));

        StringBuilder sb = new StringBuilder("<div class='active-filters-bar'>");
        sb.append("<a href='/compare' class='compare-pill'>⚖ Compare <span class='badge'>0</span></a>");
        if (chips.isEmpty()) {
            sb.append("<span style='color:#64748b; font-size:13px; margin-left:6px;'>No filters applied — click a brand or pill in the sidebar to filter.</span>");
        } else {
            for (String[] chip : chips) {
                String removeUrl = buildRemoveUrl(filters, chip[0]);
                sb.append("<span class='filter-chip'>").append(escape(chip[1]))
                        .append("<a class='remove-x' href='").append(escape(removeUrl)).append("' aria-label='Remove'>×</a></span>");
            }
            sb.append("<a class='reset-all-link' href='/'>Reset all</a>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static void addChip(List<String[]> chips, Map<String, String> filters, String key, String label) {
        String value = filters.getOrDefault(key, "");
        if (value != null && !value.isBlank()) {
            chips.add(new String[]{key, label});
        }
    }

    private static String renderBrandGrid(Map<String, String> filters, String selectedMake, boolean showAll) {
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (Map.Entry<String, String> entry : BRAND_LOGOS.entrySet()) {
            String name = entry.getKey();
            if (!showAll && idx >= 8) {
                break;
            }
            boolean active = name.equalsIgnoreCase(selectedMake);
            String url = active ? buildUrl(filters, "make", "", "showAllModels", "") : buildUrl(filters, "make", name, "model", "", "showAllModels", "");
            String color = BRAND_COLORS.getOrDefault(name, "#475569");
            String letter = name.substring(0, 1);
            sb.append("<a class='brand-tile ").append(active ? "active" : "").append("' href='").append(escape(url)).append("' title='").append(escape(name)).append("'>")
                    .append("<img src='").append(escape(entry.getValue())).append("' alt='").append(escape(name)).append("' onerror=\"this.style.display='none'; this.nextElementSibling.style.display='flex';\"/>")
                    .append("<span class='brand-letter' style='display:none; background:").append(color).append(";'>").append(escape(letter)).append("</span>")
                    .append("<span class='brand-name'>").append(escape(name)).append("</span>")
                    .append("</a>");
            idx++;
        }
        return sb.toString();
    }

    private static String renderShowAllToggle(Map<String, String> filters, String paramKey, boolean currentlyShown, int totalCount) {
        if (totalCount <= 8) return "";
        String url = currentlyShown ? buildUrl(filters, paramKey, "") : buildUrl(filters, paramKey, "1");
        String label = currentlyShown ? "− Show less" : "+ Show all";
        return "<a class='show-all-link' href='" + escape(url) + "'>" + label + "</a>";
    }

    private static String renderModelGrid(Map<String, String> filters, String selectedMake, Set<String> selectedModels, boolean showAll) {
        if (selectedMake == null || selectedMake.isBlank()) {
            return "<span class='empty-hint'>Select a brand first to see its top models.</span>";
        }
        List<String> models = BRAND_MODELS.getOrDefault(canonicalBrand(selectedMake), List.of());
        if (models.isEmpty()) {
            return "<span class='empty-hint'>No models on file for this brand yet.</span>";
        }
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (String model : models) {
            if (!showAll && idx >= 8) break;
            boolean checked = selectedModels.contains(model);
            sb.append("<label class='model-checkbox'><input type='checkbox' name='model' value='")
                    .append(escape(model)).append("'")
                    .append(checked ? " checked" : "")
                    .append(" onchange=\"this.form.submit()\"/> ")
                    .append(escape(model)).append("</label>");
            idx++;
        }
        return sb.toString();
    }

    private static String renderModelShowAllToggle(Map<String, String> filters, String selectedMake, boolean showAll) {
        if (selectedMake == null || selectedMake.isBlank()) return "";
        List<String> models = BRAND_MODELS.getOrDefault(canonicalBrand(selectedMake), List.of());
        if (models.size() <= 8) return "";
        String url = showAll ? buildUrl(filters, "showAllModels", "") : buildUrl(filters, "showAllModels", "1");
        String label = showAll ? "− Show less" : "+ Show all";
        return "<a class='show-all-link' href='" + escape(url) + "'>" + label + "</a>";
    }

    private static String renderBodyGrid(Map<String, String> filters, String selectedBody) {
        StringBuilder sb = new StringBuilder();
        for (String[] body : BODY_TYPES) {
            String name = body[0];
            String pathData = body[1];
            boolean active = name.equalsIgnoreCase(selectedBody);
            String url = active ? buildUrl(filters, "body", "") : buildUrl(filters, "body", name);
            sb.append("<a class='body-tile ").append(active ? "active" : "").append("' href='").append(escape(url)).append("' title='").append(escape(name)).append("'>")
                    .append("<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'><path d='")
                    .append(pathData).append("'/></svg>")
                    .append("<span>").append(escape(name)).append("</span>")
                    .append("</a>");
        }
        return sb.toString();
    }

    private static String renderConditionPills(Map<String, String> filters, String selected) {
        String[] options = {"New", "Genuine", "Completely Showered", "Some parts showered"};
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            boolean active = option.equalsIgnoreCase(selected);
            String url = active ? buildUrl(filters, "condition", "") : buildUrl(filters, "condition", option);
            sb.append("<a class='pill-chip ").append(active ? "active" : "").append("' href='").append(escape(url)).append("'>").append(escape(option)).append("</a>");
        }
        return sb.toString();
    }

    private static String renderTransmissionPills(Map<String, String> filters, String selected) {
        String[] options = {"Automatic", "Manual"};
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            boolean active = option.equalsIgnoreCase(selected);
            String url = active ? buildUrl(filters, "transmission", "") : buildUrl(filters, "transmission", option);
            sb.append("<a class='pill-chip ").append(active ? "active" : "").append("' href='").append(escape(url)).append("'>").append(escape(option)).append("</a>");
        }
        return sb.toString();
    }

    private static String renderYearOptions(int from, int to, String currentValue, int defaultValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("<option value=''>Any</option>");
        int current = parseIntOr(currentValue, -1);
        for (int year = to; year >= from; year--) {
            String selected = (year == current) ? "selected" : "";
            sb.append("<option value='").append(year).append("' ").append(selected).append(">").append(year).append("</option>");
        }
        return sb.toString();
    }

    private static int brandMinYear(String make) {
        if (make == null || make.isBlank()) return 1900;
        Integer year = BRAND_FOUNDED.get(canonicalBrand(make));
        return year == null ? 1900 : year;
    }

    private static String canonicalBrand(String make) {
        if (make == null) return "";
        for (String key : BRAND_LOGOS.keySet()) {
            if (key.equalsIgnoreCase(make)) return key;
        }
        return make;
    }

    /* ===== URL helpers ===== */
    private static final List<String> URL_KEYS = List.of(
            "type", "search", "make", "model", "body", "condition", "transmission",
            "minYear", "maxYear", "minPrice", "maxPrice", "color", "city",
            "minMileage", "maxMileage", "status", "showAllBrands", "showAllModels",
            "usage"
    );

    private static String buildUrl(Map<String, String> filters, String... overrides) {
        Map<String, String> next = new LinkedHashMap<>();
        for (String key : URL_KEYS) {
            String existing = filters.getOrDefault(key, "");
            if (existing != null && !existing.isBlank()) {
                next.put(key, existing);
            }
        }
        for (int i = 0; i + 1 < overrides.length; i += 2) {
            String k = overrides[i];
            String v = overrides[i + 1];
            if (v == null || v.isEmpty()) {
                next.remove(k);
            } else {
                next.put(k, v);
            }
        }
        if (next.isEmpty()) return "/";
        StringBuilder sb = new StringBuilder("/?");
        boolean first = true;
        for (Map.Entry<String, String> e : next.entrySet()) {
            if (!first) sb.append("&");
            sb.append(urlEncode(e.getKey())).append("=").append(urlEncode(e.getValue()));
            first = false;
        }
        return sb.toString();
    }

    private static String buildRemoveUrl(Map<String, String> filters, String key) {
        if (key.startsWith("model:")) {
            String modelName = key.substring("model:".length());
            Set<String> models = parseCsvSet(filters.getOrDefault("model", ""));
            models.remove(modelName);
            String csv = String.join(",", models);
            return buildUrl(filters, "model", csv);
        }
        if ("year".equals(key)) {
            return buildUrl(filters, "minYear", "", "maxYear", "");
        }
        if ("price".equals(key)) {
            return buildUrl(filters, "minPrice", "", "maxPrice", "");
        }
        return buildUrl(filters, key, "");
    }

    private static Set<String> parseCsvSet(String csv) {
        Set<String> set = new HashSet<>();
        if (csv == null || csv.isBlank()) return set;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) set.add(t);
        }
        return set;
    }

    private static String urlEncode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }

    private static int parseIntOr(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String loginPage(String title, String action, String error, String msg) {
        String messageBox = "";
        if (error != null && !error.isBlank()) messageBox = "<div class='error'>" + escape(error) + "</div>";
        else if (msg != null && !msg.isBlank()) messageBox = "<div class='notice success'>" + escape(msg) + "</div>";

        return layout(title, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">%s</h2>
                    %s
                    <form method="post" action="%s" class="stack-form">
                        <label>Username <input name="username" required/></label>
                        <label>Password <input type="password" name="password" required/></label>
                        <button type="submit">Login</button>
                    </form>
                </section>
                """.formatted(escape(title), messageBox, action));
    }

    public static String signupPage(String title, String action, boolean sellerSignup, String error, String msg) {
        String messageBox = "";
        if (error != null && !error.isBlank()) messageBox = "<div class='error'>" + escape(error) + "</div>";
        else if (msg != null && !msg.isBlank()) messageBox = "<div class='notice success'>" + escape(msg) + "</div>";

        String sellerFields = sellerSignup
                ? """
                        <label>Email <input type="email" name="email" required
                            pattern="[^\\s@]+@[^\\s@]+\\.[^\\s@]+"
                            title="Enter a valid email address (e.g. name@example.com)"/></label>
                        <label>Phone (Pakistan) <input name="phone" required
                            pattern="^(\\+92|0)?3\\d{9}$"
                            placeholder="03XX-XXXXXXX or +923XXXXXXXXX"
                            title="Enter a valid Pakistani mobile number — e.g. 03001234567 or +923001234567"/></label>
                        """
                : "";
        return layout(title, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">%s</h2>
                    %s
                    <form method="post" action="%s" class="stack-form">
                        <label>Username <input name="username" required/></label>
                        <label>Password <input type="password" name="password" required/></label>
                        %s
                        <button type="submit">Create Account</button>
                    </form>
                </section>
                """.formatted(escape(title), messageBox, action, sellerFields));
    }

    public static String sellerDashboard(User seller, List<Car> cars, String message) {
        StringBuilder myCars = new StringBuilder();
        for (Car car : cars) {
            myCars.append("""
                    <div class="car-row">
                        <span>%s (%d)</span>
                        <span>%s - %s</span>
                        <span>Rs %,.0f</span>
                        <span>%s</span>
                        <div class="actions">
                            <form method="post" action="/seller/cars/%d/status">
                                <select name="status">
                                    <option value="AVAILABLE" %s>Available</option>
                                    <option value="SOLD" %s>Sold</option>
                                </select>
                                <button type="submit">Update</button>
                            </form>
                            <a class="button secondary" href="/seller/cars/%d/edit">Edit</a>
                            <form method="post" action="/seller/cars/%d/delete">
                                <button type="submit" class="danger">Delete</button>
                            </form>
                        </div>
                    </div>
                    """.formatted(
                    escape(car.getTitle()),
                    car.getYear(),
                    escape(car.getCity()),
                    escape(car.getColor()),
                    car.getPrice(),
                    escape(car.getStatus()),
                    car.getId(),
                    "AVAILABLE".equals(car.getStatus()) ? "selected" : "",
                    "SOLD".equals(car.getStatus()) ? "selected" : "",
                    car.getId(),
                    car.getId()
            ));
        }
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";

        StringBuilder makeOptions = new StringBuilder("<option value=''>Select Make</option>");
        for (String brand : brandNames()) {
            makeOptions.append("<option value='").append(escape(brand)).append("'>").append(escape(brand)).append("</option>");
        }

        StringBuilder bodyOptions = new StringBuilder("<option value=''>Select Body Type</option>");
        for (String body : bodyTypeNames()) {
            bodyOptions.append("<option value='").append(escape(body)).append("'>").append(escape(body)).append("</option>");
        }

        return layout("Seller Dashboard", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #ffffff; font-size: 1.2rem;">Seller Panel</h2>
                    </div>
                    <nav>
                        <a href="/seller/logout" onclick="return confirm('Are you sure you want to log out?');">Logout</a>
                    </nav>
                </header>
                <section class="panel">
                    <h2>Welcome, %s</h2>
                    %s
                    <h3>Add New Listing</h3>
                    <form class="grid-form" method="post" action="/seller/cars" enctype="multipart/form-data">
                        <select name="vehicleType" id="sellerVehicleType" required onchange="syncSellerVehicleBodyField()">
                            <option value="Car">Car</option>
                            <option value="Bike">Bike</option>
                        </select>
                        <input name="title" placeholder="Listing title" required/>
                        <select name="make" required>%s</select>
                        <input name="model" placeholder="Model" required/>
                        <div id="sellerBodyTypeRow" class="seller-body-type-row">
                        <select name="body" id="sellerBodySelect" required>%s</select>
                        </div>
                        <input type="hidden" id="sellerBodyBikeHidden" name="body" value="" disabled/>
                        <input name="color" placeholder="Color" required/>
                        <input name="city" placeholder="City" required/>
                        <input type="number" name="year" placeholder="Year" min="1900" max="%d" required
                            title="Year must be between 1900 and %d"/>
                        <input type="number" name="mileage" placeholder="Mileage (km)" min="0" required/>
                        <input type="number" name="price" placeholder="Price" step="0.1" min="0" required/>
                        <select name="transmission" required>
                            <option value="Manual">Manual</option>
                            <option value="Automatic">Automatic</option>
                        </select>
                        <select name="fuelType" required>
                            <option value="Petrol">Petrol</option>
                            <option value="Diesel">Diesel</option>
                            <option value="Electric">Electric</option>
                            <option value="Hybrid">Hybrid</option>
                        </select>
                        <select name="assembly" required>
                            <option value="Local">Local</option>
                            <option value="Imported">Imported</option>
                        </select>
                        <select name="paintCondition" id="paintCond" onchange="toggleShowered()" required>
                            <option value="Genuine">Genuine</option>
                            <option value="Completely Showered">Completely Showered</option>
                            <option value="Some parts showered">Some parts showered</option>
                        </select>
                        <input name="showeredParts" id="showeredParts" placeholder="Which parts are showered?" style="display:none;"/>
                        <input name="description" placeholder="Short description" required/>
                        <input type="file" name="images" accept="image/*" multiple required/>
                        <button type="submit" style="grid-column: 1 / -1;">Upload and Publish</button>
                    </form>
                    <script>
                        function toggleShowered() {
                            var cond = document.getElementById('paintCond').value;
                            document.getElementById('showeredParts').style.display = (cond === 'Some parts showered') ? 'block' : 'none';
                        }
                        function syncSellerVehicleBodyField() {
                            var vtEl = document.getElementById('sellerVehicleType');
                            if (!vtEl) return;
                            var bike = vtEl.value === 'Bike';
                            var row = document.getElementById('sellerBodyTypeRow');
                            var sel = document.getElementById('sellerBodySelect');
                            var hid = document.getElementById('sellerBodyBikeHidden');
                            if (!row || !sel || !hid) return;
                            if (bike) {
                                row.style.display = 'none';
                                sel.removeAttribute('required');
                                sel.disabled = true;
                                sel.removeAttribute('name');
                                hid.disabled = false;
                            } else {
                                row.style.display = '';
                                sel.setAttribute('required', 'required');
                                sel.disabled = false;
                                sel.setAttribute('name', 'body');
                                hid.disabled = true;
                            }
                        }
                        document.addEventListener('DOMContentLoaded', syncSellerVehicleBodyField);
                    </script>
                </section>
                <section class="panel">
                    <h3>My Listings (%d)</h3>
                    %s
                </section>
                """.formatted(
                    escape(seller.getUsername()),
                    messageBox,
                    makeOptions,
                    bodyOptions,
                    CURRENT_YEAR,
                    CURRENT_YEAR,
                    cars.size(),
                    myCars));
    }

    public static String adminDashboard(User admin, List<Car> cars, List<User> users, String message) {
        return adminDashboard(admin, cars, users, List.of(), message);
    }

    public static String adminDashboard(User admin, List<Car> cars, List<User> users, List<String> inquiries, String message) {
        StringBuilder carRows = new StringBuilder();
        for (Car car : cars) {
            carRows.append("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>Rs %,.0f</td>
                        <td>%s</td>
                        <td>
                            <form method="post" action="/admin/cars/%d/status">
                                <select name="status">
                                    <option value="AVAILABLE" %s>Available</option>
                                    <option value="SOLD" %s>Sold</option>
                                </select>
                                <button type="submit">Save</button>
                            </form>
                            <form method="post" action="/admin/cars/%d/delete">
                                <button class="danger" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                    """.formatted(
                    car.getId(),
                    escape(car.getTitle()),
                    escape(car.getMake()),
                    escape(car.getSellerName()),
                    car.getPrice(),
                    escape(car.getStatus()),
                    car.getId(),
                    "AVAILABLE".equals(car.getStatus()) ? "selected" : "",
                    "SOLD".equals(car.getStatus()) ? "selected" : "",
                    car.getId()
            ));
        }

        StringBuilder userRows = new StringBuilder();
        for (User user : users) {
            userRows.append("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>
                            %s
                        </td>
                    </tr>
                    """.formatted(
                    user.getId(),
                    escape(user.getUsername()),
                    escape(user.getRole()),
                    "ADMIN".equals(user.getRole())
                            ? "<span class='muted'>Protected</span>"
                            : "<form method='post' action='/admin/users/" + user.getId() + "/delete'><button class='danger' type='submit'>Delete</button></form>"
            ));
        }
        StringBuilder inquiryRows = new StringBuilder();
        for (String inquiry : inquiries) {
            inquiryRows.append("<li>").append(escape(inquiry)).append("</li>");
        }
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        return layout("Admin Dashboard", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #ffffff; font-size: 1.2rem;">Admin Panel</h2>
                    </div>
                    <nav>
                        <a href="/admin/logout" onclick="return confirm('Are you sure you want to log out?');">Logout</a>
                    </nav>
                </header>
                <section class="panel">
                    <h2>Welcome, %s</h2>
                    %s
                    <h3>All Listings</h3>
                    <table>
                        <thead><tr><th>ID</th><th>Title</th><th>Make</th><th>Seller</th><th>Price</th><th>Status</th><th>Action</th></tr></thead>
                        <tbody>%s</tbody>
                    </table>
                </section>
                <section class="panel">
                    <h3>All Users</h3>
                    <table>
                        <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Action</th></tr></thead>
                        <tbody>%s</tbody>
                    </table>
                </section>
                <section class="panel">
                    <h3>Buyer Inquiries</h3>
                    <ul>%s</ul>
                </section>
                """.formatted(escape(admin.getUsername()), messageBox, carRows, userRows, inquiryRows));
    }

    public static String favoritesPage(User buyer, List<Car> favorites, Set<Integer> favoriteIds, String message) {
        StringBuilder cards = new StringBuilder();
        for (Car car : favorites) {
            String logoUrl = BRAND_LOGOS.getOrDefault(canonicalBrand(car.getMake()), "");
            cards.append(COMPACT_VIEW.render(car, true, logoUrl));
        }
        if (favorites.isEmpty()) {
            cards.append("<div class='panel' style='grid-column: 1 / -1; text-align:center; color:#64748b;'>No favorites yet. Hover over a car listing and click the heart icon to save it here.</div>");
        }

        return layout("My Favorites", """
                %s
                %s
                <div class="results-header">
                    <h1>My Favorites</h1>
                    <span class="results-count">(%d saved)</span>
                </div>
                <section class="car-grid">%s</section>
                """.formatted(
                renderTopbar(buyer, favoriteIds.size()),
                message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>",
                favorites.size(),
                cards
        ));
    }

    public static String profilePage(User viewer, int favoritesCount) {
        if (viewer == null) {
            return layout("Profile", """
                    %s
                    <section class="panel center-panel narrow">
                        <h2 style='text-align:center;'>You're browsing as a guest</h2>
                        <p class='muted' style='text-align:center;'>Sign in to view and edit your full profile and unlock more features.</p>
                        <div class='stack-form' style='margin-top:18px;'>
                            <a class='button' href='/auth/login'>Sign In</a>
                            <a class='button secondary' href='/auth/signup'>Create Account</a>
                        </div>
                    </section>
                    """.formatted(renderTopbar(null, favoritesCount)));
        }

        String letter = viewer.getUsername().substring(0, 1).toUpperCase();
        String email = (viewer.getEmail() == null || viewer.getEmail().isBlank()) ? "—" : viewer.getEmail();
        String phone = (viewer.getPhone() == null || viewer.getPhone().isBlank()) ? "—" : viewer.getPhone();

        return layout("Profile", """
                %s
                <div class="profile-page-card">
                    <div class="header-row">
                        <div class="big-avatar">%s</div>
                        <div>
                            <h2 style="margin:0;">%s</h2>
                            <p class="muted" style="margin:4px 0 0 0;">%s account · ID #%d</p>
                        </div>
                    </div>
                    <div class="info-row"><span class="label">Username</span><span>%s</span></div>
                    <div class="info-row"><span class="label">Role</span><span>%s</span></div>
                    <div class="info-row"><span class="label">Email</span><span>%s</span></div>
                    <div class="info-row"><span class="label">Phone</span><span>%s</span></div>
                    <div class="info-row"><span class="label">Saved Favorites</span><span>%d</span></div>
                    <div class="sidebar-actions" style="margin-top:22px;">
                        <a class="button-primary" href="/favorites">View My Favorites</a>
                        <a class="button-ghost" href="%s" onclick="return confirm('Are you sure you want to log out?');">Sign Out</a>
                    </div>
                </div>
                """.formatted(
                renderTopbar(viewer, favoritesCount),
                escape(letter),
                escape(viewer.getUsername()),
                escape(capitalize(viewer.getRole().toLowerCase())),
                viewer.getId(),
                escape(viewer.getUsername()),
                escape(capitalize(viewer.getRole().toLowerCase())),
                escape(email),
                escape(phone),
                favoritesCount,
                "/" + viewer.getRole().toLowerCase() + "/logout"
        ));
    }

    public static String carDetailsPage(CarDetails details, User viewer, boolean isFavorite, String message) {
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        String fairEstimateHtml = renderFairEstimate(details.getCar());

        // Build the gallery list. Some legacy listings only populate cars.image_path
        // and have no rows in car_images, in which case the previous code rendered
        // nothing at all. Prefer the gallery, then fall back to the main image, then
        // to a placeholder.
        List<String> gallery = new ArrayList<>();
        if (details.getImagePaths() != null) {
            for (String p : details.getImagePaths()) {
                if (p != null && !p.isBlank() && !gallery.contains(p)) gallery.add(p);
            }
        }
        String mainPath = details.getCar().getImagePath();
        if (mainPath != null && !mainPath.isBlank() && !gallery.contains(mainPath)) {
            gallery.add(0, mainPath);
        }
        if (gallery.isEmpty()) {
            gallery.add(NO_IMAGE_PLACEHOLDER);
        }

        StringBuilder images = new StringBuilder();
        images.append("<div class='main-image-container'>");
        images.append("<img id='main-car-pic' src='").append(escape(gallery.get(0)))
                .append("' alt='car image' onclick='openModal(this.src)'")
                .append(" onerror=\"this.onerror=null;this.src='").append(NO_IMAGE_PLACEHOLDER).append("';\"/>");
        images.append("</div>");
        if (gallery.size() > 1) {
            images.append("<div class='image-slider'>");
            for (String img : gallery) {
                images.append("<img src='").append(escape(img)).append("' alt='thumbnail'")
                        .append(" onclick='document.getElementById(\"main-car-pic\").src=this.src'")
                        .append(" onerror=\"this.onerror=null;this.src='").append(NO_IMAGE_PLACEHOLDER).append("';\"/>");
            }
            images.append("</div>");
        }

        String detailFavBtn = """
                <form method="post" action="/favorites/%d/toggle" style="display:inline-block; margin:0;">
                    <button type="submit" class="detail-fav-btn %s" aria-label="Toggle favorite">
                        <svg viewBox='0 0 24 24' fill='%s' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'>
                            <path d='M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z'/>
                        </svg>
                        <span>%s</span>
                    </button>
                </form>
                """.formatted(
                details.getCar().getId(),
                isFavorite ? "active" : "",
                isFavorite ? "currentColor" : "none",
                isFavorite ? "Saved to Favorites" : "Add to Favorites"
        );

        String buyerActions = "";
        if (viewer != null && "BUYER".equals(viewer.getRole())) {
            buyerActions = """
                    <div style="margin-top:20px;">
                    <form method="post" action="/buyer/inquiries" class="stack-form">
                        <input type="hidden" name="carId" value="%d"/>
                        <input name="phone" placeholder="Your phone (optional)"
                            pattern="^(\\+92|0)?3\\d{9}$"
                            title="Optional: enter a valid Pakistani mobile (e.g. 03001234567)"/>
                        <input name="message" placeholder="Message to seller" required/>
                        <button type="submit">Send Message</button>
                    </form>
                    </div>
                    """.formatted(details.getCar().getId());
        }
        String sellerContact = """
                <div class="seller-contact">
                    <h3>Seller Contact</h3>
                    <p>Email: %s</p>
                    <p>Phone: %s</p>
                </div>
                """.formatted(escape(details.getSeller().getEmail()), escape(details.getSeller().getPhone()));
                
        String extraDetails = "";
        if (details.getCar().getVehicleType() != null && !details.getCar().getVehicleType().isBlank()) {
            extraDetails = """
                    <div class="spec-grid">
                        <div class="spec-item"><span>Vehicle Type</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Transmission</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Fuel Type</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Assembly</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Paint Condition</span><strong>%s</strong></div>
                        %s
                    </div>
                    """.formatted(
                        escape(details.getCar().getVehicleType()),
                        escape(details.getCar().getTransmission()),
                        escape(details.getCar().getFuelType()),
                        escape(details.getCar().getAssembly()),
                        escape(details.getCar().getPaintCondition()),
                        (details.getCar().getShoweredParts() != null && !details.getCar().getShoweredParts().isBlank()) ? "<div class=\"spec-item\" style=\"grid-column: 1 / -1\"><span>Showered Parts</span><strong>" + escape(details.getCar().getShoweredParts()) + "</strong></div>" : ""
                    );
        }
                
        return layout("Car Details", """
                <header class="topbar">
                    <a href="/" class="brand">
                        <span class="brand-mark">A</span>
                        <span>AutoSphere</span>
                    </a>
                    <nav>
                        <a href="/">Home</a>
                        <a href="/?type=Car">Inventory</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare">Compare</a>
                        <a href="/favorites">Favourites</a>
                    </nav>
                    <div class="topbar-actions">
                        <a href="/" class="btn-outline">← Back to Listings</a>
                    </div>
                </header>
                %s
                <section class="details-layout">
                    <div class="gallery-section">
                        %s
                    </div>
                    <div class="info-section panel">
                        <div class="detail-title-row">
                            <h2>%s</h2>
                            %s
                        </div>
                        <div class="price-row">
                            <p class="price">Rs %,.0f</p>
                            %s
                        </div>
                        <div class="spec-grid">
                            <div class="spec-item"><span>Make</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Model</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Body</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Color</span><strong>%s</strong></div>
                            <div class="spec-item"><span>City</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Year</span><strong>%d</strong></div>
                            <div class="spec-item"><span>Mileage</span><strong>%,d km</strong></div>
                            <div class="spec-item"><span>Status</span><strong>%s</strong></div>
                        </div>
                        %s
                        <div class="description-box">
                            <h3>Description</h3>
                            <p>%s</p>
                        </div>
                        %s%s
                    </div>
                </section>
                
                <!-- The Modal -->
                <div id="imageModal" class="modal" onclick="this.style.display='none'">
                  <span class="close">&times;</span>
                  <img class="modal-content" id="img01">
                </div>
                <script>
                function openModal(src) {
                    document.getElementById('imageModal').style.display = 'block';
                    document.getElementById('img01').src = src;
                }
                </script>
                """.formatted(
                messageBox,
                images,
                escape(details.getCar().getTitle()),
                detailFavBtn,
                details.getCar().getPrice(),
                fairEstimateHtml,
                escape(details.getCar().getMake()),
                escape(details.getCar().getModel()),
                escape(blankToDash(details.getCar().getBody())),
                escape(details.getCar().getColor()),
                escape(details.getCar().getCity()),
                details.getCar().getYear(),
                details.getCar().getMileage(),
                escape(details.getCar().getStatus()),
                extraDetails,
                escape(details.getCar().getDescription()),
                sellerContact,
                buyerActions
        ));
    }

    private static String blankToDash(String value) {
        if (value == null || value.isBlank()) return "—";
        return value;
    }

    /**
     * Render the small fair-price estimate badge that appears next to the
     * seller's asking price on the car details page only.
     */
    private static String renderFairEstimate(Car car) {
        if (car == null) return "";
        FairPriceCalculator.Estimate est = FairPriceCalculator.estimate(car);
        if (est == null) return "";

        if (!est.known) {
            String reason = est.warnings.isEmpty() ? "" : est.warnings.get(0);
            return """
                    <span class="fair-estimate fair-unknown" title="%s">
                        <small>Fair price estimate unavailable</small>
                    </span>
                    """.formatted(escape(reason));
        }

        String deltaClass;
        if (est.fallback || est.indicativeOnly) deltaClass = "delta-fallback";
        else if (est.overpricedPct > 8)  deltaClass = "delta-over";
        else if (est.overpricedPct < -8) deltaClass = "delta-under";
        else                              deltaClass = "delta-fair";

        String fairLabel = est.indicativeOnly ? "Indicative estimate" : "Estimated fair price";

        StringBuilder tooltip = new StringBuilder();
        tooltip.append(est.generationLabel).append("\n");
        tooltip.append(String.format(java.util.Locale.US,
                "Fair value: Rs %s – %s (mid Rs %s)\n",
                formatPkrShort(est.fairLowPkr),
                formatPkrShort(est.fairHighPkr),
                formatPkrShort(est.fairPricePkr)));
        if (!est.reasons.isEmpty()) {
            tooltip.append("\nBreakdown:\n");
            for (String r : est.reasons) tooltip.append("• ").append(r).append("\n");
        }
        if (!est.warnings.isEmpty()) {
            tooltip.append("\nWarnings:\n");
            for (String w : est.warnings) tooltip.append("• ").append(w).append("\n");
        }

        // In fallback / indicative mode show a single conservative figure ("≈ Rs 47 Lacs"),
        // since the detailed range is known to be unreliable for this listing.
        String rangeHtml;
        if (est.fallback || est.indicativeOnly) {
            rangeHtml = "Rs ≈ " + formatPkrShort(est.fairPricePkr);
        } else {
            rangeHtml = "Rs " + formatPkrShort(est.fairLowPkr)
                    + " – Rs " + formatPkrShort(est.fairHighPkr);
        }

        return """
                <span class="fair-estimate %s" title="%s">
                    <small class="fair-label">%s</small>
                    <small class="fair-range">%s</small>
                    <small class="fair-delta">%s</small>
                </span>
                """.formatted(
                deltaClass,
                escape(tooltip.toString().trim()),
                escape(fairLabel),
                escape(rangeHtml),
                escape(est.verdict())
        );
    }

    /** Format a PKR amount as a short, human-friendly string (e.g. 47.5 Lacs, 1.2 Cr). */
    private static String formatPkrShort(double pkr) {
        if (pkr <= 0) return "—";
        if (pkr >= 10_000_000.0) {
            double cr = pkr / 10_000_000.0;
            return String.format(java.util.Locale.US, "%.2f Cr", cr);
        }
        if (pkr >= 100_000.0) {
            double l = pkr / 100_000.0;
            return String.format(java.util.Locale.US, "%.1f Lacs", l);
        }
        return String.format(java.util.Locale.US, "%,d", (long) pkr);
    }

    public static String sellerEditPage(Car car, String message) {
        String messageBox = message == null || message.isBlank() ? "" : "<div class='error'>" + escape(message) + "</div>";

        StringBuilder makeOptions = new StringBuilder();
        boolean foundMake = false;
        for (String brand : brandNames()) {
            boolean sel = brand.equalsIgnoreCase(car.getMake());
            if (sel) foundMake = true;
            makeOptions.append("<option value='").append(escape(brand)).append("'")
                    .append(sel ? " selected" : "")
                    .append(">").append(escape(brand)).append("</option>");
        }
        if (!foundMake && car.getMake() != null && !car.getMake().isBlank()) {
            makeOptions.insert(0, "<option value='" + escape(car.getMake()) + "' selected>" + escape(car.getMake()) + "</option>");
        }

        StringBuilder bodyOptions = new StringBuilder("<option value=''>Select Body Type</option>");
        for (String body : bodyTypeNames()) {
            boolean sel = body.equalsIgnoreCase(car.getBody());
            bodyOptions.append("<option value='").append(escape(body)).append("'")
                    .append(sel ? " selected" : "")
                    .append(">").append(escape(body)).append("</option>");
        }

        return layout("Edit Listing", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #ffffff; font-size: 1.2rem;">Seller Panel</h2>
                    </div>
                    <nav><a href="/seller/dashboard">Back</a></nav>
                </header>
                <section class="panel">
                    <h2>Edit Listing</h2>
                    %s
                    <form method="post" action="/seller/cars/%d/edit" class="grid-form" enctype="multipart/form-data">
                        <select name="vehicleType" id="sellerVehicleType" required onchange="syncSellerVehicleBodyField()">
                            <option value="Car" %s>Car</option>
                            <option value="Bike" %s>Bike</option>
                        </select>
                        <input name="title" value="%s" required/>
                        <select name="make" required>%s</select>
                        <input name="model" value="%s" required/>
                        <div id="sellerBodyTypeRow" class="seller-body-type-row">
                        <select name="body" id="sellerBodySelect" required>%s</select>
                        </div>
                        <input type="hidden" id="sellerBodyBikeHidden" name="body" value="" disabled/>
                        <input name="color" value="%s" required/>
                        <input name="city" value="%s" required/>
                        <input type="number" name="year" value="%d" min="1900" max="%d" required
                            title="Year must be between 1900 and %d"/>
                        <input type="number" name="mileage" value="%d" required/>
                        <input type="number" step="0.1" name="price" value="%.0f" required/>
                        <select name="transmission" required>
                            <option value="Manual" %s>Manual</option>
                            <option value="Automatic" %s>Automatic</option>
                        </select>
                        <select name="fuelType" required>
                            <option value="Petrol" %s>Petrol</option>
                            <option value="Diesel" %s>Diesel</option>
                            <option value="Electric" %s>Electric</option>
                            <option value="Hybrid" %s>Hybrid</option>
                        </select>
                        <select name="assembly" required>
                            <option value="Local" %s>Local</option>
                            <option value="Imported" %s>Imported</option>
                        </select>
                        <select name="paintCondition" id="paintCond" onchange="toggleShowered()" required>
                            <option value="Genuine" %s>Genuine</option>
                            <option value="Completely Showered" %s>Completely Showered</option>
                            <option value="Some parts showered" %s>Some parts showered</option>
                        </select>
                        <input name="showeredParts" id="showeredParts" value="%s" placeholder="Which parts are showered?" style="display:%s;"/>
                        <input name="description" value="%s" required/>
                        <input type="file" name="images" accept="image/*" multiple/>
                        <button type="submit" style="grid-column: 1 / -1;">Save Changes</button>
                    </form>
                    <script>
                        function toggleShowered() {
                            var cond = document.getElementById('paintCond').value;
                            document.getElementById('showeredParts').style.display = (cond === 'Some parts showered') ? 'block' : 'none';
                        }
                        function syncSellerVehicleBodyField() {
                            var vtEl = document.getElementById('sellerVehicleType');
                            if (!vtEl) return;
                            var bike = vtEl.value === 'Bike';
                            var row = document.getElementById('sellerBodyTypeRow');
                            var sel = document.getElementById('sellerBodySelect');
                            var hid = document.getElementById('sellerBodyBikeHidden');
                            if (!row || !sel || !hid) return;
                            if (bike) {
                                row.style.display = 'none';
                                sel.removeAttribute('required');
                                sel.disabled = true;
                                sel.removeAttribute('name');
                                hid.disabled = false;
                            } else {
                                row.style.display = '';
                                sel.setAttribute('required', 'required');
                                sel.disabled = false;
                                sel.setAttribute('name', 'body');
                                hid.disabled = true;
                            }
                        }
                        document.addEventListener('DOMContentLoaded', syncSellerVehicleBodyField);
                    </script>
                </section>
                """.formatted(
                messageBox,
                car.getId(),
                "Car".equals(car.getVehicleType()) ? "selected" : "",
                "Bike".equals(car.getVehicleType()) ? "selected" : "",
                escape(car.getTitle()),
                makeOptions,
                escape(car.getModel()),
                bodyOptions,
                escape(car.getColor()),
                escape(car.getCity()),
                car.getYear(),
                CURRENT_YEAR,
                CURRENT_YEAR,
                car.getMileage(),
                car.getPrice(),
                "Manual".equals(car.getTransmission()) ? "selected" : "",
                "Automatic".equals(car.getTransmission()) ? "selected" : "",
                "Petrol".equals(car.getFuelType()) ? "selected" : "",
                "Diesel".equals(car.getFuelType()) ? "selected" : "",
                "Electric".equals(car.getFuelType()) ? "selected" : "",
                "Hybrid".equals(car.getFuelType()) ? "selected" : "",
                "Local".equals(car.getAssembly()) ? "selected" : "",
                "Imported".equals(car.getAssembly()) ? "selected" : "",
                "Genuine".equals(car.getPaintCondition()) ? "selected" : "",
                "Completely Showered".equals(car.getPaintCondition()) ? "selected" : "",
                "Some parts showered".equals(car.getPaintCondition()) ? "selected" : "",
                escape(car.getShoweredParts()),
                "Some parts showered".equals(car.getPaintCondition()) ? "block" : "none",
                escape(car.getDescription())
        ));
    }

    private static String layout(String title, String body) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>%s</title>
                    <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                    <main class="container">%s</main>
                </body>
                </html>
                """.formatted(escape(title), body);
    }

    private static String getImageSrc(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return "https://via.placeholder.com/1200x800?text=No+Image";
        }
        return imagePath;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String compareSelectionPage(List<CarSpec> allSpecs, User viewer) {
        StringBuilder specOptions = new StringBuilder();
        for (CarSpec spec : allSpecs) {
            specOptions.append("""
                <div class="spec-checkbox-card">
                    <input type="checkbox" name="ids" id="spec_%d" value="%d" onchange="limitCheckboxes()">
                    <label for="spec_%d">%s</label>
                </div>
            """.formatted(spec.getId(), spec.getId(), spec.getId(), escape(spec.getName())));
        }

        String authLinks = viewer == null
                ? "<a href='/auth/login'>Sign In</a><a href='/auth/signup'>Sign Up</a>"
                : "<a href='/" + escape(viewer.getRole().toLowerCase()) + "/logout' onclick=\"return confirm('Are you sure you want to log out?');\">Sign Out</a>";

        return layout("Compare Cars", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav>
                        <a href="/?type=Car">Used Cars</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare" style="text-decoration:underline;">Compare Cars</a>
                        %s
                    </nav>
                </header>
                <section class="panel">
                    <h2>Select up to 3 cars to compare</h2>
                    <form method="get" action="/compare" class="compare-selection-form" onsubmit="return validateSelection()">
                        <div class="spec-selection-grid">
                            %s
                        </div>
                        <button type="submit" id="compare-btn" disabled style="margin-top:20px;">Compare Selected (0)</button>
                    </form>
                    <script>
                        function limitCheckboxes() {
                            const checkedBoxes = document.querySelectorAll('input[name="ids"]:checked');
                            const btn = document.getElementById('compare-btn');
                            btn.innerText = 'Compare Selected (' + checkedBoxes.length + ')';
                            
                            if (checkedBoxes.length > 0 && checkedBoxes.length <= 3) {
                                btn.disabled = false;
                            } else {
                                btn.disabled = true;
                            }

                            if (checkedBoxes.length >= 3) {
                                document.querySelectorAll('input[name="ids"]:not(:checked)').forEach(box => box.disabled = true);
                            } else {
                                document.querySelectorAll('input[name="ids"]').forEach(box => box.disabled = false);
                            }
                        }
                        
                        function validateSelection() {
                            const checkedBoxes = document.querySelectorAll('input[name="ids"]:checked');
                            if (checkedBoxes.length < 2) {
                                alert("Please select at least 2 cars to compare.");
                                return false;
                            }
                            return true;
                        }
                    </script>
                </section>
                """.formatted(authLinks, specOptions.toString()));
    }

    public static String compareResultPage(List<CarSpec> specs, User viewer) {
        String authLinks = viewer == null
                ? "<a href='/auth/login'>Sign In</a><a href='/auth/signup'>Sign Up</a>"
                : "<a href='/" + escape(viewer.getRole().toLowerCase()) + "/logout' onclick=\"return confirm('Are you sure you want to log out?');\">Sign Out</a>";

        StringBuilder tableHeader = new StringBuilder();
        StringBuilder engineRow = new StringBuilder();
        StringBuilder fuelTypeRow = new StringBuilder();
        StringBuilder maxPowerRow = new StringBuilder();
        StringBuilder maxTorqueRow = new StringBuilder();
        StringBuilder transRow = new StringBuilder();
        StringBuilder driveRow = new StringBuilder();
        StringBuilder bodyRow = new StringBuilder();
        StringBuilder seatsRow = new StringBuilder();
        StringBuilder tankRow = new StringBuilder();
        StringBuilder econRow = new StringBuilder();
        StringBuilder dimRow = new StringBuilder();
        StringBuilder wheelRow = new StringBuilder();
        StringBuilder clearanceRow = new StringBuilder();
        StringBuilder weightRow = new StringBuilder();
        StringBuilder safetyRow = new StringBuilder();
        StringBuilder priceRow = new StringBuilder();

        for (CarSpec spec : specs) {
            tableHeader.append("<th>").append(escape(spec.getName())).append("</th>");
            engineRow.append("<td>").append(escape(spec.getEngine())).append("</td>");
            fuelTypeRow.append("<td>").append(escape(spec.getFuelType())).append("</td>");
            maxPowerRow.append("<td>").append(escape(spec.getMaxPower())).append("</td>");
            maxTorqueRow.append("<td>").append(escape(spec.getMaxTorque())).append("</td>");
            transRow.append("<td>").append(escape(spec.getTransmission())).append("</td>");
            driveRow.append("<td>").append(escape(spec.getDriveType())).append("</td>");
            bodyRow.append("<td>").append(escape(spec.getBodyType())).append("</td>");
            seatsRow.append("<td>").append(spec.getSeats()).append("</td>");
            tankRow.append("<td>").append(escape(spec.getFuelTank())).append("</td>");
            econRow.append("<td>").append(escape(spec.getFuelEconomy())).append("</td>");
            dimRow.append("<td>").append(escape(spec.getDimensions())).append("</td>");
            wheelRow.append("<td>").append(escape(spec.getWheelbase())).append("</td>");
            clearanceRow.append("<td>").append(escape(spec.getGroundClearance())).append("</td>");
            weightRow.append("<td>").append(escape(spec.getKerbWeight())).append("</td>");
            safetyRow.append("<td>").append(escape(spec.getSafety())).append("</td>");
            priceRow.append("<td class='price-cell'>").append(escape(spec.getPriceRange())).append("</td>");
        }

        return layout("Compare Specifications", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav>
                        <a href="/?type=Car">Used Cars</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare" style="text-decoration:underline;">Compare Cars</a>
                        %s
                    </nav>
                </header>
                <section class="panel">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px;">
                        <h2>Compare Specifications</h2>
                        <a class="button secondary" href="/compare">Back to Selection</a>
                    </div>
                    <div class="compare-table-wrapper">
                        <table class="compare-table">
                            <thead>
                                <tr>
                                    <th class="feature-col">Features</th>
                                    %s
                                </tr>
                            </thead>
                            <tbody>
                                <tr class="group-header"><td colspan="%d">Dimensions & Capacity</td></tr>
                                <tr><td class="feature-col">Body Type</td>%s</tr>
                                <tr><td class="feature-col">Dimensions (L x W)</td>%s</tr>
                                <tr><td class="feature-col">Wheelbase</td>%s</tr>
                                <tr><td class="feature-col">Ground Clearance</td>%s</tr>
                                <tr><td class="feature-col">Kerb Weight</td>%s</tr>
                                <tr><td class="feature-col">Seating Capacity</td>%s</tr>
                                <tr><td class="feature-col">Fuel Tank</td>%s</tr>
                                
                                <tr class="group-header"><td colspan="%d">Engine & Performance</td></tr>
                                <tr><td class="feature-col">Engine</td>%s</tr>
                                <tr><td class="feature-col">Fuel Type</td>%s</tr>
                                <tr><td class="feature-col">Fuel Economy</td>%s</tr>
                                <tr><td class="feature-col">Max Power</td>%s</tr>
                                <tr><td class="feature-col">Max Torque</td>%s</tr>
                                <tr><td class="feature-col">Transmission</td>%s</tr>
                                <tr><td class="feature-col">Drive Type</td>%s</tr>
                                
                                <tr class="group-header"><td colspan="%d">Safety & Price</td></tr>
                                <tr><td class="feature-col">Safety Features</td>%s</tr>
                                <tr><td class="feature-col">Price Range</td>%s</tr>
                            </tbody>
                        </table>
                    </div>
                </section>
                """.formatted(
                        authLinks, 
                        tableHeader.toString(),
                        specs.size() + 1,
                        bodyRow.toString(), dimRow.toString(), wheelRow.toString(), clearanceRow.toString(), weightRow.toString(), seatsRow.toString(), tankRow.toString(),
                        specs.size() + 1,
                        engineRow.toString(), fuelTypeRow.toString(), econRow.toString(), maxPowerRow.toString(), maxTorqueRow.toString(), transRow.toString(), driveRow.toString(),
                        specs.size() + 1,
                        safetyRow.toString(), priceRow.toString()
                ));
    }
}
