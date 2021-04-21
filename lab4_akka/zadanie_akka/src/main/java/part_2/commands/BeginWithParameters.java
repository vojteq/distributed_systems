package part_2.commands;

import akka.actor.typed.ActorRef;

public class BeginWithParameters implements Command {
    public final ActorRef<Command> dispatcher;
    public final ActorRef<Command> database;
    public final int firstSatelliteId;
    public final int range;
    public final int timeout;

    public BeginWithParameters(ActorRef<Command> dispatcher, ActorRef<Command> database, int firstSatelliteId, int range, int timeout) {
        this.dispatcher = dispatcher;
        this.database = database;
        this.firstSatelliteId = firstSatelliteId;
        this.range = range;
        this.timeout = timeout;
    }
}
