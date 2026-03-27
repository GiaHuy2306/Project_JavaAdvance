package service;

import model.MenuItem;
import model.enums.FoodType;
import model.enums.MenuStatus;

import java.util.List;

public interface IMenuItemService {
    void addMenu(String name, FoodType foodType, double price, int stock) throws Exception;

    void updateMenuItem(MenuItem item) throws Exception;

    void delete(int id) throws Exception;

    MenuItem findById(int id) throws Exception;

    List<MenuItem> findByType(FoodType foodType) throws Exception;

    List<MenuItem> findAll() throws Exception;
}
