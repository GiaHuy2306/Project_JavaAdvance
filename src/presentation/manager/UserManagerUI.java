package presentation.manager;

import model.User;
import model.enums.Role;
import service.IUserService;
import service.impl.UserService;
import utils.InputMethod;

import java.util.List;

public class UserManagerUI {
    private IUserService service = new UserService();

    public void menu(){
        while (true){
            try {
                String separator = "+-----------------------------------+";

                System.out.println("\n" + separator);
                System.out.println("|          USER MANAGEMENT          |");
                System.out.println(separator);
                System.out.printf("| %-33s |\n", "1. Tạo tài khoản");
                System.out.printf("| %-33s |\n", "2. Danh sách user");
                System.out.printf("| %-33s |\n", "3. Ban user");
                System.out.printf("| %-33s |\n", "0. Đăng xuất");
                System.out.println(separator);

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
                        boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                        if (confirm){
                            return;
                        }
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

        String separator = "+-----------------------------------+";

        System.out.println("\n" + separator);
        System.out.println("|             CHỌN ROLE             |");
        System.out.println(separator);
        System.out.printf("| %-33s |\n", "1. CHEF");
        System.out.printf("| %-33s |\n", "2. MANAGER");
        System.out.println(separator);

        int choice = InputMethod.inputInt("Chọn (1 or 2): ");

        Role role = (choice == 1) ? Role.CHEF : Role.MANAGER;

        service.createUser(username, password, role);
    }

    private void showUsers() throws Exception {
        List<User> list = service.getAllUser();

        if (list.isEmpty()) {
            System.out.println("Danh sách người dùng trống.");
            return;
        }

        String separator = "+-------+-----------------+------------+------------+";

        System.out.println(separator);
        System.out.printf("| %-5s | %-15s | %-10s | %-10s |\n", "ID", "Username", "Role", "Status");
        System.out.println(separator);

        for (User u : list) {
            System.out.printf("| %-5d | %-15s | %-10s | %-10s |\n",
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getStatus());
        }
        System.out.println(separator);
    }

    private void banUser() throws Exception {
        int id = InputMethod.inputInt("Nhập ID user: ");

        if (InputMethod.inputConfirm("Bạn có chắn chắn muốn ban ?")) {
            service.banUser(id);
            System.out.println("Ban user thành công");
        }
    }
}
