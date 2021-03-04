package zad3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String args[]) {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int serverPortNumber = 9008;

        try {
            socket = new DatagramSocket(serverPortNumber);
            byte[] buffer = new byte[1024];

            while (true) {
                Arrays.fill(buffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                ByteBuffer byteBuffer = ByteBuffer.wrap(receivePacket.getData());
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int result = byteBuffer.getInt();
                System.out.println("received msg: " + result);

                InetAddress address = receivePacket.getAddress();
                int clientPortNumber = receivePacket.getPort();
                System.out.println("client port: " + clientPortNumber);
                byte[] sendBuff = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(result + 1).array();
                DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, address, clientPortNumber);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
