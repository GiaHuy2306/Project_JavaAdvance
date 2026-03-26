package utils;

public class CreateHash {
    public static void main(String[] args) {
        String password = "123456";
        String hashed = HashPassword.hash(password);
        System.out.println(hashed);
    }
}
