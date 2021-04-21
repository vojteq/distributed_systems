package part_2.commands;

import akka.actor.typed.ActorRef;

public class RequestOne implements Command {
    public final int satelliteId;
    public final long timeout;
    public final ActorRef<Command> replyTo;

    public RequestOne(ActorRef<Command> replyTo, int satelliteId, long timeout) {
        this.replyTo = replyTo;
        this.satelliteId = satelliteId;
        this.timeout = timeout;
    }
}
