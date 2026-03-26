package presentation;

import app.App;
import config.DataSeeder;

public class Main {
    public static void main(String[] args) {
        DataSeeder.seed();
        new App().run();
    }
}