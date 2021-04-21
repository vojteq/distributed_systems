package part_2.commands;

import part_2.SatelliteAPI;

public class ResponseOne implements Command {
    public final int satelliteId;
    public final long responseTime;
    public final SatelliteAPI.Status status;
    public final boolean wasSuccessful;

    public ResponseOne(int satelliteId, long responseTime, SatelliteAPI.Status status, boolean wasSuccessful) {
        this.satelliteId = satelliteId;
        this.responseTime = responseTime;
        this.status = status;
        this.wasSuccessful = wasSuccessful;
    }
}
