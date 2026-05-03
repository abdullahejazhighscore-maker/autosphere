package com.samtech.carapp;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pakistan used-car fair price valuation engine.
 *
 * <p>Implements the formula:
 * <pre>
 *   Fair Price = Base Price
 *              x (1 - Age Depreciation)
 *              x (1 - Mileage Penalty)
 *              x (1 - Condition Penalty)
 *              x Brand Retention Factor
 *              x City / Fuel adjustments
 * </pre>
 *
 * <p>Data is calibrated to PakWheels, OLX, FameWheels, Gari.pk April 2026 prices.
 * The estimate is shown only on a single car ad page (not on the dashboard / list).
 */
public final class FairPriceCalculator {

    /** Vehicle category buckets for the age depreciation curve. */
    public enum Category {
        BUDGET_HATCH,
        MODERN_HATCH,
        COMPACT_SEDAN,
        MID_SEDAN,
        SUV,
        CHINESE,
        IMPORTED_JDM,
        BIKE
    }

    /** A single generation row in the catalogue. */
    public static final class Generation {
        public final String make;
        public final String modelKey;
        public final String label;
        public final int yearStart;
        public final int yearEnd;
        public final double basePriceMidPkr;
        public final double basePriceTopPkr;
        public final Category category;
        public final double brandRetentionFactor;
        public final double maxDepreciationCap;

        Generation(String make, String modelKey, String label,
                   int yearStart, int yearEnd,
                   double basePriceMidLacs, double basePriceTopLacs,
                   Category category,
                   double brandRetentionFactor, double maxDepreciationCap) {
            this.make = make;
            this.modelKey = modelKey;
            this.label = label;
            this.yearStart = yearStart;
            this.yearEnd = yearEnd;
            this.basePriceMidPkr = basePriceMidLacs * 100_000.0;
            this.basePriceTopPkr = basePriceTopLacs * 100_000.0;
            this.category = category;
            this.brandRetentionFactor = brandRetentionFactor;
            this.maxDepreciationCap = maxDepreciationCap;
        }
    }

    /** Result of a valuation run. */
    public static final class Estimate {
        public final boolean known;
        public final String generationLabel;
        public final double basePricePkr;
        public final double fairPricePkr;
        public final double fairLowPkr;
        public final double fairHighPkr;
        public final double askingPricePkr;
        public final double overpricedPct;
        public final List<String> reasons;
        public final List<String> warnings;
        /**
         * True when the calculated fair price differed from the seller's asking price
         * by more than {@link #FALLBACK_THRESHOLD_PCT}. In that case the displayed
         * fair / low / high values are overridden to a conservative band a few lacs
         * below the asking price instead of showing a wildly different number.
         */
        public final boolean fallback;

        Estimate(boolean known, String generationLabel,
                 double basePricePkr, double fairPricePkr,
                 double fairLowPkr, double fairHighPkr,
                 double askingPricePkr, double overpricedPct,
                 boolean fallback,
                 List<String> reasons, List<String> warnings) {
            this.known = known;
            this.generationLabel = generationLabel;
            this.basePricePkr = basePricePkr;
            this.fairPricePkr = fairPricePkr;
            this.fairLowPkr = fairLowPkr;
            this.fairHighPkr = fairHighPkr;
            this.askingPricePkr = askingPricePkr;
            this.overpricedPct = overpricedPct;
            this.fallback = fallback;
            this.reasons = reasons;
            this.warnings = warnings;
        }

        /** Verdict shown next to the asking price ("13% above fair price"). */
        public String verdict() {
            if (!known) return "estimate unavailable";
            if (fallback) {
                double diffLacs = (askingPricePkr - fairPricePkr) / 100_000.0;
                if (diffLacs >= 0.5) {
                    return String.format(Locale.US, "≈ Rs %d Lacs below seller's price",
                            Math.round(diffLacs));
                }
                return "close to seller's asking price";
            }
            double pct = overpricedPct;
            if (pct > 8)  return String.format(Locale.US, "%.0f%% above fair price", pct);
            if (pct < -8) return String.format(Locale.US, "%.0f%% below fair price", Math.abs(pct));
            return "close to fair price";
        }
    }

