package com.samtech.carapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarSpecRepository {

    public List<CarSpec> findAll() {
        String sql = "SELECT * FROM car_specs ORDER BY name ASC";
        List<CarSpec> specs = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                specs.add(mapCarSpec(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all car specs", e);
        }
        return specs;
    }

    public List<CarSpec> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder("SELECT * FROM car_specs WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        List<CarSpec> specs = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    specs.add(mapCarSpec(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find car specs by ids", e);
        }
        return specs;
    }

    private CarSpec mapCarSpec(ResultSet rs) throws SQLException {
        return new CarSpec(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("engine"),
                rs.getString("fuel_type"),
                rs.getString("max_power"),
                rs.getString("max_torque"),
                rs.getString("transmission"),
                rs.getString("drive_type"),
                rs.getString("body_type"),
                rs.getInt("seats"),
                rs.getString("fuel_tank"),
                rs.getString("fuel_economy"),
                rs.getString("dimensions"),
                rs.getString("wheelbase"),
                rs.getString("ground_clearance"),
                rs.getString("kerb_weight"),
                rs.getString("safety"),
                rs.getString("price_range")
        );
    }
}
