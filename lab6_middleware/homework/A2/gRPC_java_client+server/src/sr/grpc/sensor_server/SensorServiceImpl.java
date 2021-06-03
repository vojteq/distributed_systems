package sr.grpc.sensor_server;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import sensors.*;

public class SensorServiceImpl extends SensorServiceGrpc.SensorServiceImplBase {

    @Override
    public StreamObserver<MeasurementsPackage> sensorMeasurement(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<MeasurementsPackage>() {

            private int counter = 0;

            @Override
            public void onNext(MeasurementsPackage measurementsPackage) {
                System.out.println("\n\n" + counter + " RECEIVED MEASUREMENTS PACKAGE:");
                for (int i = 0; i < measurementsPackage.getMeasurementCount(); i++) {
                    Measurement measurement = measurementsPackage.getMeasurement(i);
                    if (measurement.hasAdditionalData()) {
                        Any additionalData = measurement.getAdditionalData();
                        if (additionalData.is(TemperatureAdditionalData.class)) {
                            try {
                                TemperatureAdditionalData temperatureAdditionalData = additionalData.unpack(TemperatureAdditionalData.class);
                                System.out.println(i + "owner: " + measurement.getOwner() + ", type: " + measurement.getType() + ", value: " + measurement.getValue() + temperatureAdditionalData.getUnit());
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println(i + "owner: " + measurement.getOwner() + ", type: " + measurement.getType() + ", value: " + measurement.getValue());
                    }
                }
                counter++;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
            }
        };
    }
}