    /**
     * Threshold above which we consider our detailed calculation unreliable for this
     * specific listing (incomplete catalogue, mis-detected variant, unusual condition,
     * etc.) and fall back to a conservative "≈ 3 Lacs below asking" display.
     */
    public static final double FALLBACK_THRESHOLD_PCT = 50.0;
    /** Conservative gap (PKR) below the seller's asking price used in fallback mode. */
    public static final double FALLBACK_GAP_PKR = 300_000.0;

    /* =========== Generation catalogue (2025/26 PKR Lacs, Pakistan market) =========== */

    private static final List<Generation> GENERATIONS = new ArrayList<>();

    static {
        // Honda Civic (5B in datasheet)
        add("Honda", "civic",   "Honda Civic ES (7th Gen)",          2001, 2006,   3.5,   6.0, Category.MID_SEDAN, 0.92, 0.55);
        add("Honda", "civic",   "Honda Civic Reborn (8th Gen)",      2006, 2012,  14.0,  22.0, Category.MID_SEDAN, 0.92, 0.55);
        add("Honda", "civic",   "Honda Civic Rebirth (9th Gen)",     2012, 2016,  24.0,  33.0, Category.MID_SEDAN, 0.92, 0.55);
        add("Honda", "civic",   "Honda Civic Turbo (10th Gen)",      2016, 2021,  48.0,  72.0, Category.MID_SEDAN, 0.92, 0.55);
        add("Honda", "civic",   "Honda Civic 11th Gen",              2022, 2030,  75.0, 110.0, Category.MID_SEDAN, 0.92, 0.55);

        // Toyota Corolla (5B)
        add("Toyota", "corolla", "Toyota Corolla 7th Gen (E100)",    1993, 2002,   2.5,   6.0, Category.MID_SEDAN, 0.90, 0.50);
        add("Toyota", "corolla", "Toyota Corolla 9th Gen (E120)",    2002, 2008,   5.0,  10.0, Category.MID_SEDAN, 0.90, 0.50);
        add("Toyota", "corolla", "Toyota Corolla 10th Gen (E140)",   2008, 2014,   9.0,  22.0, Category.MID_SEDAN, 0.90, 0.50);
        add("Toyota", "corolla", "Toyota Corolla 11th Gen (E170)",   2014, 2021,  22.0,  45.0, Category.MID_SEDAN, 0.90, 0.50);
        add("Toyota", "corolla", "Toyota Corolla 12th Gen (E210)",   2021, 2030,  52.0,  85.0, Category.MID_SEDAN, 0.90, 0.50);

        // Honda City (5D)
        add("Honda", "city",    "Honda City 3rd Gen",                1998, 2003,   3.0,   6.0, Category.COMPACT_SEDAN, 0.92, 0.52);
        add("Honda", "city",    "Honda City i-DSI / ZX (4th Gen)",   2003, 2009,   6.0,  13.0, Category.COMPACT_SEDAN, 0.92, 0.52);
        add("Honda", "city",    "Honda City 5th Gen",                2009, 2014,  10.0,  20.0, Category.COMPACT_SEDAN, 0.92, 0.52);
        add("Honda", "city",    "Honda City Aspire (6th Gen)",       2014, 2021,  18.0,  35.0, Category.COMPACT_SEDAN, 0.92, 0.52);
        add("Honda", "city",    "Honda City RS (7th Gen)",           2021, 2030,  38.0,  58.0, Category.COMPACT_SEDAN, 0.92, 0.52);

        // Suzuki (5C)
        add("Suzuki", "mehran", "Suzuki Mehran 1st Gen (Carb)",      1989, 2012,   3.0,   7.0, Category.BUDGET_HATCH, 0.93, 0.30);
        add("Suzuki", "mehran", "Suzuki Mehran Euro-II (EFI)",       2012, 2019,   6.0,  12.0, Category.BUDGET_HATCH, 0.93, 0.30);
        add("Suzuki", "alto",   "Suzuki Alto 1st Gen (800cc)",       2000, 2012,   2.5,   5.0, Category.BUDGET_HATCH, 0.93, 0.45);
        add("Suzuki", "alto",   "Suzuki Alto 8th Gen (HA36S 660cc)", 2019, 2030,  22.0,  30.0, Category.MODERN_HATCH, 0.93, 0.45);
        add("Suzuki", "cultus", "Suzuki Cultus Gen1",                2000, 2012,   5.0,  13.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "cultus", "Suzuki Cultus Gen2",                2012, 2017,  10.0,  18.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "cultus", "Suzuki Cultus Gen3 / Celerio",      2017, 2030,  22.0,  32.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "wagon r","Suzuki Wagon R (Local)",            2014, 2030,  18.0,  28.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "wagonr", "Suzuki Wagon R (Local)",            2014, 2030,  18.0,  28.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "swift",  "Suzuki Swift Gen1 (Local)",         2010, 2021,  12.0,  24.0, Category.MODERN_HATCH, 0.94, 0.50);
        add("Suzuki", "swift",  "Suzuki Swift Gen2 (New shape)",     2022, 2030,  28.0,  38.0, Category.MODERN_HATCH, 0.94, 0.50);

        // KIA (5E)
        add("KIA",     "sportage","KIA Sportage 3rd Gen (AWD)",      2018, 2021,  60.0,  90.0, Category.SUV, 1.00, 0.60);
        add("KIA",     "sportage","KIA Sportage 4th Gen (FWD)",      2022, 2030,  85.0, 115.0, Category.SUV, 1.00, 0.60);
        add("KIA",     "picanto", "KIA Picanto",                     2019, 2030,  22.0,  32.0, Category.MODERN_HATCH, 1.00, 0.55);
        add("KIA",     "stonic",  "KIA Stonic",                      2022, 2030,  45.0,  65.0, Category.SUV, 1.00, 0.60);

        // Hyundai (5E)
        add("Hyundai", "tucson",  "Hyundai Tucson 3rd Gen",          2020, 2030,  75.0, 110.0, Category.SUV, 1.00, 0.62);
        add("Hyundai", "elantra", "Hyundai Elantra 6th Gen",         2021, 2030,  55.0,  80.0, Category.MID_SEDAN, 1.00, 0.55);
        add("Hyundai", "sonata",  "Hyundai Sonata (Imported)",       2014, 2030,  40.0,  90.0, Category.MID_SEDAN, 1.00, 0.55);

        // Toyota SUVs / Imports (5F)
        add("Toyota", "fortuner","Toyota Fortuner 2nd Gen",          2016, 2030, 120.0, 200.0, Category.SUV, 0.90, 0.45);
        add("Toyota", "hilux",   "Toyota Hilux Revo 2nd Gen",        2016, 2030, 120.0, 175.0, Category.SUV, 0.90, 0.45);
        add("Toyota", "revo",    "Toyota Hilux Revo 2nd Gen",        2016, 2030, 120.0, 175.0, Category.SUV, 0.90, 0.45);
        add("Toyota", "prius",   "Toyota Prius (Imported Hybrid)",   2009, 2020,  28.0,  65.0, Category.IMPORTED_JDM, 0.95, 0.58);
        add("Toyota", "vitz",    "Toyota Vitz (Imported)",           2010, 2020,  10.0,  22.0, Category.IMPORTED_JDM, 0.95, 0.55);
        add("Toyota", "passo",   "Toyota Passo (Imported)",          2010, 2020,   9.0,  18.0, Category.IMPORTED_JDM, 0.95, 0.55);
        add("Toyota", "yaris",   "Toyota Yaris",                     2020, 2030,  35.0,  55.0, Category.COMPACT_SEDAN, 0.90, 0.50);

        // Daihatsu
        add("Daihatsu", "mira",  "Daihatsu Mira (Imported JDM)",     2012, 2020,  12.0,  22.0, Category.IMPORTED_JDM, 0.95, 0.55);
        add("Daihatsu", "cuore", "Daihatsu Cuore (Local)",           2000, 2012,   4.0,  10.0, Category.BUDGET_HATCH, 0.95, 0.40);

        // Chinese brands - generic estimates
        add("Changan", "alsvin",  "Changan Alsvin",                  2020, 2030,  35.0,  50.0, Category.CHINESE, 1.25, 0.70);
        add("Changan", "oshan",   "Changan Oshan X7",                2020, 2030,  50.0,  70.0, Category.CHINESE, 1.25, 0.70);
        add("Changan", "karvaan", "Changan Karvaan",                 2018, 2030,  18.0,  25.0, Category.CHINESE, 1.25, 0.70);
        add("MG",      "hs",      "MG HS",                           2020, 2030,  60.0,  90.0, Category.CHINESE, 1.20, 0.65);
        add("MG",      "zs",      "MG ZS",                           2020, 2030,  45.0,  65.0, Category.CHINESE, 1.20, 0.65);
        add("Proton",  "saga",    "Proton Saga",                     2021, 2030,  30.0,  42.0, Category.CHINESE, 1.15, 0.65);
        add("Proton",  "x70",     "Proton X70",                      2020, 2030,  60.0,  95.0, Category.CHINESE, 1.15, 0.65);
        add("Haval",   "h6",      "Haval H6",                        2021, 2030,  85.0, 120.0, Category.CHINESE, 1.20, 0.65);
        add("Haval",   "jolion",  "Haval Jolion",                    2022, 2030,  75.0,  95.0, Category.CHINESE, 1.20, 0.65);
    }

