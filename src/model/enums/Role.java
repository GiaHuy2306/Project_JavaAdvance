package model.enums;

public enum Role {
    MANAGER, CHEF, CUSTOMER;

    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return CUSTOMER;
        }
    }
}
