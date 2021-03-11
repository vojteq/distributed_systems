package zad1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String args[]) {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int serverPortNumber = 9008;

        try{
            socket = new DatagramSocket(serverPortNumber);
            byte[] buffer = new byte[1024];

            while(true) {
                Arrays.fill(buffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println("received msg: " + msg);

                InetAddress address = receivePacket.getAddress();
                int clientPortNumber = receivePacket.getPort();
                byte[] bufferRes = ("<server> response to " + clientPortNumber).getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bufferRes, bufferRes.length, address, clientPortNumber);
                socket.send(sendPacket);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
