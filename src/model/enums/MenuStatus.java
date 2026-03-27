package model.enums;

public enum MenuStatus {
    AVAILABLE,
    OUT_OF_STOCK;

    public static MenuStatus fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        }catch (Exception e) {
            return  MenuStatus.AVAILABLE;
        }
    }
}
