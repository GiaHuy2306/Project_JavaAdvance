package model;

import model.enums.FoodType;

public class MenuItem {
    private int id;
    private String name;
    private double price;
    private FoodType foodType;
    private int stock;

    public MenuItem(int id, String name, double price, FoodType foodType, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.foodType = foodType;
        this.stock = stock;
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

    public void setStock(int stock) {
        this.stock = stock;
    }
}
