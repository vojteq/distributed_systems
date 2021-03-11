package zad_dom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TcpController implements Runnable {

    private final Socket clientSocket;


    private boolean connected;

    public TcpController(Socket clientSocket) {
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
//                    message = "(sender port: " + clientSocket.getPort() + ") " + message;
                    ChatServer.sendMessage(message, clientSocket);
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