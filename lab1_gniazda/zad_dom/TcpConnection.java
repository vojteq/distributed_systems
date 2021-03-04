package zad_dom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class TcpConnection implements Runnable {

    private final Socket clientSocket;


    private boolean connected;

    public TcpConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        connected = true;
    }

    @Override
    public void run() {
        while (connected) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = in.readLine();
                System.out.println("(TcpConnection): " + message);
                if (message.equals("disconnect") || message.equals("dc")) {
                    stop();
                } else {
                    message = "(sender port: " + clientSocket.getPort() + ") " + message;
                    List<Socket> clients = ChatServer.getClonedClientSocketList();
                    for (Socket socket : clients) {
                        if (!socket.equals(clientSocket)) {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(message);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stop() {
        connected = false;
        try {
            ChatServer.deleteSocket(clientSocket);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}