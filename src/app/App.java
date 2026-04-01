package app;

import model.User;
import presentation.chef.ChefUI;
import presentation.customer.CustomerUI;
import presentation.manager.AuthUI;
import presentation.manager.ManagerMenuUI;

public class App {
    public void run () {
        AuthUI authUI = new AuthUI();

        while (true) {
            User user = authUI.start();

            if (user == null) {
                System.out.println("Thoát hệ thống.");
                break;
            }

            boolean backToMainMenu = false;

            switch (user.getRole()) {
                case MANAGER:
                    backToMainMenu = new ManagerMenuUI().menu();
                    break;
                case CHEF:
                    backToMainMenu = new ChefUI().menu();
                    break;
                case CUSTOMER:
                    backToMainMenu = new CustomerUI().menu(user.getId());
                    break;
            }

            if (backToMainMenu) {
//                System.out.println("Quay về menu chính...");
                continue;
            }
        }
    }
}
