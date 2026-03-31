package dao.impl;


import dao.IMenuItemDAO;
import model.MenuItem;
import model.enums.FoodType;
import model.enums.MenuStatus;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO implements IMenuItemDAO {
    @Override
    public void insert(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu (name, price, type, stock, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {

            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            ps.setString(3, item.getFoodType().name());
            ps.setInt(4, item.getStock());
            ps.setString(5, item.getStatus().name());

            ps.executeUpdate();
        }
    }

    @Override
    public List<MenuItem> findAll() throws SQLException {
        List<MenuItem> menuItemList = new ArrayList<>();
        String sqlGetAll = "SELECT menu_id, name, price, type, stock, status FROM menu";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlGetAll);
             ResultSet rs = ps.executeQuery();) {

            while (rs.next()) {
                menuItemList.add(map(rs));
            }
        }
        return menuItemList;
    }

    @Override
    public MenuItem findById(Connection conn, int id) throws SQLException {
        String sqlSearchId = "SELECT menu_id, name, price, type, stock, status FROM menu WHERE menu_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSearchId);) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<MenuItem> findByType(Connection conn, FoodType foodType) throws SQLException {
        String sqlSearchType = "SELECT menu_id, name, price, type, stock, status FROM menu WHERE type = ? AND status = ?";
        List<MenuItem> menuList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sqlSearchType);) {

            ps.setString(1, foodType.name());
            ps.setString(2, MenuStatus.AVAILABLE.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                menuList.add(map(rs));
            }
        }
        return menuList;
    }

    @Override
    public void update(Connection conn, MenuItem item) throws SQLException {
        String sqlUpdate = "UPDATE menu SET name = ?, price = ?, type = ?, stock = ?, status = ? WHERE menu_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate);) {

            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            ps.setString(3, item.getFoodType().name());
            ps.setInt(4, item.getStock());
            ps.setString(5, item.getStatus().name());
            ps.setInt(6, item.getId());

            int rows = ps.executeUpdate();

            if (rows == 0){
                throw new SQLException("ID món ăn không tồn tại trong DB");
            }
        }
    }

    @Override
    public boolean updateStock(Connection conn, int menuItemId, int newStock) throws SQLException {
        String sql = "UPDATE menu SET stock = ? WHERE menu_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, menuItemId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    @Override
    public void delete(Connection conn, int id) throws SQLException {
        String sqlDelete = "DELETE FROM menu WHERE menu_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlDelete);) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    private MenuItem map(ResultSet rs) throws SQLException{
        return new MenuItem(
                rs.getInt( "menu_id"),
                rs.getString("name"),
                FoodType.fromString(rs.getString("type")),
                rs.getDouble("price"),
                rs.getInt("stock"),
                MenuStatus.fromString(rs.getString("status"))
        );
    }
}
