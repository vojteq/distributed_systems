package zad_dom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Arrays;

public class ChatClient {

    private static PrintWriter tcpOut;
    private static BufferedReader tcpIn;
    private static BufferedReader reader;
    private static String nick;
    private static boolean connected;
    private static Socket tcpSocket = null;
    private static DatagramSocket udpSocket = null;
    private static MulticastSocket multicastSocket = null;
    private static int serverPort = 12345;
    private static int multicastPort = 12350;
    private static String host = "localhost";
    private static String group = "230.0.0.0";

    public static void main(String[] args) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        Thread msgSender = new Thread(new ClientMsgSender());
        Thread udpReceiver = new Thread(new UdpReceiver());
        Thread multicatsReceiver = new Thread(new MulticastReceiver());

        System.out.println("enter your nick");
        try {
            nick = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("TCP CLIENT STARTED");

        try {
            tcpSocket = new Socket(InetAddress.getByName(host), serverPort);
            udpSocket = new DatagramSocket(tcpSocket.getLocalPort());
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(InetAddress.getByName(group));
            connected = true;
            tcpOut = new PrintWriter(tcpSocket.getOutputStream(), true);
            tcpIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            msgSender.start();
            udpReceiver.start();
            multicatsReceiver.start();

            while (connected) {
                System.out.println(tcpIn.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tcpSocket != null) {
                try {
                    tcpSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (udpSocket != null) {
                udpSocket.close();
            }
        }
    }

    private static class ClientMsgSender implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] buffer;
                    DatagramPacket datagramPacket;
                    System.out.println("enter message (U to use UDP | M to use Multicast)");
                    String msg = reader.readLine();
                    if (!msg.isBlank()) {
                        switch (msg) {
                            case "U":
                                System.out.println("enter udp msg");
                                msg = reader.readLine();
                                buffer = ("[UDP] " + nick + ": " + msg).getBytes();
                                datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(host), serverPort);
                                udpSocket.send(datagramPacket);
                                continue;
                            case "M":
                                System.out.println("enter multicast msg");
                                msg = reader.readLine();
                                buffer = ("[M] " + nick + ": " + msg).getBytes();
                                datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(group), multicastPort);     // TODO co z tym portem
                                udpSocket.send(datagramPacket);
                                continue;
                            default:
                                tcpOut.println("[TCP] " + nick + ": " + msg);
                                if (msg.equals("disconnect") || msg.equals("dc")) {
                                    connected = false;
                                    break;
                                }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class UdpReceiver implements Runnable {
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (connected) {
                    Arrays.fill(buffer, (byte) 0);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);
                    System.out.println(new String(packet.getData()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MulticastReceiver implements Runnable {
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (connected) {
                    Arrays.fill(buffer, (byte) 0);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);
                    if (packet.getPort() != udpSocket.getLocalPort()) {
                        System.out.println(new String(packet.getData()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
