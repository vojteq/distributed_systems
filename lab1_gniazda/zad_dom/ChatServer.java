package zad_dom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer {

    private final static List<Socket> clientSocketList = new ArrayList<>();

    private final static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        System.out.println("CHAT SERVER");
        ServerSocket serverSocket = null;
        int serverPort = 12345;

        try {
            serverSocket = new ServerSocket(serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                addClientSocket(clientSocket);
                System.out.println("new client connected");

                TcpConnection tcpConnection = new TcpConnection(clientSocket);
                new Thread(tcpConnection).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
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

    public static List<Socket> getClonedClientSocketList() {
        lock.lock();
        try {
            return new ArrayList<>(clientSocketList);
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
}