    private static void add(String make, String modelKey, String label,
                            int yearStart, int yearEnd,
                            double basePriceMidLacs, double basePriceTopLacs,
                            Category category, double retention, double cap) {
        GENERATIONS.add(new Generation(make, modelKey, label, yearStart, yearEnd,
                basePriceMidLacs, basePriceTopLacs, category, retention, cap));
    }

    private FairPriceCalculator() {}

    /* =========================== Public API =========================== */

    /** Compute the estimate for the given car listing. Returns a non-null result. */
    public static Estimate estimate(Car car) {
        if (car == null) {
            return unknownEstimate(0, "No vehicle data");
        }
        if (car.getVehicleType() != null && "Bike".equalsIgnoreCase(car.getVehicleType())) {
            return estimateBike(car);
        }

        Generation gen = findGeneration(car);
        if (gen == null) {
            return unknownEstimate(car.getPrice(),
                    "Fair-price database does not yet include this make / model.");
        }

        int currentYear = Year.now().getValue();
        int age = Math.max(0, currentYear - car.getYear());

        // Catalogue values represent the LIVE used-market range across the whole
        // generation: low end ≈ oldest year, high end ≈ newest year. We interpolate
        // by year position rather than re-applying age depreciation on top.
        double yearSpan = Math.max(1, gen.yearEnd - gen.yearStart);
        double yearPos  = (car.getYear() - gen.yearStart) / yearSpan;
        if (yearPos < 0) yearPos = 0;
        if (yearPos > 1) yearPos = 1;

        VariantTier tier = detectVariant(car.getTitle(), car.getDescription());
        double variantPos;
        switch (tier) {
            case TOP: variantPos = 0.7; break;
            case MID: variantPos = 0.4; break;
            default:  variantPos = 0.0;
        }

        // Year is the dominant signal; variant nudges within the band.
        double curvePos = clamp01(yearPos * 0.7 + variantPos * 0.3);
        double basePrice = gen.basePriceMidPkr
                + (gen.basePriceTopPkr - gen.basePriceMidPkr) * curvePos;

        double mileagePen = mileagePenalty(car.getMileage());
        double paintAdj   = paintConditionAdj(car.getPaintCondition(), car.getShoweredParts());
        double cityAdj    = cityAdjustment(car.getCity());
        double fuelAdj    = fuelTypeAdjustment(car.getFuelType(), car.getTitle(), car.getDescription());
        double importAdj  = assemblyAdjustment(car.getAssembly());

        double netDep = mileagePen + paintAdj + importAdj - cityAdj - fuelAdj;
        netDep *= gen.brandRetentionFactor;

        // Short-term adjustments shouldn't move the price more than ~30% in either
        // direction (the year-position has already accounted for age).
        if (netDep > 0.30)  netDep = 0.30;
        if (netDep < -0.10) netDep = -0.10;

        double fair = basePrice * (1.0 - netDep);
        double low  = fair * 0.93;
        double high = fair * 1.07;

        double asking = car.getPrice();
        double pct = fair > 0 ? ((asking - fair) / fair) * 100.0 : 0.0;

        List<String> reasons = new ArrayList<>();
        reasons.add(String.format(Locale.US,
                "%s base for year %d: Rs %.1f Lacs (gen range %.0f–%.0f)",
                gen.label, car.getYear(), basePrice / 100_000.0,
                gen.basePriceMidPkr / 100_000.0, gen.basePriceTopPkr / 100_000.0));
        if (mileagePen > 0) {
            reasons.add(String.format(Locale.US, "Mileage %,d km: -%.0f%%",
                    car.getMileage(), mileagePen * 100));
        }
        if (paintAdj > 0) {
            reasons.add(String.format(Locale.US, "Paint / showered panels: -%.0f%%", paintAdj * 100));
        }
        if (cityAdj != 0) {
            reasons.add(String.format(Locale.US, "Registration city (%s): %s%.0f%%",
                    safe(car.getCity(), "—"), cityAdj > 0 ? "+" : "-", Math.abs(cityAdj) * 100));
        }
        if (fuelAdj != 0) {
            reasons.add(String.format(Locale.US, "Fuel type adjustment: %s%.0f%%",
                    fuelAdj > 0 ? "+" : "-", Math.abs(fuelAdj) * 100));
        }
        if (importAdj != 0) {
            reasons.add(String.format(Locale.US, "Assembly (%s): +%.0f%% penalty",
                    safe(car.getAssembly(), "—"), importAdj * 100));
        }
        reasons.add(String.format(Locale.US, "Brand retention factor: x%.2f", gen.brandRetentionFactor));
        if (tier == VariantTier.TOP) reasons.add("Variant detected: TOP / Full option");
        else if (tier == VariantTier.MID) reasons.add("Variant detected: MID");

        List<String> warnings = new ArrayList<>();
        if (age > 0 && car.getMileage() > 0 && (car.getMileage() / Math.max(1, age)) < 3000) {
            warnings.add("Suspiciously low mileage (avg "
                    + (car.getMileage() / Math.max(1, age)) + " km / yr) — possible odometer rollback.");
        }
        if (gen.make.equalsIgnoreCase("Honda") && gen.modelKey.equalsIgnoreCase("civic")
                && car.getYear() == 2012) {
            warnings.add("2012 Civic could be Reborn (8th gen) OR Rebirth (9th gen) — confirm shape.");
        }
        if (gen.make.equalsIgnoreCase("Toyota") && gen.modelKey.equalsIgnoreCase("corolla")
                && car.getYear() == 2014) {
            warnings.add("2014 Corolla could be E140 OR E170 — confirm shape code.");
        }

        // Safety net: if our detailed estimate is more than 50% off the seller's
        // asking price, fall back to a conservative "≈ 3 Lacs below asking" band.
        boolean fallback = asking > 0 && Math.abs(pct) > FALLBACK_THRESHOLD_PCT;
        if (fallback) {
            warnings.add(0, "Detailed valuation differed sharply from the asking price; "
                    + "showing a conservative estimate instead.");
            double safeFair = Math.max(50_000.0, asking - FALLBACK_GAP_PKR);
            fair = safeFair;
            low  = Math.max(50_000.0, safeFair - 50_000.0);
            high = safeFair + 50_000.0;
            pct  = ((asking - fair) / fair) * 100.0;
        }

        return new Estimate(true, gen.label, basePrice, fair, low, high,
                asking, pct, fallback, reasons, warnings);
    }

