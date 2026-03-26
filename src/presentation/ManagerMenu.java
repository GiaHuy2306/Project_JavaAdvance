package presentation;

import model.User;
import utils.InputMethod;

public class ManagerMenu {
    private UserManagerUI userUI = new UserManagerUI();
//    private MenuItemUI menuUI = new MenuItemUI();
//    private TableUI tableUI = new TableUI();

    public void menu(User user) {
        while (true) {
            System.out.println("\n===== MANAGER MENU =====");
            System.out.println("1. Quản lý User");
            System.out.println("2. Quản lý Menu");
            System.out.println("3. Quản lý Bàn");
            System.out.println("0. Thoát");

            int choice = InputMethod.inputInt("Chọn: ");

            switch (choice) {
                case 1 -> userUI.menu();
//                case 2 -> menuUI.menu();
//                case 3 -> tableUI.menu();
                case 0 -> { return; }
            }
        }
    }
}
