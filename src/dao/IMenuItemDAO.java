package dao;

import model.MenuItem;
import model.User;
import model.enums.FoodType;
import model.enums.UserStatus;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IMenuItemDAO {
    void insert (MenuItem item) throws SQLException;
    MenuItem findById(Connection conn, int id) throws SQLException;
    List<MenuItem> findAll() throws SQLException;
    List<MenuItem> findByType(Connection conn, FoodType foodType) throws SQLException;
    void update(Connection conn, MenuItem item) throws SQLException;
    void delete(Connection conn, int id) throws SQLException;
    boolean updateStock(Connection conn, int menuItemId, int i) throws SQLException;
}
