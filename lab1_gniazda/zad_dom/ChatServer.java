package zad_dom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer {

    private final static List<Socket> clientSocketList = new ArrayList<>();

    private final static ReentrantLock lock = new ReentrantLock();

    private static DatagramSocket serverUdpSocket;

    public static void main(String[] args) {
        System.out.println("CHAT SERVER");
        ServerSocket serverTcpSocket = null;
        int serverPort = 12345;

        try {
            serverTcpSocket = new ServerSocket(serverPort);
            serverUdpSocket = new DatagramSocket(serverPort);

            new Thread(new UdpController()).start();

            while (true) {
                Socket clientSocket = serverTcpSocket.accept();
                addClientSocket(clientSocket);
                System.out.println("new client connected");

                TcpController tcpController = new TcpController(clientSocket);
                new Thread(tcpController).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverTcpSocket != null) {
                try {
                    serverTcpSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clearSockets();
        }
    }

    private static void clearSockets() {
        lock.lock();
        try {
            for (Socket socket : clientSocketList) {
                if (socket != null) {
                    socket.close();
                }
            }
            clientSocketList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void addClientSocket(Socket socket) {
        lock.lock();
        try {
            clientSocketList.add(socket);
        } finally {
            lock.unlock();
        }
    }

    public static void deleteSocket(Socket socket) {
        lock.lock();
        try {
            clientSocketList.remove(socket);
        } finally {
            lock.unlock();
        }
    }

    public static void sendMessage(String message, Socket senderSocket) {
        lock.lock();
        try {
            for (Socket socket : clientSocketList) {
                if (!socket.equals(senderSocket)) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void sendMessage(String message, int port) {
        lock.lock();
        try {
            byte[] buffer = message.getBytes();
            for (Socket socket : clientSocketList) {
                if(socket.getPort() != port) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), socket.getPort());
                    serverUdpSocket.send(packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static class UdpController implements Runnable {

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    Arrays.fill(buffer, (byte) 0);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    serverUdpSocket.receive(packet);
                    System.out.println("received: " + new String(packet.getData()));
                    ChatServer.sendMessage(new String(packet.getData()), packet.getPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