    private static double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    /* =========================== Internals =========================== */

    private static Estimate unknownEstimate(double asking, String reason) {
        List<String> warn = new ArrayList<>();
        warn.add(reason);
        return new Estimate(false, "Unknown model", 0, 0, 0, 0,
                asking, 0, false, new ArrayList<>(), warn);
    }

    private static Generation findGeneration(Car car) {
        if (car.getMake() == null || car.getModel() == null) return null;
        String makeKey  = car.getMake().trim().toLowerCase(Locale.ROOT);
        String modelKey = car.getModel().trim().toLowerCase(Locale.ROOT);
        int year = car.getYear();
        String title = (car.getTitle() == null ? "" : car.getTitle()).toLowerCase(Locale.ROOT);

        // Pass 1: shape-keyword disambiguation (most specific)
        String[] shapeHints = {
                "reborn", "rebirth", "turbo", "11th gen", "10th gen", "9th gen",
                "altis grande", "altis", "grande", "e210", "e170", "e140", "e120", "e100",
                "aspire", "i-dsi", "ids", "ha36s",
                "celerio", "gen3", "gen2", "gen1",
                "8th gen", "7th gen"
        };
        for (Generation g : GENERATIONS) {
            if (!g.make.equalsIgnoreCase(makeKey)) continue;
            if (!modelMatch(modelKey, g.modelKey)) continue;
            if (year < g.yearStart || year > g.yearEnd) continue;
            String label = g.label.toLowerCase(Locale.ROOT);
            for (String kw : shapeHints) {
                if (title.contains(kw) && label.contains(kw)) {
                    return g;
                }
            }
        }

        // Pass 2: year fits, prefer the narrowest (most specific) range
        Generation best = null;
        for (Generation g : GENERATIONS) {
            if (!g.make.equalsIgnoreCase(makeKey)) continue;
            if (!modelMatch(modelKey, g.modelKey)) continue;
            if (year < g.yearStart || year > g.yearEnd) continue;
            if (best == null || (g.yearEnd - g.yearStart) < (best.yearEnd - best.yearStart)) {
                best = g;
            }
        }
        return best;
    }

