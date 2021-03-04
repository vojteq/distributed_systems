package zad4;

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
                InetAddress address = receivePacket.getAddress();
                int clientPortNumber = receivePacket.getPort();
                byte[] sendBuff = new byte[0];
                System.out.println("client port: " + clientPortNumber);

                String msg = new String(receivePacket.getData());
                System.out.println("received msg: " + msg);

                if (msg.contains("java")) {
                    sendBuff = "Pong Java".getBytes();
                } else if (msg.contains("python")) {
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    sendBuff = "Pong Python".getBytes();
                    sendBuff = ByteBuffer.allocate(sendBuff.length).order(ByteOrder.LITTLE_ENDIAN).put(sendBuff).array();
                }

                DatagramPacket datagramPacket = new DatagramPacket(sendBuff, sendBuff.length, address, clientPortNumber);
                socket.send(datagramPacket);
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
