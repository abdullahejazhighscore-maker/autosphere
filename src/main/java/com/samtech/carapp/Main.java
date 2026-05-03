package com.samtech.carapp;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class Main {
    private static final String ROLE_SELLER = "SELLER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_BUYER = "BUYER";

    private static final Pattern PK_PHONE = Pattern.compile("^(\\+92|0)?3\\d{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public static void main(String[] args) {
        Database.initialize();

        CarRepository carRepository = new CarRepository();
        UserRepository userRepository = new UserRepository();
        CarSpecRepository carSpecRepository = new CarSpecRepository();

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.staticFiles.add("uploads", Location.EXTERNAL);
        });

        app.get("/", ctx -> {
            Map<String, String> filters = new HashMap<>();
            filters.put("type", queryParamOrEmpty(ctx, "type"));
            filters.put("search", queryParamOrEmpty(ctx, "search"));
            filters.put("make", queryParamOrEmpty(ctx, "make"));
            filters.put("model", joinModelParams(ctx));
            filters.put("body", queryParamOrEmpty(ctx, "body"));
            filters.put("condition", queryParamOrEmpty(ctx, "condition"));
            filters.put("transmission", queryParamOrEmpty(ctx, "transmission"));
            filters.put("color", queryParamOrEmpty(ctx, "color"));
            filters.put("city", queryParamOrEmpty(ctx, "city"));
            filters.put("minMileage", queryParamOrEmpty(ctx, "minMileage"));
            filters.put("maxMileage", queryParamOrEmpty(ctx, "maxMileage"));
            filters.put("minYear", queryParamOrEmpty(ctx, "minYear"));
            filters.put("maxYear", queryParamOrEmpty(ctx, "maxYear"));
            filters.put("minPrice", queryParamOrEmpty(ctx, "minPrice"));
            filters.put("maxPrice", queryParamOrEmpty(ctx, "maxPrice"));
            filters.put("status", queryParamOrEmpty(ctx, "status"));
            filters.put("showAllBrands", queryParamOrEmpty(ctx, "showAllBrands"));
            filters.put("showAllModels", queryParamOrEmpty(ctx, "showAllModels"));
            filters.put("usage", queryParamOrEmpty(ctx, "usage"));

            List<Car> cars = carRepository.findAllWithFilters(filters);
            String message = queryParamOrEmpty(ctx, "msg");
            User viewer = getCurrentUser(ctx);
            Set<Integer> favoriteIds = collectFavoriteIds(ctx, viewer, carRepository);
            ctx.html(HtmlRenderer.homePage(cars, filters, viewer, favoriteIds, message));
        });

        app.get("/favorites", ctx -> {
            User viewer = getCurrentUser(ctx);
            List<Car> favorites;
            if (viewer != null && ROLE_BUYER.equals(viewer.getRole())) {
                favorites = carRepository.findFavoritesByUserId(viewer.getId());
            } else {
                Set<Integer> guestFavs = getGuestFavorites(ctx);
                favorites = guestFavs.isEmpty() ? new ArrayList<>() : carRepository.findByIds(new ArrayList<>(guestFavs));
            }
            Set<Integer> favoriteIds = new HashSet<>();
            for (Car c : favorites) favoriteIds.add(c.getId());
            ctx.html(HtmlRenderer.favoritesPage(viewer, favorites, favoriteIds, queryParamOrEmpty(ctx, "msg")));
        });

        app.post("/favorites/{id}/toggle", ctx -> {
            int carId;
            try { carId = Integer.parseInt(ctx.pathParam("id")); }
            catch (NumberFormatException e) { ctx.redirect("/"); return; }

            User viewer = getCurrentUser(ctx);
            if (viewer != null && ROLE_BUYER.equals(viewer.getRole())) {
                if (carRepository.isFavorite(viewer.getId(), carId)) {
                    carRepository.removeFavorite(viewer.getId(), carId);
                } else {
                    carRepository.addFavorite(viewer.getId(), carId);
                }
            } else {
                Set<Integer> favs = getGuestFavorites(ctx);
                if (favs.contains(carId)) favs.remove(carId);
                else favs.add(carId);
                ctx.sessionAttribute("guestFavorites", favs);
            }

            String returnTo = ctx.formParam("returnTo");
            if (returnTo == null || returnTo.isBlank()) returnTo = ctx.header("Referer");
            if (returnTo == null || returnTo.isBlank()) returnTo = "/";
            ctx.redirect(returnTo);
        });

        app.get("/profile", ctx -> {
            User viewer = getCurrentUser(ctx);
            if (viewer != null) {
                viewer = userRepository.findById(viewer.getId());
            }
            int favCount = collectFavoriteIds(ctx, viewer, carRepository).size();
            ctx.html(HtmlRenderer.profilePage(viewer, favCount));
        });

        app.get("/cars/{id}", ctx -> {
            int carId = Integer.parseInt(ctx.pathParam("id"));
            CarDetails details = carRepository.findDetailsById(carId);
            if (details == null) {
                ctx.redirect("/?msg=Car+not+found");
                return;
            }
            User viewer = getCurrentUser(ctx);
            boolean isFavorite = collectFavoriteIds(ctx, viewer, carRepository).contains(carId);
            ctx.html(HtmlRenderer.carDetailsPage(details, viewer, isFavorite, queryParamOrEmpty(ctx, "msg")));
        });

        app.get("/compare", ctx -> {
            List<String> idsParams = ctx.queryParams("ids");
            if (idsParams == null || idsParams.isEmpty()) {
                List<CarSpec> allSpecs = carSpecRepository.findAll();
                ctx.html(HtmlRenderer.compareSelectionPage(allSpecs, getCurrentUser(ctx)));
            } else {
                List<Integer> ids = new java.util.ArrayList<>();
                for (String idStr : idsParams) {
                    for (String part : idStr.split(",")) {
                        if (!part.isBlank()) {
                            try {
                                ids.add(Integer.parseInt(part.trim()));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                List<CarSpec> selectedSpecs = carSpecRepository.findByIds(ids);
                ctx.html(HtmlRenderer.compareResultPage(selectedSpecs, getCurrentUser(ctx)));
            }
        });

        app.get("/auth/login", ctx -> ctx.html(HtmlRenderer.authSelectionPage("Sign In", "login")));
        app.get("/auth/signup", ctx -> ctx.html(HtmlRenderer.authSelectionPage("Sign Up", "signup")));

        app.get("/buyer/login", ctx -> ctx.html(HtmlRenderer.loginPage(
                "Buyer Login",
                "/buyer/login",
                queryParamOrEmpty(ctx, "error"),
                queryParamOrEmpty(ctx, "msg")
        )));

        app.get("/buyer/signup", ctx -> ctx.html(HtmlRenderer.signupPage(
                "Buyer Sign Up",
                "/buyer/signup",
                false,
                queryParamOrEmpty(ctx, "error"),
                queryParamOrEmpty(ctx, "msg")
        )));

        app.post("/buyer/signup", ctx -> {
            String username = required(ctx.formParam("username"));
            String password = required(ctx.formParam("password"));
            if (userRepository.usernameExists(username)) {
                ctx.redirect("/buyer/signup?error=Username+already+taken");
                return;
            }
            userRepository.createBuyer(username, password);
            ctx.redirect("/buyer/login?msg=Account+successfully+created.+Please+login");
        });

        app.post("/buyer/login", ctx -> {
            User user = userRepository.authenticate(ctx.formParam("username"), ctx.formParam("password"), ROLE_BUYER);
            if (user == null) {
                ctx.redirect("/buyer/login?error=Invalid+buyer+credentials");
                return;
            }
            setSession(ctx, user);
            ctx.redirect("/?msg=Buyer+logged+in");
        });

        app.get("/buyer/logout", ctx -> {
            clearSession(ctx);
            ctx.redirect("/?msg=Buyer+logged+out");
        });

        app.get("/buyer/favorites", ctx -> {
            User buyer = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_BUYER);
            if (buyer == null) {
                ctx.redirect("/buyer/login?error=Please+login+first");
                return;
            }
            List<Car> favorites = carRepository.findFavoritesByUserId(buyer.getId());
            Set<Integer> favoriteIds = new HashSet<>();
            for (Car car : favorites) {
                favoriteIds.add(car.getId());
            }
            ctx.html(HtmlRenderer.favoritesPage(buyer, favorites, favoriteIds, queryParamOrEmpty(ctx, "msg")));
        });

        app.post("/buyer/favorites/{id}/add", ctx -> {
            User buyer = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_BUYER);
            if (buyer == null) {
                ctx.redirect("/buyer/login?error=Please+login+to+add+favorites");
                return;
            }
            carRepository.addFavorite(buyer.getId(), Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/?msg=Car+added+to+favorites");
        });

        app.post("/buyer/favorites/{id}/remove", ctx -> {
            User buyer = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_BUYER);
            if (buyer == null) {
                ctx.redirect("/buyer/login?error=Please+login+to+remove+favorites");
                return;
            }
            carRepository.removeFavorite(buyer.getId(), Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/?msg=Car+removed+from+favorites");
        });

        app.post("/buyer/inquiries", ctx -> {
            User buyer = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_BUYER);
            if (buyer == null) {
                ctx.redirect("/buyer/login?error=Please+login+to+contact+seller");
                return;
            }
            carRepository.createInquiry(
                    Integer.parseInt(required(ctx.formParam("carId"))),
                    buyer.getId(),
                    required(ctx.formParam("message")),
                    formParamOrEmpty(ctx, "phone")
            );
            ctx.redirect("/cars/" + required(ctx.formParam("carId")) + "?msg=Seller+contacted+successfully");
        });

        app.get("/seller/login", ctx -> ctx.html(HtmlRenderer.loginPage(
                "Seller Login",
                "/seller/login",
                queryParamOrEmpty(ctx, "error"),
                queryParamOrEmpty(ctx, "msg")
        )));

        app.get("/seller/signup", ctx -> ctx.html(HtmlRenderer.signupPage(
                "Seller Sign Up",
                "/seller/signup",
                true,
                queryParamOrEmpty(ctx, "error"),
                queryParamOrEmpty(ctx, "msg")
        )));

        app.post("/seller/signup", ctx -> {
            String username = required(ctx.formParam("username"));
            String password = required(ctx.formParam("password"));
            String email = required(ctx.formParam("email"));
            String phone = required(ctx.formParam("phone"));
            if (!EMAIL.matcher(email).matches()) {
                ctx.redirect("/seller/signup?error=Please+enter+a+valid+email+address");
                return;
            }
            if (!PK_PHONE.matcher(phone.replaceAll("[\\s-]", "")).matches()) {
                ctx.redirect("/seller/signup?error=Enter+a+valid+Pakistani+mobile+number+(e.g.+03001234567)");
                return;
            }
            if (userRepository.usernameExists(username)) {
                ctx.redirect("/seller/signup?error=Username+already+taken");
                return;
            }
            userRepository.createSeller(username, password, email, phone);
            ctx.redirect("/seller/login?msg=Account+successfully+created.+Please+login");
        });

        app.post("/seller/login", ctx -> {
            User user = userRepository.authenticate(ctx.formParam("username"), ctx.formParam("password"), ROLE_SELLER);
            if (user == null) {
                ctx.redirect("/seller/login?error=Invalid+seller+credentials");
                return;
            }
            setSession(ctx, user);
            ctx.redirect("/seller/dashboard");
        });

        app.get("/seller/dashboard", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+first");
                return;
            }
            List<Car> myCars = carRepository.findBySellerId(seller.getId());
            ctx.html(HtmlRenderer.sellerDashboard(seller, myCars, queryParamOrEmpty(ctx, "msg")));
        });

        app.post("/seller/cars", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+again");
                return;
            }
            List<UploadedFile> images = ctx.uploadedFiles("images");
            if (images.isEmpty()) {
                ctx.redirect("/seller/dashboard?msg=Please+upload+at+least+one+image");
                return;
            }

            int year;
            try {
                year = Integer.parseInt(required(ctx.formParam("year")));
            } catch (NumberFormatException ex) {
                ctx.redirect("/seller/dashboard?msg=Year+must+be+a+number");
                return;
            }
            int currentYear = Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                ctx.redirect("/seller/dashboard?msg=Year+must+be+between+1900+and+" + currentYear);
                return;
            }

            List<String> imagePaths = saveUploadedImages(images);
            carRepository.insert(
                    required(ctx.formParam("title")),
                    required(ctx.formParam("make")),
                    required(ctx.formParam("model")),
                    required(ctx.formParam("color")),
                    required(ctx.formParam("city")),
                    year,
                    Double.parseDouble(required(ctx.formParam("price"))),
                    Integer.parseInt(required(ctx.formParam("mileage"))),
                    required(ctx.formParam("description")),
                    imagePaths.get(0),
                    imagePaths,
                    seller.getId(),
                    formParamOrEmpty(ctx, "vehicleType"),
                    formParamOrEmpty(ctx, "transmission"),
                    formParamOrEmpty(ctx, "fuelType"),
                    formParamOrEmpty(ctx, "assembly"),
                    formParamOrEmpty(ctx, "paintCondition"),
                    formParamOrEmpty(ctx, "showeredParts"),
                    formParamOrEmpty(ctx, "body")
            );
            ctx.redirect("/seller/dashboard?msg=Car+added+successfully");
        });

        app.get("/seller/cars/{id}/edit", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+again");
                return;
            }
            Car car = carRepository.findById(Integer.parseInt(ctx.pathParam("id")));
            if (car == null || car.getSellerId() != seller.getId()) {
                ctx.redirect("/seller/dashboard?msg=Car+not+found");
                return;
            }
            ctx.html(HtmlRenderer.sellerEditPage(car, queryParamOrEmpty(ctx, "error")));
        });

        app.post("/seller/cars/{id}/edit", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+again");
                return;
            }
            int carId = Integer.parseInt(ctx.pathParam("id"));
            int year;
            try {
                year = Integer.parseInt(required(ctx.formParam("year")));
            } catch (NumberFormatException ex) {
                ctx.redirect("/seller/cars/" + carId + "/edit?error=Year+must+be+a+number");
                return;
            }
            int currentYear = Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                ctx.redirect("/seller/cars/" + carId + "/edit?error=Year+must+be+between+1900+and+" + currentYear);
                return;
            }
            carRepository.updateCarForSeller(
                    carId,
                    seller.getId(),
                    required(ctx.formParam("title")),
                    required(ctx.formParam("make")),
                    required(ctx.formParam("model")),
                    required(ctx.formParam("color")),
                    required(ctx.formParam("city")),
                    year,
                    Double.parseDouble(required(ctx.formParam("price"))),
                    Integer.parseInt(required(ctx.formParam("mileage"))),
                    required(ctx.formParam("description")),
                    formParamOrEmpty(ctx, "vehicleType"),
                    formParamOrEmpty(ctx, "transmission"),
                    formParamOrEmpty(ctx, "fuelType"),
                    formParamOrEmpty(ctx, "assembly"),
                    formParamOrEmpty(ctx, "paintCondition"),
                    formParamOrEmpty(ctx, "showeredParts"),
                    formParamOrEmpty(ctx, "body")
            );
            List<UploadedFile> images = ctx.uploadedFiles("images");
            if (!images.isEmpty()) {
                carRepository.addImages(carId, saveUploadedImages(images));
            }
            ctx.redirect("/seller/dashboard?msg=Car+updated");
        });

        app.post("/seller/cars/{id}/delete", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+again");
                return;
            }
            carRepository.deleteByIdForSeller(Integer.parseInt(ctx.pathParam("id")), seller.getId());
            ctx.redirect("/seller/dashboard?msg=Car+deleted");
        });

        app.post("/seller/cars/{id}/status", ctx -> {
            User seller = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_SELLER);
            if (seller == null) {
                ctx.redirect("/seller/login?error=Please+login+again");
                return;
            }
            carRepository.updateStatus(
                    Integer.parseInt(ctx.pathParam("id")),
                    seller.getId(),
                    required(ctx.formParam("status"))
            );
            ctx.redirect("/seller/dashboard?msg=Status+updated");
        });

        app.get("/seller/logout", ctx -> {
            clearSession(ctx);
            ctx.redirect("/?msg=Seller+logged+out");
        });

        app.get("/admin/login", ctx -> ctx.html(HtmlRenderer.loginPage(
                "Admin Login",
                "/admin/login",
                queryParamOrEmpty(ctx, "error"),
                queryParamOrEmpty(ctx, "msg")
        )));

        app.post("/admin/login", ctx -> {
            User user = userRepository.authenticate(ctx.formParam("username"), ctx.formParam("password"), ROLE_ADMIN);
            if (user == null) {
                ctx.redirect("/admin/login?error=Invalid+admin+credentials");
                return;
            }
            setSession(ctx, user);
            ctx.redirect("/admin/dashboard");
        });

        app.get("/admin/dashboard", ctx -> {
            User admin = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_ADMIN);
            if (admin == null) {
                ctx.redirect("/admin/login?error=Please+login+first");
                return;
            }
            List<Car> cars = carRepository.findAllWithFilters(Map.of());
            List<User> users = userRepository.findAll();
            List<String> inquiries = carRepository.findInquiryRowsForAdmin();
            ctx.html(HtmlRenderer.adminDashboard(admin, cars, users, inquiries, queryParamOrEmpty(ctx, "msg")));
        });

        app.post("/admin/cars/{id}/status", ctx -> {
            User admin = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_ADMIN);
            if (admin == null) {
                ctx.redirect("/admin/login?error=Please+login+again");
                return;
            }
            carRepository.adminUpdateStatus(Integer.parseInt(ctx.pathParam("id")), required(ctx.formParam("status")));
            ctx.redirect("/admin/dashboard?msg=Car+status+updated");
        });

        app.post("/admin/cars/{id}/delete", ctx -> {
            User admin = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_ADMIN);
            if (admin == null) {
                ctx.redirect("/admin/login?error=Please+login+again");
                return;
            }
            carRepository.deleteById(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/admin/dashboard?msg=Car+deleted");
        });

        app.post("/admin/users/{id}/delete", ctx -> {
            User admin = requireRole(ctx.sessionAttribute("userId"), ctx.sessionAttribute("username"), ctx.sessionAttribute("role"), ROLE_ADMIN);
            if (admin == null) {
                ctx.redirect("/admin/login?error=Please+login+again");
                return;
            }
            userRepository.deleteById(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/admin/dashboard?msg=User+deleted");
        });

        app.get("/admin/logout", ctx -> {
            clearSession(ctx);
            ctx.redirect("/?msg=Admin+logged+out");
        });

        app.start(7000);
        System.out.println("Autosphere running at http://localhost:7000");
    }

    private static String required(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field");
        }
        return value.trim();
    }

    private static String queryParamOrEmpty(io.javalin.http.Context ctx, String key) {
        String value = ctx.queryParam(key);
        return value == null ? "" : value;
    }

    private static String formParamOrEmpty(io.javalin.http.Context ctx, String key) {
        String value = ctx.formParam(key);
        return value == null ? "" : value;
    }

    private static String saveUploadedImage(UploadedFile image) {
        String extension = ".jpg";
        String originalName = image.filename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID() + extension;
        Path target = Path.of("uploads", fileName);
        try (InputStream input = image.content()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed saving uploaded image", e);
        }
        return "/" + fileName;
    }

    private static List<String> saveUploadedImages(List<UploadedFile> images) {
        return images.stream().map(Main::saveUploadedImage).toList();
    }

    private static User requireRole(Integer userId, String username, String role, String expectedRole) {
        if (userId == null || username == null || role == null) {
            return null;
        }
        if (!expectedRole.equals(role)) {
            return null;
        }
        return new User(userId, username, role);
    }

    private static User getCurrentUser(io.javalin.http.Context ctx) {
        Integer userId = ctx.sessionAttribute("userId");
        String username = ctx.sessionAttribute("username");
        String role = ctx.sessionAttribute("role");
        if (userId == null || username == null || role == null) {
            return null;
        }
        return new User(userId, username, role);
    }

    private static void setSession(io.javalin.http.Context ctx, User user) {
        ctx.sessionAttribute("userId", user.getId());
        ctx.sessionAttribute("username", user.getUsername());
        ctx.sessionAttribute("role", user.getRole());
    }

    private static void clearSession(io.javalin.http.Context ctx) {
        ctx.sessionAttribute("userId", null);
        ctx.sessionAttribute("username", null);
        ctx.sessionAttribute("role", null);
    }

    @SuppressWarnings("unchecked")
    private static Set<Integer> getGuestFavorites(io.javalin.http.Context ctx) {
        Set<Integer> favs = ctx.sessionAttribute("guestFavorites");
        if (favs == null) {
            favs = new HashSet<>();
            ctx.sessionAttribute("guestFavorites", favs);
        }
        return favs;
    }

    private static Set<Integer> collectFavoriteIds(io.javalin.http.Context ctx, User viewer, CarRepository carRepository) {
        Set<Integer> favoriteIds = new HashSet<>();
        if (viewer != null && ROLE_BUYER.equals(viewer.getRole())) {
            for (Car c : carRepository.findFavoritesByUserId(viewer.getId())) {
                favoriteIds.add(c.getId());
            }
        } else {
            Set<Integer> guest = ctx.sessionAttribute("guestFavorites");
            if (guest != null) favoriteIds.addAll(guest);
        }
        return favoriteIds;
    }

    private static String joinModelParams(io.javalin.http.Context ctx) {
        List<String> values = ctx.queryParams("model");
        if (values == null || values.isEmpty()) return "";
        Set<String> dedup = new java.util.LinkedHashSet<>();
        for (String raw : values) {
            if (raw == null) continue;
            for (String part : raw.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) dedup.add(trimmed);
            }
        }
        return String.join(",", dedup);
    }
}
