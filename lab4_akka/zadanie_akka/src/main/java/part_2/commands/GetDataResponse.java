package part_2.commands;

public class GetDataResponse implements Command {
    public final int satelliteId;
    public final int issues;

    public GetDataResponse(int satelliteId, int issues) {
        this.satelliteId = satelliteId;
        this.issues = issues;
    }
}
