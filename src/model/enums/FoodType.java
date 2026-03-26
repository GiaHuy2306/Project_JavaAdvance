package model.enums;

public enum FoodType {
    FOOD, DRINK;

    public static FoodType fromString(String value){
        try {
            return FoodType.valueOf(value.toUpperCase());
        }catch (Exception e){
            return FOOD;
        }
    }
}
