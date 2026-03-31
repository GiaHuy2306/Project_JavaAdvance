package presentation.manager;

import model.User;
import service.AuthService;
import utils.InputMethod;

public class AuthUI {

    private AuthService authService = new AuthService();

    public User start() {
        while (true) {
            System.out.println("\n===== RESTAURANT SYSTEM =====");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký");
            System.out.println("0. Thoát");

            int choice = InputMethod.inputInt("Chọn: ");

            switch (choice) {
                case 1:
                    User user = login();
                    if (user != null) return user;
                    break;

                case 2:
                    register();
                    break;

                case 0:
                    return null;
            }
        }
    }

    private User login() {
        String username = InputMethod.inputString("Username: ");
        String password = InputMethod.inputString("Password: ");

        try {
            User user = authService.login(username, password);
            System.out.println("Đăng nhập thành công");
            return user;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void register() {
        String username;
        while (true) {
            username = InputMethod.inputString("Nhập username: ").trim();

            if (username.isEmpty()) {
                System.out.println("Không được để trống");
                continue;
            }

            if (authService.existsByUsername(username)) {
                System.out.println("Username đã tồn tại");
                continue;
            }

            break;
        }

        String password;
        while (true) {
            password = InputMethod.inputString("Nhập password: ");

            if (password.length() < 6) {
                System.out.println("Mật khẩu >= 6 ký tự");
                continue;
            }

            break;
        }

        try {
            authService.register(username, password);
            System.out.println("Đăng ký thành công");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}