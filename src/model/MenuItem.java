package model;

import model.enums.FoodType;
import model.enums.MenuStatus;

public class MenuItem {
    private int id;
    private String name;
    private double price;
    private FoodType foodType;
    private int stock;
    private MenuStatus status;

    public MenuItem(int id, String name, FoodType foodType, double price, int stock, MenuStatus status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.foodType = foodType;
        this.stock = stock;
        this.status = status;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void updateStock(int newStock){
        this.stock = newStock;
        this.status = (newStock > 0) ? MenuStatus.AVAILABLE : MenuStatus.OUT_OF_STOCK;
    }

    public  MenuStatus getStatus() {return  status;}

    public void setStatus(MenuStatus status) {this.status = status;}

    public void setStock(int i) {
        this.stock = i;
        this.status = (i > 0) ? MenuStatus.AVAILABLE : MenuStatus.OUT_OF_STOCK;
    }
}
