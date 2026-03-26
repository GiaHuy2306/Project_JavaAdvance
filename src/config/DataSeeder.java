package config;

import dao.iml.UserDAO;
import model.User;
import model.enums.Role;
import model.enums.UserStatus;
import utils.HashPassword;

public class DataSeeder {

    public static void seed() {
        try {
            UserDAO dao = new UserDAO();

            // Manager
            if (dao.findByUserName("manager") == null) {
                String pass = HashPassword.hash("123456");
                dao.insert(new User(0, "manager", pass, Role.MANAGER, UserStatus.ACTIVE));
            }

            // Chef
            if (dao.findByUserName("chef") == null) {
                String pass = HashPassword.hash("123456");
                dao.insert(new User(0, "chef", pass, Role.CHEF, UserStatus.ACTIVE));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}