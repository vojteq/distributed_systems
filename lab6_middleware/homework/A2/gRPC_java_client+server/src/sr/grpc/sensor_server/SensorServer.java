package sr.grpc.sensor_server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;

public class SensorServer {

    private final int port = 50051;
    private Server server;

    public static void main(String[] args) {
        SensorServer sensorServer = new SensorServer();
        try {
            sensorServer.start();
            sensorServer.blockUntilShutdown();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void start() throws IOException {
        this.server = ServerBuilder.forPort(port)
                .executor(Executors.newFixedThreadPool(10))
                .addService(new SensorServiceImpl())
                .build()
                .start();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }
}