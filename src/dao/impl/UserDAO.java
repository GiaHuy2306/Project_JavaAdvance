package dao.impl;

import dao.IUserDAO;
import model.User;
import model.enums.Role;
import model.enums.UserStatus;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (!dbPassword.equals(password)) {
                    return null;
                }

                if (!rs.getBoolean("status")) {
                    throw new RuntimeException("Tài khoản bị khóa");
                }

                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(username);
                user.setRole(Role.fromString(rs.getString("role")));
                user.setStatus(UserStatus.fromString(rs.getString("status")));

                return user;
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return null;
    }

    @Override
    public void register(User user) throws Exception {
        String sql = "INSERT INTO users(username, password, role, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, true);

            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<User> findAll() {
        String sqlGetAll = "SELECT user_id, username, password, role, status FROM Users";
        List<User> userList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()){

            ResultSet rs = stmt.executeQuery(sqlGetAll);

            while(rs.next()){
                userList.add(map(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return  userList;
    }

    @Override
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO Users (username, password, role, status) VALUES (?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole().name());
        ps.setString(4, user.getStatus().name());
        int count = ps.executeUpdate();

        if (count > 0) {
            System.out.println("Thêm user thành công");
        }else {
            System.out.println("Thêm user thất bại");
        }

    }

    @Override
    public User findById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    @Override
    public User findByUserName(String username) throws SQLException {
        String sqlFindUserName = "SELECT user_id, username, password, role, status FROM Users WHERE username = ? ";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlFindUserName);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        if (rs.next()){
            return map(rs);
        }
        return null;
    }

    @Override
    public boolean updateStatus(Connection conn, int user_id, UserStatus status) throws SQLException {
        String sqlUpdateStatus = "UPDATE Users SET status = ? WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStatus);) {
            ps.setString(1, status.name());
            ps.setInt(2, user_id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int countActiveManagers(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'MANAGER' AND status != 'BANNED'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                Role.fromString(rs.getString("role")),
                UserStatus.fromString(rs.getString("status"))
        );
    }
}
