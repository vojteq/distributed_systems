package sr.grpc.sensor_client;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import sensors.*;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class SensorClient {

    private final ManagedChannel channel;

    private final SensorServiceGrpc.SensorServiceStub sensorServiceStub;

    public SensorClient(String remoteHost, int remotePort) {
        channel = ManagedChannelBuilder.forAddress(remoteHost, remotePort)
                .usePlaintext()
                .build();

        sensorServiceStub = SensorServiceGrpc.newStub(channel);
    }

    public static void main(String[] args) {
        SensorClient sensorClient = new SensorClient("127.0.0.2", 50051);
        sensorClient.start();
    }

    private void start() {
        StreamObserver<Empty> emptyMStreamObserver = getEmptyMessageStreamObserver();
        StreamObserver<MeasurementsPackage> packageStream = sensorServiceStub.sensorMeasurement(emptyMStreamObserver);
        LinkedBlockingQueue<Measurement> queue = new LinkedBlockingQueue<>();
        Random random = new Random();
        Thread packageSender = new Thread(new DataSender(queue, packageStream));
        packageSender.start();

        String owner = "owner_" + new Random().nextInt(100);

        for (int i = 0; i < 8; i++) {
            SensorType sensorType = SensorType.forNumber(random.nextInt(3));
            int value = random.nextInt(100);
            Measurement.Builder builder = Measurement.newBuilder()
                    .setOwner(owner)
                    .setType(sensorType)
                    .setValue(value);
            if (sensorType.equals(SensorType.TEMPERATURE)) {
                builder.setAdditionalData(Any.pack(TemperatureAdditionalData.newBuilder().setUnit("C").build()));
            }
            Measurement measurement = builder.build();
            queue.add(measurement);
            sleep(2);
        }
        sleep(5);
        packageSender.interrupt();
    }

    private void sleep(long s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private StreamObserver<Empty> getEmptyMessageStreamObserver() {
        return new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty emptyM) {
                // nothing
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error (emptyMStreamObserver");
            }

            @Override
            public void onCompleted() {
                System.out.println("finished");
            }
        };
    }
}

class DataSender implements Runnable {

    private final LinkedBlockingQueue<Measurement> queue;
    private final StreamObserver<MeasurementsPackage> outputStream;
    private final long timeToWait = 1000 * 10;

    public DataSender(LinkedBlockingQueue<Measurement> queue, StreamObserver<MeasurementsPackage> outputStream) {
        this.queue = queue;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        long time = new Date().getTime();
        MeasurementsPackage.Builder builder = MeasurementsPackage.newBuilder();
        while (!Thread.currentThread().isInterrupted()) {
            if (new Date().getTime() - time > this.timeToWait && builder.getAllFields().size() > 0) {
                MeasurementsPackage measurementsPackage = builder.build();
                outputStream.onNext(measurementsPackage);
                time = new Date().getTime();
                builder = MeasurementsPackage.newBuilder();
            }
            if (queue.size() > 0) {
                try {
                    builder.addMeasurement(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        outputStream.onCompleted();
    }
}
