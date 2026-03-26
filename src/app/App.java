package app;

import model.User;
import presentation.AuthUI;
import presentation.ManagerMenu;

public class App {
    public void run () {
        AuthUI authUI = new AuthUI();
        User user = authUI.start();

        if (user == null) return;

        switch (user.getRole()){
            case MANAGER -> new ManagerMenu().menu(user);
//            case CHEF -> new ChefManagerUI().menu();
//            case CUSTOMER -> new CustomerManagerUI().menu();
        }
    }
}
