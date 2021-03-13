package zad_dom_v2;

import java.util.Random;

public class Admin {
    public static void main(String[] args) {
        String adminId = "admin" + Math.abs(new Random().nextInt() % 100);
    }
}
