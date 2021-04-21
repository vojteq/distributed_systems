package part_1.commands;

import akka.actor.typed.ActorRef;

public class BeginWithParameters implements Command {
    public final ActorRef<Command> dispatcher;
    public final int firstSatelliteId;
    public final int range;
    public final int timeout;

    public BeginWithParameters(ActorRef<Command> dispatcher, int firstSatelliteId, int range, int timeout) {
        this.dispatcher = dispatcher;
        this.firstSatelliteId = firstSatelliteId;
        this.range = range;
        this.timeout = timeout;
    }
}
