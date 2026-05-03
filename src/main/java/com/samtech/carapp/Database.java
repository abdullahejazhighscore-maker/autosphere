package com.samtech.carapp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String DB_DIR = "data";
    private static final String DB_URL = "jdbc:sqlite:data/car_marketplace.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() {
        ensureDirectories();
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        email TEXT,
                        phone TEXT,
                        role TEXT NOT NULL
                    )
                    """);
            ensureUsersExtraColumns(st);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS cars (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        make TEXT NOT NULL,
                        model TEXT NOT NULL,
                        color TEXT NOT NULL,
                        city TEXT NOT NULL,
                        year INTEGER NOT NULL,
                        price REAL NOT NULL,
                        mileage INTEGER NOT NULL,
                        description TEXT,
                        image_path TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'AVAILABLE',
                        seller_id INTEGER NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (seller_id) REFERENCES users(id)
                    )
                    """);
            ensureCarsStatusColumn(st);
            ensureCarsExtraColumns(st);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS favorites (
                    
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        car_id INTEGER NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(user_id, car_id),
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (car_id) REFERENCES cars(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS inquiries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        car_id INTEGER NOT NULL,
                        buyer_id INTEGER NOT NULL,
                        message TEXT NOT NULL,
                        phone TEXT,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (car_id) REFERENCES cars(id),
                        FOREIGN KEY (buyer_id) REFERENCES users(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS car_images (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        car_id INTEGER NOT NULL,
                        image_path TEXT NOT NULL,
                        FOREIGN KEY (car_id) REFERENCES cars(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS car_specs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT UNIQUE NOT NULL,
                        engine TEXT,
                        fuel_type TEXT,
                        max_power TEXT,
                        max_torque TEXT,
                        transmission TEXT,
                        drive_type TEXT,
                        body_type TEXT,
                        seats INTEGER,
                        fuel_tank TEXT,
                        fuel_economy TEXT,
                        dimensions TEXT,
                        wheelbase TEXT,
                        ground_clearance TEXT,
                        kerb_weight TEXT,
                        safety TEXT,
                        price_range TEXT
                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
        migratePlainTextPasswords();
        seedDefaultUsers();
        removeDummyCars();
        seedCarSpecs();
    }

    private static void ensureDirectories() {
        new File(DB_DIR).mkdirs();
        new File("uploads").mkdirs();
    }

    private static void seedDefaultUsers() {
        createUserIfMissing("admin", "admin123", "ADMIN");
        createUserIfMissing("seller1", "seller123", "SELLER", "seller1@autosphere.app", "+92-300-1111111");
        createUserIfMissing("seller2", "seller123", "SELLER", "seller2@autosphere.app", "+92-300-2222222");
        createUserIfMissing("buyer1", "buyer123", "BUYER");
    }

    private static void createUserIfMissing(String username, String password, String role) {
        createUserIfMissing(username, password, role, "", "");
    }

    private static void createUserIfMissing(String username, String password, String role, String email, String phone) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users(username, password, role, email, phone) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setString(1, username);
                    insert.setString(2, PasswordUtil.hashPassword(password));
                    insert.setString(3, role);
                    insert.setString(4, email);
                    insert.setString(5, phone);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed users", e);
        }
    }

    private static void ensureCarsStatusColumn(Statement st) throws SQLException {
        try {
            st.execute("ALTER TABLE cars ADD COLUMN status TEXT NOT NULL DEFAULT 'AVAILABLE'");
        } catch (SQLException ignored) {
        }
    }

    private static void ensureCarsExtraColumns(Statement st) throws SQLException {
        String[] columns = {
            "ALTER TABLE cars ADD COLUMN vehicle_type TEXT NOT NULL DEFAULT 'Car'",
            "ALTER TABLE cars ADD COLUMN transmission TEXT",
            "ALTER TABLE cars ADD COLUMN fuel_type TEXT",
            "ALTER TABLE cars ADD COLUMN assembly TEXT",
            "ALTER TABLE cars ADD COLUMN paint_condition TEXT",
            "ALTER TABLE cars ADD COLUMN showered_parts TEXT",
            "ALTER TABLE cars ADD COLUMN body TEXT"
        };
        for (String colSql : columns) {
            try {
                st.execute(colSql);
            } catch (SQLException ignored) {
            }
        }
    }

    private static void ensureUsersExtraColumns(Statement st) throws SQLException {
        try {
            st.execute("ALTER TABLE users ADD COLUMN email TEXT");
        } catch (SQLException ignored) {
        }
        try {
            st.execute("ALTER TABLE users ADD COLUMN phone TEXT");
        } catch (SQLException ignored) {
        }
    }

    private static void migratePlainTextPasswords() {
        String sql = "SELECT id, password FROM users";
        String update = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String stored = rs.getString("password");
                if (stored != null && !stored.isBlank() && !looksHashed(stored)) {
                    try (PreparedStatement ps = conn.prepareStatement(update)) {
                        ps.setString(1, PasswordUtil.hashPassword(stored));
                        ps.setInt(2, rs.getInt("id"));
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed migrating passwords", e);
        }
    }

    private static boolean looksHashed(String value) {
        return value.length() == 44 && value.endsWith("=");
    }

    private static void removeDummyCars() {
        String[] dummyTitles = {
                "Honda Civic 2020", "Toyota Corolla Altis", "Hyundai Elantra GLS",
                "Kia Sportage AWD", "Suzuki Swift GLX", "BMW 320i Sport",
                "Audi A4 Premium", "Mercedes C200", "Nissan X-Trail", "Ford Mustang EcoBoost"
        };
        String sql = "DELETE FROM cars WHERE title = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String title : dummyTitles) {
                ps.setString(1, title);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Failed removing dummy cars: " + e.getMessage());
        }
    }

    private static void seedSampleCars() {
        String countSql = "SELECT COUNT(*) AS c FROM cars";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("c") >= 10) {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed counting cars", e);
        }

        int seller1 = getUserIdByUsername("seller1");
        int seller2 = getUserIdByUsername("seller2");

        insertSampleCar("Honda Civic 2020", "Honda", "Civic", "White", "Lahore", 2020, 21500, 45000,
                "Well-maintained sedan, single owner, excellent fuel economy.", "https://source.unsplash.com/1200x800/?honda,civic,car", seller1);
        insertSampleCar("Toyota Corolla Altis", "Toyota", "Corolla", "Black", "Karachi", 2021, 23800, 35000,
                "Smooth drive with alloy rims and new tires.", "https://source.unsplash.com/1200x800/?toyota,corolla,car", seller2);
        insertSampleCar("Hyundai Elantra GLS", "Hyundai", "Elantra", "Blue", "Islamabad", 2022, 26500, 22000,
                "Top variant with leather seats and camera package.", "https://source.unsplash.com/1200x800/?hyundai,elantra,car", seller1);
        insertSampleCar("Kia Sportage AWD", "Kia", "Sportage", "Red", "Rawalpindi", 2021, 32900, 28000,
                "Family SUV with panoramic sunroof and full service history.", "https://source.unsplash.com/1200x800/?kia,sportage,suv", seller2);
        insertSampleCar("Suzuki Swift GLX", "Suzuki", "Swift", "Silver", "Peshawar", 2019, 16200, 52000,
                "Compact hatchback with smart infotainment and low maintenance.", "https://source.unsplash.com/1200x800/?suzuki,swift,car", seller1);
        insertSampleCar("BMW 320i Sport", "BMW", "320i", "Grey", "Lahore", 2018, 38900, 64000,
                "Executive sedan with powerful turbo engine and premium cabin.", "https://source.unsplash.com/1200x800/?bmw,sedan,car", seller2);
        insertSampleCar("Audi A4 Premium", "Audi", "A4", "White", "Islamabad", 2019, 41200, 55000,
                "Luxury sedan with virtual cockpit and comfort package.", "https://source.unsplash.com/1200x800/?audi,a4,car", seller1);
        insertSampleCar("Mercedes C200", "Mercedes", "C200", "Black", "Karachi", 2020, 46800, 40000,
                "Elegant and reliable, includes AMG styling and LED package.", "https://source.unsplash.com/1200x800/?mercedes,c-class,car", seller2);
        insertSampleCar("Nissan X-Trail", "Nissan", "X-Trail", "Green", "Multan", 2017, 24800, 76000,
                "Spacious crossover with excellent road grip.", "https://source.unsplash.com/1200x800/?nissan,suv,car", seller1);
        insertSampleCar("Ford Mustang EcoBoost", "Ford", "Mustang", "Yellow", "Faisalabad", 2021, 55200, 15000,
                "Performance coupe in pristine condition with sporty exhaust note.", "https://source.unsplash.com/1200x800/?ford,mustang,car", seller2);
    }

    private static int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new IllegalStateException("User not found: " + username);
        } catch (SQLException e) {
            throw new RuntimeException("Failed finding user id", e);
        }
    }

    private static void insertSampleCar(
            String title, String make, String model, String color, String city, int year, double price,
            int mileage, String description, String imagePath, int sellerId
    ) {
        String sql = """
                INSERT INTO cars(title, make, model, color, city, year, price, mileage, description, image_path, seller_id)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = getConnection();
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
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    insertCarImage(keys.getInt(1), imagePath);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding cars", e);
        }
    }

    private static void insertCarImage(int carId, String imagePath) {
        String sql = "INSERT INTO car_images(car_id, image_path) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setString(2, imagePath);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed inserting car image", e);
        }
    }

    private static void seedCarSpecs() {
        String countSql = "SELECT COUNT(*) AS count FROM car_specs";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("count") > 0) {
                return;
            }
        } catch (SQLException e) {
            System.err.println("Failed to count car_specs: " + e.getMessage());
            return;
        }

        String insertSql = "INSERT INTO car_specs (name, engine, fuel_type, max_power, max_torque, transmission, drive_type, body_type, seats, fuel_tank, fuel_economy, dimensions, wheelbase, ground_clearance, kerb_weight, safety, price_range) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            
            insertSpec(ps, "Suzuki Alto VX", "660cc, 3-Cylinder", "Petrol", "38 hp @ 5500 rpm", "57 Nm @ 3500 rpm", "5-speed Manual / AGS Auto", "FWD", "Hatchback", 4, "27 Litres", "~18–20 km/l", "3395 × 1475 mm", "2360 mm", "160 mm", "650 kg", "Driver airbag, ABS (higher trims)", "PKR 23.3L – 27L");
            insertSpec(ps, "Suzuki Cultus VXL", "1000cc, 3-Cylinder K10B", "Petrol", "67 hp @ 6200 rpm", "90 Nm @ 3500 rpm", "5-speed Manual / AGS Auto", "FWD", "Hatchback", 5, "35 Litres", "~14–17 km/l", "3600 × 1595 mm", "2425 mm", "165 mm", "790 kg", "Dual airbags, ABS, EBD", "PKR 29L – 34L");
            insertSpec(ps, "Suzuki Swift GL CVT", "1200cc, 4-Cylinder K12B", "Petrol", "82 hp @ 6000 rpm", "113 Nm @ 4400 rpm", "CVT Automatic / 5-speed Manual", "FWD", "Hatchback", 5, "37 Litres", "~16–19 km/l", "3840 × 1735 mm", "2450 mm", "170 mm", "880 kg", "Dual airbags, ABS, EBD, ESP", "PKR 38L – 44L");
            insertSpec(ps, "Suzuki Wagon R VXL", "1000cc, 3-Cylinder K10B", "Petrol / CNG", "67 hp @ 6200 rpm", "90 Nm @ 3500 rpm", "5-speed Manual / AGS Auto", "FWD", "Tall Hatchback", 5, "32 Litres", "~15–18 km/l", "3599 × 1620 mm", "2400 mm", "170 mm", "800 kg", "Driver airbag, ABS (VXL trim)", "PKR 27L – 32L");
            insertSpec(ps, "Toyota Yaris 1.3 MT", "1300cc, 4-Cylinder 2NR-FE", "Petrol", "97 hp @ 6000 rpm", "121 Nm @ 4400 rpm", "6-speed Manual / CVT Auto", "FWD", "Sedan", 5, "42 Litres", "~16–18 km/l", "4425 × 1730 mm", "2550 mm", "145 mm", "1045 kg", "Dual airbags, ABS, EBD, VSC", "PKR 39L – 50L");
            insertSpec(ps, "Toyota Corolla Altis 1.6", "1600cc, 4-Cylinder 1ZR-FE", "Petrol", "122 hp @ 6000 rpm", "154 Nm @ 4000 rpm", "CVT Automatic / 6-speed Manual", "FWD", "Sedan", 5, "50 Litres", "~13–16 km/l", "4630 × 1775 mm", "2700 mm", "140 mm", "1260 kg", "7 airbags, ABS, EBD, VSC, Pre-collision System", "PKR 65L – 82L");
            insertSpec(ps, "Honda City 1.2L MT", "1200cc, 4-Cylinder L12B", "Petrol", "89 hp @ 6000 rpm", "110 Nm @ 4800 rpm", "6-speed Manual / CVT Auto", "FWD", "Sedan", 5, "40 Litres", "~15–18 km/l", "4553 × 1748 mm", "2600 mm", "132 mm", "1070 kg", "Dual SRS airbags, ABS, EBD, VSA", "PKR 42L – 55L");
            insertSpec(ps, "Honda Civic 1.5 Turbo", "1500cc Turbo, 4-Cylinder L15B7", "Petrol", "174 hp @ 6000 rpm", "220 Nm @ 1700–5500 rpm", "CVT Automatic", "FWD", "Sedan", 5, "47 Litres", "~12–15 km/l", "4674 × 1802 mm", "2730 mm", "135 mm", "1284 kg", "6 airbags, ABS, EBD, VSA, Honda Sensing", "PKR 90L – 110L");
            insertSpec(ps, "Changan Alsvin 1.5L CVT", "1500cc, 4-Cylinder", "Petrol", "107 hp @ 6000 rpm", "142 Nm @ 4100 rpm", "CVT Automatic / 5-speed Manual", "FWD", "Sedan", 5, "40 Litres", "~14–17 km/l", "4390 × 1750 mm", "2600 mm", "140 mm", "1100 kg", "6 airbags, ABS, EBD, ESP, TCS", "PKR 38L – 47L");
            insertSpec(ps, "KIA Stonic 1.4L", "1400cc, 4-Cylinder", "Petrol", "100 hp @ 6000 rpm", "133 Nm @ 4000 rpm", "6-speed Automatic", "FWD", "Compact SUV", 5, "45 Litres", "~13–16 km/l", "4140 × 1760 mm", "2580 mm", "170 mm", "1165 kg", "6 airbags, ABS, EBD, ESC, Hill Assist", "PKR 55L – 65L");
            insertSpec(ps, "KIA Sportage 2.0L Alpha", "2000cc, 4-Cylinder Nu MPI", "Petrol", "155 hp @ 6200 rpm", "196 Nm @ 4000 rpm", "6-speed Automatic", "FWD / AWD", "Mid-Size SUV", 5, "55 Litres", "~11–14 km/l", "4515 × 1865 mm", "2670 mm", "181 mm", "1540 kg", "6 airbags, ABS, ESC, TPMS, Blind Spot Monitor", "PKR 90L – 115L");
            insertSpec(ps, "Hyundai Tucson 2.0L GLS", "2000cc, 4-Cylinder MPI", "Petrol", "155 hp @ 6200 rpm", "196 Nm @ 4000 rpm", "6-speed Automatic", "FWD", "Mid-Size SUV", 5, "62 Litres", "~11–14 km/l", "4475 × 1850 mm", "2670 mm", "183 mm", "1565 kg", "6 airbags, ABS, ESC, TPMS, Rear Camera", "PKR 80L – 100L");
            insertSpec(ps, "MG HS 1.5T", "1500cc Turbo, 4-Cylinder", "Petrol", "162 hp @ 5600 rpm", "250 Nm @ 1600–4000 rpm", "7-speed DCT Automatic", "FWD", "Mid-Size SUV", 5, "55 Litres", "~12–15 km/l", "4571 × 1876 mm", "2720 mm", "190 mm", "1564 kg", "6 airbags, ABS, ESP, Lane Assist, 360° Camera", "PKR 85L – 105L");
            insertSpec(ps, "Proton X70 1.8T Premium", "1800cc Turbo, 4-Cylinder", "Petrol", "181 hp @ 5500 rpm", "285 Nm @ 2000–4000 rpm", "7-speed DCT Automatic", "FWD", "Mid-Size SUV", 5, "55 Litres", "~11–14 km/l", "4519 × 1833 mm", "2700 mm", "200 mm", "1595 kg", "6 airbags, ABS, ESC, AEBS, Lane Keep Assist, Blind Spot Detection", "PKR 88L – 100L");
            insertSpec(ps, "Toyota Fortuner 2.7L VVT-i", "2700cc, 4-Cylinder 2TR-FE", "Petrol", "164 hp @ 5200 rpm", "245 Nm @ 4000 rpm", "6-speed Automatic", "4WD (Part-Time)", "7-Seat SUV / 4x4", 7, "80 Litres", "~9–12 km/l", "4795 × 1855 mm", "2745 mm", "221 mm", "1960 kg", "7 airbags, ABS, VSC, TRAC, Hill Start Assist", "PKR 155L – 195L");

        } catch (SQLException e) {
            System.err.println("Failed seeding car specs: " + e.getMessage());
        }
    }

    private static void insertSpec(PreparedStatement ps, String name, String engine, String fuelType, String maxPower, String maxTorque, String transmission, String driveType, String bodyType, int seats, String fuelTank, String fuelEconomy, String dimensions, String wheelbase, String groundClearance, String kerbWeight, String safety, String priceRange) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, engine);
        ps.setString(3, fuelType);
        ps.setString(4, maxPower);
        ps.setString(5, maxTorque);
        ps.setString(6, transmission);
        ps.setString(7, driveType);
        ps.setString(8, bodyType);
        ps.setInt(9, seats);
        ps.setString(10, fuelTank);
        ps.setString(11, fuelEconomy);
        ps.setString(12, dimensions);
        ps.setString(13, wheelbase);
        ps.setString(14, groundClearance);
        ps.setString(15, kerbWeight);
        ps.setString(16, safety);
        ps.setString(17, priceRange);
        ps.executeUpdate();
    }
}
