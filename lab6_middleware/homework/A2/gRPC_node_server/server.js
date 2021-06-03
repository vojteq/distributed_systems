const PROTO = "./sensor.proto";
const grpc = require('grpc');
const protoLoader = require('@grpc/proto-loader');

let packageDef = protoLoader.loadSync(
    PROTO,
    {
        keepCase: true,
        longs: String,
        enums: String,
        defaults: true,
        oneofs: true
    }
);

let sensorProto = grpc.loadPackageDefinition(packageDef).Sensors;

function main() {
    let server = new grpc.Server();
    server.addService(sensorProto.SensorService.service, { SensorMeasurement: readMeasurements });
    server.bind('127.0.0.2:50051', grpc.ServerCredentials.createInsecure());
    console.log('server listenning at 127.0.0.2:50051');
    server.start();
}

main();

function readMeasurements(call, callback) {
    call.on('data', function (stream) {
        let measurementPackage = stream.measurement;
        // console.log(JSON.stringify(measurementPackage));
        console.log("\nRECEIVED MEASUREMENTS PACKAGE")
        for (i = 0; i < measurementPackage.length; i++) {
            let measurement = measurementPackage[i];
            // console.log(JSON.stringify(measurement));
            if (measurement.hasOwnProperty("additionalData") && measurement.additionalData != null) {
                if (measurement.additionalData.type_url === "type.googleapis.com/Sensors.TemperatureAdditionalData") {
                    let additional = new Uint8Array(measurement.additionalData.value);
                    let unit = new TextDecoder().decode(additional.slice(2));
                    console.log("owner: " + measurement.owner + ", type: " + measurement.type + ", value: " + measurement.value + unit);
                }
            } else {
                console.log("owner: " + measurement.owner + ", type: " + measurement.type + ", value: " + measurement.value);
            }
        }
    });
}