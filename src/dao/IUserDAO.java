package dao;

import model.User;
import model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    void insert (User user) throws SQLException;
    User findByUserName(String username) throws SQLException;
    List<User> findAll();
    void updateStatus(int id, UserStatus status) throws SQLException;
}
