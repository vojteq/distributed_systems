package zad4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpClient {

    public static void main(String args[]) throws Exception
    {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
        int portNumber = 9008;
        int myPortNumber = 9009;

        try {
            socket = new DatagramSocket(myPortNumber);
            InetAddress address = InetAddress.getByName("localhost");
            byte[] buffer = "msg from java client".getBytes();

            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, portNumber);
            socket.send(sendPacket);

            buffer = new byte[1024];
            Arrays.fill(buffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);
            String response = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + response);
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
