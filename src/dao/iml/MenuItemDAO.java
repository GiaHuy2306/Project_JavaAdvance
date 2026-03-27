package dao.iml;


import dao.IMenuItemDAO;
import model.MenuItem;
import model.enums.FoodType;
import model.enums.MenuStatus;
import utils.DBConnection;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO implements IMenuItemDAO {
    @Override
    public void insert(MenuItem item) throws SQLException {
        String sql = "INSERT INTO Menu (name, price, type, stock, status) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, item.getName());
        ps.setDouble(2, item.getPrice());
        ps.setString(3, item.getFoodType().name());
        ps.setInt(4, item.getStock());
        ps.setString(5, item.getStatus().name());
        int count = ps.executeUpdate();

        if (count > 0) {
            System.out.println("Thêm menu item thành công");
        } else {
            System.out.println("Thêm menu item thất bại");
        }
    }

    @Override
    public List<MenuItem> findAll() throws SQLException {
        List<MenuItem> menuItemList = new ArrayList<>();
        String sqlGetAll = "SELECT menu_id, name, price, type, stock, status FROM Menu";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlGetAll);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            menuItemList.add(map(rs));
        }
        return menuItemList;
    }

    @Override
    public MenuItem findById(int id) throws SQLException {
        String sqlSearchId = "SELECT menu_id, name, price, type, stock, status FROM Menu WHERE menu_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlSearchId);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()){
            return map(rs);
        }else {
            System.out.println("Không tìm thấy menu item với id này");
        }
        return null;
    }

    @Override
    public List<MenuItem> findByType(FoodType foodType) throws SQLException {
        String sqlSearchType = "SELECT menu_id, name, price, type, stock, status FROM Menu WHERE type = ? AND status = ?";
        List<MenuItem> menuList = new ArrayList<>();
        try ( Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sqlSearchType);) {

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
    public void update(MenuItem item) throws SQLException {
        String sqlUpdate = "UPDATE Menu SET name = ?, price = ?, type = ?, stock = ?, status = ? WHERE menu_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlUpdate);

        ps.setString(1, item.getName());
        ps.setDouble(2, item.getPrice());
        ps.setString(3, item.getFoodType().name());
        ps.setInt(4, item.getStock());
        ps.setString(5, item.getStatus().name());
        ps.setInt(6, item.getId());

        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sqlDelete = "DELETE FROM Menu WHERE menu_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlDelete);

        ps.setInt(1, id);

        int count = ps.executeUpdate();

        if (count > 0) {
            System.out.println("Xóa menu item thành công");
        }else {
            System.out.println("Xóa menu thất bại");
        }
    }

    private MenuItem map(ResultSet rs) throws SQLException{
        return new MenuItem(
                rs.getInt( "menu_id"),
                rs.getString("name"),
                rs.getDouble("price"),
                FoodType.fromString(rs.getString("type")),
                rs.getInt("stock"),
                MenuStatus.fromString(rs.getString("status"))
        );
    }
}