    private static boolean modelMatch(String userInput, String catalogueKey) {
        return userInput.contains(catalogueKey) || catalogueKey.contains(userInput);
    }

    private enum VariantTier { BASE, MID, TOP }

    private static VariantTier detectVariant(String title, String description) {
        String hay = ((title == null ? "" : title) + " " + (description == null ? "" : description))
                .toLowerCase(Locale.ROOT);
        if (hay.contains("grande") || hay.contains("oriel") || hay.contains(" rs ") ||
            hay.endsWith(" rs") || hay.contains("vxl") || hay.contains("turbo") ||
            hay.contains("full option") || hay.contains("top of the line")) {
            return VariantTier.TOP;
        }
        if (hay.contains("altis") || hay.contains("aspire") || hay.contains("vxr") ||
            hay.contains(" exi") || hay.contains(" vti")) {
            return VariantTier.MID;
        }
        return VariantTier.BASE;
    }

    /** Cumulative depreciation (0..1) for the given age and category. */
    private static double ageDepreciation(int age, Category cat) {
        double[] yearly;
        switch (cat) {
            case BUDGET_HATCH:
                yearly = new double[]{0.09, 0.08, 0.07, 0.06, 0.06, 0.045, 0.045, 0.035, 0.035, 0.035, 0.035, 0.025, 0.025};
                break;
            case MODERN_HATCH:
                yearly = new double[]{0.115, 0.10, 0.09, 0.07, 0.07, 0.055, 0.055, 0.045, 0.045, 0.045, 0.045, 0.035, 0.035};
                break;
            case COMPACT_SEDAN:
                yearly = new double[]{0.135, 0.11, 0.10, 0.08, 0.08, 0.06, 0.06, 0.045, 0.045, 0.045, 0.045, 0.035, 0.035};
                break;
            case MID_SEDAN:
                yearly = new double[]{0.115, 0.10, 0.09, 0.07, 0.07, 0.055, 0.055, 0.035, 0.035, 0.035, 0.035, 0.025, 0.025};
                break;
            case SUV:
                yearly = new double[]{0.145, 0.12, 0.11, 0.09, 0.09, 0.07, 0.07, 0.055, 0.055, 0.055, 0.055, 0.045, 0.045};
                break;
            case CHINESE:
                yearly = new double[]{0.20, 0.165, 0.14, 0.11, 0.11, 0.09, 0.09, 0.07, 0.07, 0.07, 0.07, 0.055, 0.055};
                break;
            case IMPORTED_JDM:
                yearly = new double[]{0.165, 0.135, 0.11, 0.09, 0.09, 0.07, 0.07, 0.055, 0.055, 0.055, 0.055, 0.04, 0.04};
                break;
            case BIKE:
                yearly = new double[]{0.10, 0.08, 0.07, 0.05, 0.05, 0.04, 0.04, 0.03, 0.03};
                break;
            default:
                yearly = new double[]{0.10, 0.08, 0.07};
        }
        double remaining = 1.0;
        int i = 0;
        for (; i < Math.min(age, yearly.length); i++) {
            remaining *= (1.0 - yearly[i]);
        }
        // Beyond the table, taper off.
        for (; i < age; i++) {
            remaining *= (1.0 - yearly[yearly.length - 1] * 0.7);
        }
        return 1.0 - remaining;
    }

