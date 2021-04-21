package part_2.commands;

public class IncrementRequest implements Command {
    public final int satelliteId;

    public IncrementRequest(int satelliteId) {
        this.satelliteId = satelliteId;
    }
}
