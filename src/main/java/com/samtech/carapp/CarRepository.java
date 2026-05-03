package com.samtech.carapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarRepository {
    public List<Car> findAllWithFilters(Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
                SELECT c.*, u.username AS seller_name
                FROM cars c
                JOIN users u ON c.seller_id = u.id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        addLikeFilter(sql, params, "c.make", filters.get("make"));
        addModelInFilter(sql, params, filters.get("model"));
        addLikeFilter(sql, params, "c.color", filters.get("color"));
        addLikeFilter(sql, params, "c.city", filters.get("city"));
        addBodyTypeFilter(sql, params, filters.get("body"));
        addConditionFilter(sql, params, filters.get("condition"));
        addExactFilter(sql, params, "c.transmission", filters.get("transmission"));
        addMinPriceFilter(sql, params, filters.get("minPrice"));
        addMaxPriceFilter(sql, params, filters.get("maxPrice"));
        addMinMileageFilter(sql, params, filters.get("minMileage"));
        addMaxMileageFilter(sql, params, filters.get("maxMileage"));
        addMinYearFilter(sql, params, filters.get("minYear"));
        addMaxYearFilter(sql, params, filters.get("maxYear"));
        addStatusFilter(sql, params, filters.get("status"));
        addVehicleTypeFilter(sql, params, filters.get("type"));
        addSearchFilter(sql, params, filters.get("search"));

        sql.append(" ORDER BY c.created_at DESC, c.id DESC");

        List<Car> cars = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching cars", e);
        }
    }

    public List<Car> findBySellerId(int sellerId) {
        String sql = """
                SELECT c.*, u.username AS seller_name
                FROM cars c
                JOIN users u ON c.seller_id = u.id
                WHERE c.seller_id = ?
                ORDER BY c.created_at DESC, c.id DESC
                """;
        List<Car> cars = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching seller cars", e);
        }
    }

    public void insert(
            String title,
            String make,
            String model,
            String color,
            String city,
            int year,
            double price,
            int mileage,
            String description,
            String imagePath,
            List<String> imagePaths,
            int sellerId,
            String vehicleType,
            String transmission,
            String fuelType,
            String assembly,
            String paintCondition,
            String showeredParts,
            String body
    ) {
        String sql = """
                INSERT INTO cars(title, make, model, color, city, year, price, mileage, description, image_path, seller_id, status, vehicle_type, transmission, fuel_type, assembly, paint_condition, showered_parts, body)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'AVAILABLE', ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, make);
            ps.setString(3, model);
            ps.setString(4, color);
            ps.setString(5, city);
            ps.setInt(6, year);
            ps.setDouble(7, price);
            ps.setInt(8, mileage);
            ps.setString(9, description);
            ps.setString(10, imagePath);
            ps.setInt(11, sellerId);
            ps.setString(12, vehicleType);
            ps.setString(13, transmission);
            ps.setString(14, fuelType);
            ps.setString(15, assembly);
            ps.setString(16, paintCondition);
            ps.setString(17, showeredParts);
            ps.setString(18, body);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int carId = keys.getInt(1);
                    saveImages(carId, imagePaths);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed inserting car", e);
        }
    }

    public void deleteById(int carId) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed deleting car", e);
        }
    }

    public void deleteByIdForSeller(int carId, int sellerId) {
        String sql = "DELETE FROM cars WHERE id = ? AND seller_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, sellerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed deleting seller car", e);
        }
    }

    public Car findById(int carId) {
        String sql = """
                SELECT c.*, u.username AS seller_name
                FROM cars c
                JOIN users u ON c.seller_id = u.id
                WHERE c.id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapCar(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching car", e);
        }
    }

    public CarDetails findDetailsById(int carId) {
        String sql = """
                SELECT c.*, u.username AS seller_name, u.email AS seller_email, u.phone AS seller_phone
                FROM cars c
                JOIN users u ON c.seller_id = u.id
                WHERE c.id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            Car car = mapCar(rs);
            User seller = new User(rs.getInt("seller_id"), rs.getString("seller_name"), "SELLER",
                    nullToEmpty(rs.getString("seller_email")), nullToEmpty(rs.getString("seller_phone")));
            return new CarDetails(car, seller, findImagesByCarId(carId));
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching car details", e);
        }
    }

    public void updateCarForSeller(
            int carId,
            int sellerId,
            String title,
            String make,
            String model,
            String color,
            String city,
            int year,
            double price,
            int mileage,
            String description,
            String vehicleType,
            String transmission,
            String fuelType,
            String assembly,
            String paintCondition,
            String showeredParts,
            String body
    ) {
        String sql = """
                UPDATE cars
                SET title = ?, make = ?, model = ?, color = ?, city = ?, year = ?, price = ?, mileage = ?, description = ?,
                    vehicle_type = ?, transmission = ?, fuel_type = ?, assembly = ?, paint_condition = ?, showered_parts = ?, body = ?
                WHERE id = ? AND seller_id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, make);
            ps.setString(3, model);
            ps.setString(4, color);
            ps.setString(5, city);
            ps.setInt(6, year);
            ps.setDouble(7, price);
            ps.setInt(8, mileage);
            ps.setString(9, description);
            ps.setString(10, vehicleType);
            ps.setString(11, transmission);
            ps.setString(12, fuelType);
            ps.setString(13, assembly);
            ps.setString(14, paintCondition);
            ps.setString(15, showeredParts);
            ps.setString(16, body);
            ps.setInt(17, carId);
            ps.setInt(18, sellerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed updating seller car", e);
        }
    }

    public void addImages(int carId, List<String> imagePaths) {
        saveImages(carId, imagePaths);
    }

    public void updateStatus(int carId, int sellerId, String status) {
        String sql = "UPDATE cars SET status = ? WHERE id = ? AND seller_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeStatus(status));
            ps.setInt(2, carId);
            ps.setInt(3, sellerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed updating car status", e);
        }
    }

    public void adminUpdateStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeStatus(status));
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed updating car status", e);
        }
    }

    public void addFavorite(int userId, int carId) {
        String sql = "INSERT OR IGNORE INTO favorites(user_id, car_id) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed adding favorite", e);
        }
    }

    public void removeFavorite(int userId, int carId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND car_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed removing favorite", e);
        }
    }

    public List<Car> findFavoritesByUserId(int userId) {
        String sql = """
                SELECT c.*, u.username AS seller_name
                FROM favorites f
                JOIN cars c ON f.car_id = c.id
                JOIN users u ON c.seller_id = u.id
                WHERE f.user_id = ?
                ORDER BY f.created_at DESC
                """;
        List<Car> cars = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching favorites", e);
        }
    }

    public void createInquiry(int carId, int buyerId, String message, String phone) {
        String sql = "INSERT INTO inquiries(car_id, buyer_id, message, phone) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, buyerId);
            ps.setString(3, message);
            ps.setString(4, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed creating inquiry", e);
        }
    }

    public List<String> findInquiryRowsForAdmin() {
        String sql = """
                SELECT i.id, c.title AS car_title, b.username AS buyer_name, i.phone, i.message, i.created_at
                FROM inquiries i
                JOIN cars c ON i.car_id = c.id
                JOIN users b ON i.buyer_id = b.id
                ORDER BY i.created_at DESC
                """;
        List<String> rows = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add("#" + rs.getInt("id")
                        + " | " + rs.getString("car_title")
                        + " | " + rs.getString("buyer_name")
                        + " | " + nullToEmpty(rs.getString("phone"))
                        + " | " + rs.getString("message")
                        + " | " + rs.getString("created_at"));
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching inquiries", e);
        }
    }

    public List<String> findImagesByCarId(int carId) {
        String sql = "SELECT image_path FROM car_images WHERE car_id = ? ORDER BY id";
        List<String> images = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("image_path"));
            }
            return images;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching car images", e);
        }
    }

    public boolean isFavorite(int userId, int carId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND car_id = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, carId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed checking favorite", e);
        }
    }

    public List<Car> findByIds(List<Integer> ids) {
        List<Car> cars = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return cars;
        StringBuilder sql = new StringBuilder("""
                SELECT c.*, u.username AS seller_name
                FROM cars c
                JOIN users u ON c.seller_id = u.id
                WHERE c.id IN (
                """);
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(") ORDER BY c.id DESC");
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching cars by ids", e);
        }
    }

    private void addLikeFilter(StringBuilder sql, List<Object> params, String field, String value) {
        if (value != null && !value.isBlank()) {
            sql.append(" AND ").append(field).append(" LIKE ?");
            params.add("%" + value.trim() + "%");
        }
    }

    private void addExactFilter(StringBuilder sql, List<Object> params, String field, String value) {
        if (value != null && !value.isBlank()) {
            sql.append(" AND ").append(field).append(" = ?");
            params.add(value.trim());
        }
    }

    private void addModelInFilter(StringBuilder sql, List<Object> params, String csv) {
        if (csv == null || csv.isBlank()) return;
        String[] parts = csv.split(",");
        List<String> models = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) models.add(t);
        }
        if (models.isEmpty()) return;
        sql.append(" AND (");
        for (int i = 0; i < models.size(); i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("c.model LIKE ?");
            params.add("%" + models.get(i) + "%");
        }
        sql.append(")");
    }

    private void addBodyTypeFilter(StringBuilder sql, List<Object> params, String body) {
        if (body == null || body.isBlank()) return;
        sql.append(" AND (c.body = ? OR c.title LIKE ? OR c.description LIKE ?)");
        params.add(body.trim());
        params.add("%" + body.trim() + "%");
        params.add("%" + body.trim() + "%");
    }

    private void addConditionFilter(StringBuilder sql, List<Object> params, String condition) {
        if (condition == null || condition.isBlank()) return;
        if ("New".equalsIgnoreCase(condition.trim())) {
            sql.append(" AND ((c.paint_condition IS NULL OR c.paint_condition = '' OR c.paint_condition = 'Genuine') AND c.year >= ?)");
            params.add(java.time.Year.now().getValue() - 1);
            return;
        }
        sql.append(" AND c.paint_condition = ?");
        params.add(condition.trim());
    }

    private void addMinYearFilter(StringBuilder sql, List<Object> params, String value) {
        if (value != null && !value.isBlank()) {
            try {
                sql.append(" AND c.year >= ?");
                params.add(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void addMaxYearFilter(StringBuilder sql, List<Object> params, String value) {
        if (value != null && !value.isBlank()) {
            try {
                sql.append(" AND c.year <= ?");
                params.add(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void addSearchFilter(StringBuilder sql, List<Object> params, String value) {
        if (value == null || value.isBlank()) return;
        sql.append(" AND (c.title LIKE ? OR c.make LIKE ? OR c.model LIKE ? OR c.description LIKE ?)");
        String pattern = "%" + value.trim() + "%";
        params.add(pattern);
        params.add(pattern);
        params.add(pattern);
        params.add(pattern);
    }

    private void addMinPriceFilter(StringBuilder sql, List<Object> params, String minPrice) {
        if (minPrice != null && !minPrice.isBlank()) {
            try {
                sql.append(" AND c.price >= ?");
                params.add(Double.parseDouble(minPrice));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void addMaxPriceFilter(StringBuilder sql, List<Object> params, String maxPrice) {
        if (maxPrice != null && !maxPrice.isBlank()) {
            try {
                sql.append(" AND c.price <= ?");
                params.add(Double.parseDouble(maxPrice));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void addMinMileageFilter(StringBuilder sql, List<Object> params, String minMileage) {
        if (minMileage != null && !minMileage.isBlank()) {
            try {
                sql.append(" AND c.mileage >= ?");
                params.add(Integer.parseInt(minMileage));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void addMaxMileageFilter(StringBuilder sql, List<Object> params, String maxMileage) {
        if (maxMileage != null && !maxMileage.isBlank()) {
            try {
                sql.append(" AND c.mileage <= ?");
                params.add(Integer.parseInt(maxMileage));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void addStatusFilter(StringBuilder sql, List<Object> params, String status) {
        if (status != null && !status.isBlank()) {
            sql.append(" AND c.status = ?");
            params.add(normalizeStatus(status));
        }
    }

    private void addVehicleTypeFilter(StringBuilder sql, List<Object> params, String type) {
        if (type != null && !type.isBlank()) {
            sql.append(" AND c.vehicle_type = ?");
            params.add(type);
        }
    }

    private Car mapCar(ResultSet rs) throws SQLException {
        String body = "";
        try { body = rs.getString("body"); } catch (SQLException ignored) {}
        return new Car(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getString("color"),
                rs.getString("city"),
                rs.getInt("year"),
                rs.getDouble("price"),
                rs.getInt("mileage"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getInt("seller_id"),
                rs.getString("seller_name"),
                rs.getString("status"),
                rs.getString("vehicle_type"),
                rs.getString("transmission"),
                rs.getString("fuel_type"),
                rs.getString("assembly"),
                rs.getString("paint_condition"),
                rs.getString("showered_parts"),
                body == null ? "" : body
        );
    }

    private void saveImages(int carId, List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO car_images(car_id, image_path) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String imagePath : imagePaths) {
                ps.setInt(1, carId);
                ps.setString(2, imagePath);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed saving car images", e);
        }
    }

    private String normalizeStatus(String status) {
        if ("SOLD".equalsIgnoreCase(status)) {
            return "SOLD";
        }
        return "AVAILABLE";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
