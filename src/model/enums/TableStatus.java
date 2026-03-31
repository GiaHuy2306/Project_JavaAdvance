package model.enums;


public enum TableStatus {
    FULL, EMPTY, DELETED;

    public static TableStatus fromString(String name) {
        try {
            return TableStatus.valueOf(name.toUpperCase());
        }catch (Exception e) {
            return EMPTY;
        }
    }
}
