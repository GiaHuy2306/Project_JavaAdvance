package service.impl;

import dao.impl.UserDAO;
import model.User;
import model.enums.Role;
import model.enums.UserStatus;
import service.IUserService;
import utils.HashPassword;

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

    @Override
    public void banUser(int userId) throws Exception {
        dao.updateStatus(userId, UserStatus.BANNED);
    }
}
