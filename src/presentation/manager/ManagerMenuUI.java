package presentation.manager;

import model.User;
import utils.InputMethod;

public class ManagerMenuUI {
    private UserManagerUI userUI = new UserManagerUI();
    private MenuItemUI menuUI = new MenuItemUI();
    private TableUI tableUI = new TableUI();

    public boolean menu() {
        while (true) {
            String separator = "+-----------------------------------+";

            System.out.println("\n" + separator);
            System.out.println("|           MANAGER MENU            |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. Quản lý User");
            System.out.printf("| %-33s |\n", "2. Quản lý Menu");
            System.out.printf("| %-33s |\n", "3. Quản lý Bàn");
            System.out.printf("| %-33s |\n", "0. Thoát");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn: ");

            switch (choice) {
                case 1 -> userUI.menu();
                case 2 -> menuUI.menu();
                case 3 -> tableUI.menu();
                case 0 -> {
                    boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                    if (confirm){
                        return true;
                    }
                }
            }
        }
    }
}
