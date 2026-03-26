package model.enums;

public enum UserStatus {
    ACTIVE, BANNED;

    public static UserStatus fromString(String value) {
        try {
            return UserStatus.valueOf(value.toUpperCase());
        }catch (Exception e){
            return  ACTIVE;
        }
    }
}
