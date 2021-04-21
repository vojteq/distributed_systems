package part_1.commands;

import part_1.SatelliteAPI;

import java.util.Map;

public class Response implements Command {
    public final int queryId;
    public final Map<Integer, SatelliteAPI.Status> map;
    public final int successful;

    public Response(int queryId, Map<Integer, SatelliteAPI.Status> map, int successful) {
        this.queryId = queryId;
        this.map = map;
        this.successful = successful;
    }
}
