package service;

import model.User;
import model.enums.Role;

import java.util.List;

public interface IUserService {
    void register(String username, String password) throws Exception;
    User login(String username, String password) throws Exception;
    void createUser(String username, String password, Role role) throws Exception;
    List<User> getAllUser() throws  Exception;
    void banUser(int userId) throws Exception;
}
