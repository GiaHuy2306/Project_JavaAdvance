package dao;

import model.MenuItem;
import model.User;
import model.enums.FoodType;
import model.enums.UserStatus;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public interface IMenuItemDAO {
    void insert (MenuItem item) throws SQLException;
    MenuItem findById(int id) throws SQLException;
    List<MenuItem> findAll() throws SQLException;
    List<MenuItem> findByType(FoodType foodType) throws SQLException;
    void update(MenuItem item) throws SQLException;
    void delete(int id) throws SQLException;
}
