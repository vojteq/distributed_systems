package part_2.commands;

import akka.actor.typed.ActorRef;

public class GetDataRequest implements Command {
    public final ActorRef<Command> actorRef;
    public final int satelliteId;

    public GetDataRequest(ActorRef<Command> actorRef, int satelliteId) {
        this.actorRef = actorRef;
        this.satelliteId = satelliteId;
    }
}
