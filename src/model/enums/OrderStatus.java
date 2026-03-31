package model.enums;

public enum OrderStatus {
    PENDING, IN_PROGRESS, DONE, CANCEL, DELETE;

    public static OrderStatus fromString(String value){
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        }catch (Exception e){
            return PENDING;
        }
    }
}
