package service;

import dao.IUserDAO;
import dao.impl.UserDAO;
import model.enums.Role;
import model.User;
import model.enums.UserStatus;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private IUserDAO userDAO = new UserDAO();

    // ================= LOGIN =================
    public User login(String username, String password) throws Exception {

        User user = userDAO.findByUserName(username);

        if (user == null) {
            throw new Exception("Sai tài khoản hoặc mật khẩu");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Sai tài khoản hoặc mật khẩu");
        }

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new Exception("Tài khoản đã bị khóa");
        }

        return user;
    }

    // ================= REGISTER =================
    public void register(String username, String password) throws Exception {

        if (username.isBlank()) {
            throw new Exception("Username không được để trống");
        }

        if (password.length() < 6) {
            throw new Exception("Mật khẩu >= 6 ký tự");
        }

        if (userDAO.existsByUsername(username)) {
            throw new Exception("Username đã tồn tại");
        }

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashed);
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        userDAO.register(user);
    }

    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }
}