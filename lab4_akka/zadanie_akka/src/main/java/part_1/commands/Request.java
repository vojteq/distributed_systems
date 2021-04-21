package part_1.commands;

import akka.actor.typed.ActorRef;

public class Request implements Command {
    public final ActorRef<Command> replyTo;
    public final int queryId;
    public final int firstSatelliteId;
    public final int range;
    public final int timeout;

    public Request(ActorRef<Command> replyTo, int queryId, int firstSatelliteId, int range, int timeout) {
        this.replyTo = replyTo;
        this.queryId = queryId;
        this.firstSatelliteId = firstSatelliteId;
        this.range = range;
        this.timeout = timeout;
    }
}
