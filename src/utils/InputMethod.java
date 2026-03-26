package utils;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Scanner;

public class InputMethod {

    private static final Scanner sc = new Scanner(System.in);

    // ===== STRING =====
    public static String inputString(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Không được để trống!");
            } else {
                return input;
            }
        }
    }

    // ===== INT =====
    public static int inputInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Phải nhập số nguyên!");
            }
        }
    }

    // ===== DOUBLE =====
    public static double inputDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(sc.nextLine());
                if (value <= 0) {
                    System.out.println("Phải > 0!");
                } else {
                    return value;
                }
            } catch (Exception e) {
                System.out.println("Phải nhập số!");
            }
        }
    }

    // ===== PASSWORD =====
    public static String inputPassword(String message) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            return reader.readLine(message, '*');
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // ===== CONFIRM (Y/N) =====
    public static boolean inputConfirm(String message) {
        while (true) {
            System.out.print(message + " (y/n): ");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println("Chỉ nhập y hoặc n!");
        }
    }
}