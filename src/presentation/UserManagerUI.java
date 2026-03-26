package presentation;

import model.User;
import model.enums.Role;
import service.IUserService;
import service.iml.UserService;
import utils.InputMethod;

import java.util.List;

public class UserManagerUI {
    private IUserService service = new UserService();

    public void menu(){
        while (true){
            try {
                System.out.println("\n===== USER MANAGEMENT =====");
                System.out.println("1. Tạo tài khoản");
                System.out.println("2. Danh sách user");
                System.out.println("3. Ban user");
                System.out.println("0. Thoát");

                int choice = InputMethod.inputInt("Chọn: ");

                switch (choice){
                    case 1 -> {
                        createUser();
                    }
                    case 2 -> {
                        showUsers();
                    }
                    case 3 -> {
                        banUser();
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Lựa chọn ko hợp lệ");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    private void createUser() throws Exception {
        String username = InputMethod.inputString("Username: ");
        String password = InputMethod.inputPassword("Password: ");

        System.out.println("Chọn role: ");
        System.out.println("1. CHEF");
        System.out.println("2. MANAGER");

        int choice = InputMethod.inputInt("Chọn: ");

        Role role = (choice == 1) ? Role.CHEF : Role.MANAGER;

        service.createUser(username, password, role);
    }

    private void showUsers() throws Exception {
        List<User> list = service.getAllUser();

        System.out.printf("%-5s %-15s %-10s %-10s\n", "ID", "Username", "Role", "Status");
        for (User u : list) {
            System.out.printf("%-5d %-15s %-10s %-10s\n",
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getStatus());
        }
    }

    private void banUser() throws Exception {
        int id = InputMethod.inputInt("Nhập ID user: ");

        if (InputMethod.inputConfirm("Bạn có chắn chắn muốn ban ?")) {
            service.banUser(id);
            System.out.println("Ban user thành công");
        }
    }
}
