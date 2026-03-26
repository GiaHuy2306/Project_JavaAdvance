package model.enums;

public enum OrderItemStatus {
    PENDING, COOKING, READY, SERVED;

    public static OrderItemStatus fromString(String value){
        try{
            return OrderItemStatus.valueOf(value.toUpperCase());
        }catch (Exception e){
            return PENDING;
        }
    }
}