    private static double mileagePenalty(int km) {
        if (km <= 60_000)  return 0;
        if (km <= 90_000)  return 0.04;
        if (km <= 120_000) return 0.075;
        if (km <= 150_000) return 0.115;
        if (km <= 200_000) return 0.16;
        if (km <= 250_000) return 0.215;
        if (km <= 300_000) return 0.275;
        return 0.345;
    }

    private static double paintConditionAdj(String paint, String showered) {
        if (paint == null) return 0;
        String p = paint.toLowerCase(Locale.ROOT);
        if (p.contains("genuine")) return 0;
        if (p.contains("completely showered")) return 0.18;
        if (p.contains("some parts")) {
            int parts = 1;
            if (showered != null && !showered.isBlank()) {
                parts = showered.split(",").length;
            }
            if (parts >= 4) return 0.15;
            if (parts >= 2) return 0.10;
            return 0.06;
        }
        return 0;
    }

    private static double cityAdjustment(String city) {
        if (city == null) return 0;
        String c = city.toLowerCase(Locale.ROOT);
        if (c.contains("islamabad") || c.contains("rawalpindi")) return 0.04;
        if (c.contains("lahore")) return 0;
        if (c.contains("karachi")) return -0.03;
        if (c.contains("peshawar") || c.contains("kpk") || c.contains("mardan")) return -0.04;
        // Smaller / interior cities
        return -0.02;
    }

