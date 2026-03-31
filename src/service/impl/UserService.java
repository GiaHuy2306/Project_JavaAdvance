package service.impl;

import dao.impl.UserDAO;
import model.User;
import model.enums.Role;
import model.enums.UserStatus;
import service.IUserService;
import utils.DBConnection;
import utils.HashPassword;

import java.sql.Connection;
import java.util.List;

public class UserService implements IUserService {
    private UserDAO dao = new UserDAO();
    @Override
    public void register(String username, String password) throws Exception {
        if (dao.findByUserName(username) != null){
            throw new Exception("Username đã tồn tại");
        }

        if (password.trim().isEmpty()){
            throw new Exception("Mật khẩu không được trống");
        }

        String hashed = HashPassword.hash(password);

        User user = new User(0,username, hashed, Role.CUSTOMER, UserStatus.ACTIVE);

        dao.insert(user);
    }

    @Override
    public User login(String username, String password) throws Exception {
        User user = dao.findByUserName(username);

        if (user == null) throw new Exception("Chưa có username này");

        if (user.getStatus().equals(UserStatus.BANNED)){
            throw new Exception("Tài khoản đã bị khóa");
        }

        if (user.getRole() != Role.MANAGER) {
            throw new Exception("Không có quyền!");
        }

        if (!HashPassword.verify(password, user.getPassword())) {
            throw new Exception("Sai mật khẩu");
        }
        return user;
    }

    @Override
    public void createUser(String username, String password, Role role) throws Exception {
        if (dao.findByUserName(username) != null){
            throw new Exception("User name đã tồn tại");
        }

        String hashed = HashPassword.hash(password);

        User user = new User(0, username, hashed, role, UserStatus.ACTIVE);
        dao.insert(user);
    }

    @Override
    public List<User> getAllUser() throws Exception {
        return dao.findAll();
    }

    // Sửa trong UserService.java
    @Override
    public void banUser(int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            // 1. Tìm thông tin người dùng đang định khóa
            User targetUser = dao.findById(conn, userId);

            if (targetUser == null) {
                throw new Exception("Không tìm thấy người dùng này trong hệ thống!");
            }

            if (targetUser.getStatus() == UserStatus.BANNED) {
                throw new Exception("Tài khoản này đã bị khóa từ trước rồi!");
            }

            if (targetUser.getRole() == Role.MANAGER) {
                int activeManagerCount = dao.countActiveManagers(conn);

                if (activeManagerCount <= 1) {
                    throw new Exception("BẢO MẬT: Đây là Quản lý duy nhất còn lại của hệ thống. Bạn không thể khóa tài khoản này!");
                }
            }

            // 3. Vượt qua mọi bài kiểm tra thì mới tiến hành khóa
            boolean isSuccess = dao.updateStatus(conn, userId, UserStatus.BANNED);

            if (!isSuccess) {
                throw new Exception("Khóa tài khoản thất bại do lỗi CSDL.");
            }

        }
    }
}
