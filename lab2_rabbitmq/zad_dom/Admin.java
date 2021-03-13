package zad_dom;

import java.util.Random;

public class Admin {
    public static void main(String[] args) {
        String adminId = "admin" + Math.abs(new Random().nextInt() % 100);
    }
}