    private static double fuelTypeAdjustment(String fuelType, String title, String description) {
        String hay = (safe(title, "") + " " + safe(description, "")).toLowerCase(Locale.ROOT);
        if (hay.contains("cng")) return -0.06;
        if (fuelType == null) return 0;
        String f = fuelType.toLowerCase(Locale.ROOT);
        if (f.contains("hybrid")) return 0.08;
        if (f.contains("diesel")) return 0.05;
        return 0;
    }

    private static double assemblyAdjustment(String assembly) {
        // Imported JDM cars carry a small risk premium for parts availability.
        if (assembly == null) return 0;
        if (assembly.equalsIgnoreCase("Imported")) return 0.02;
        return 0;
    }

    private static String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /* =========================== Bike valuation =========================== */

    private static Estimate estimateBike(Car car) {
        String model = safe(car.getModel(), "").toLowerCase(Locale.ROOT);
        String make  = safe(car.getMake(),  "").toLowerCase(Locale.ROOT);
        String label = safe(car.getMake(), "?") + " " + safe(car.getModel(), "?");
        boolean known = true;
        double newPricePkr;

        if (model.contains("cd 70") || model.contains("cd70") || model.contains("cd-70")) newPricePkr = 165_000;
        else if (model.contains("cg 125s") || model.contains("125s"))                     newPricePkr = 280_000;
        else if (model.contains("cg 125") || model.contains("cg125"))                     newPricePkr = 260_000;
        else if (model.contains("cb 150f") || model.contains("cb150f"))                   newPricePkr = 360_000;
        else if (model.contains("cb 150r") || model.contains("cb150r"))                   newPricePkr = 400_000;
        else if (model.contains("ybr 125g") || model.contains("ybrg"))                    newPricePkr = 310_000;
        else if (model.contains("ybr 125") || model.contains("ybr125"))                   newPricePkr = 290_000;
        else if (model.contains("gs 150") || model.contains("gs150"))                     newPricePkr = 300_000;
        else if (make.contains("road prince") || make.contains("united")
                || make.contains("ravi") || make.contains("super power"))                 newPricePkr =  90_000;
        else { newPricePkr = 200_000; known = false; }

        int age = Math.max(0, Year.now().getValue() - car.getYear());
        double dep = ageDepreciation(age, Category.BIKE);

        double brand;
        if (make.contains("honda"))  brand = 0.85;
        else if (make.contains("yamaha")) brand = 0.90;
        else if (make.contains("suzuki")) brand = 0.95;
        else brand = 1.30;

        double fair = newPricePkr * (1.0 - dep * brand);
        if (car.getMileage() > 60_000)      fair *= 0.75;
        else if (car.getMileage() > 30_000) fair *= 0.88;

        double asking = car.getPrice();
        double pct = fair > 0 ? ((asking - fair) / fair) * 100.0 : 0.0;
        double low = fair * 0.92;
        double high = fair * 1.08;

        List<String> reasons = new ArrayList<>();
        reasons.add(String.format(Locale.US, "Age %d yr depreciation", age));
        reasons.add(String.format(Locale.US, "Brand factor x%.2f", brand));
        if (car.getMileage() > 30_000) reasons.add("High-mileage penalty applied");

        List<String> warnings = new ArrayList<>();
        boolean fallback = known && asking > 0 && Math.abs(pct) > FALLBACK_THRESHOLD_PCT;
        if (fallback) {
            warnings.add("Detailed valuation differed sharply from the asking price; "
                    + "showing a conservative estimate instead.");
            double safeFair = Math.max(20_000.0, asking - FALLBACK_GAP_PKR);
            fair = safeFair;
            low  = Math.max(20_000.0, safeFair - 25_000.0);
            high = safeFair + 25_000.0;
            pct  = ((asking - fair) / fair) * 100.0;
        }

        return new Estimate(known, label.trim(), newPricePkr, fair,
                low, high, asking, pct, fallback,
                reasons, warnings);
    }
}
