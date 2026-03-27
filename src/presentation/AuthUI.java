package presentation;

import model.User;
import service.IUserService;
import service.iml.UserService;
import utils.InputMethod;

public class AuthUI {

    private IUserService userService = new UserService();

    public User start() {
        while (true) {
            try {
                System.out.println("\n===== RESTAURANT SYSTEM =====");
                System.out.println("1. Đăng ký");
                System.out.println("2. Đăng nhập");

                int choice = InputMethod.inputInt("Chọn: ");

                switch (choice) {

                    case 1 -> {
                        System.out.println("\n===== Đăng ký =====");
                        String username = InputMethod.inputString("Nhập username: ");
                        String password = InputMethod.inputPassword("Nhập password: ");
                        userService.register(username, password);
                        System.out.println("Đăng ký thành công!");
                    }

                    case 2 -> {
                        System.out.println("\n===== Đăng nhập =====");
                        String username = InputMethod.inputString("Nhập username: ");
                        String password = InputMethod.inputPassword("Nhập password: ");
                        User user = userService.login(username, password);
                        System.out.println("Đăng nhập thành công!");
                        return user;
                    }

                    default -> System.out.println("Lựa chọn không hợp lệ");
                }

            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }
}