package model.enums;

public enum OrderStatus {
    OPEN, CLOSED;

    public static OrderStatus fromString(String value){
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        }catch (Exception e){
            return OPEN;
        }
    }
}
