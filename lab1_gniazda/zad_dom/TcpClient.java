package zad_dom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClient {

    private final static ReentrantLock lock = new ReentrantLock();
    private static PrintWriter out;
    private static BufferedReader in;
    private static String nick;
    private static boolean connected;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        Thread msgSender = new Thread(() -> {
            while (true) {
                System.out.println("enter message");
                String msg = scanner.nextLine();
//                if (sendMessage(msg) == -1) {
//                    break;
//                }
                if (!msg.isBlank()) {
                    out.println(nick + ": " + msg);
                    if (msg.equals("disconnect") || msg.equals("dc")) {
                        connected = false;
                        break;
                    }
                }
            }
        });

        System.out.println("enter your nick");
        nick = scanner.nextLine();

        System.out.println("TCP CLIENT STARTED");

        try {
            socket = new Socket("localhost", 12345);
            connected = true;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msgSender.start();

            while (connected) {
//                getMessage();
                System.out.println(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void getMessage() {
        lock.lock();
        try {
            System.out.println(in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static int sendMessage(String msg) {    // returns -1 on disconnect
        lock.lock();
        try {
            System.out.println("starting sending message");
            out.println(nick + ": " + msg);

            if (msg.equals("disconnect") || msg.equals("dc")) {
                connected = false;
                return -1;
            }
            System.out.println("sent message: " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return 0;
    }
}
