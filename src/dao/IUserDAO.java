package dao;

import model.User;
import model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    void insert (User user) throws SQLException;
    User findById(Connection conn, int id) throws SQLException;
    User findByUserName(String username) throws SQLException;

    User login(String username, String password);

    void register(User user) throws Exception;

    boolean existsByUsername(String username);

    List<User> findAll();
    boolean updateStatus(Connection conn, int id, UserStatus status) throws SQLException;

    int countActiveManagers(Connection conn) throws SQLException;
}
