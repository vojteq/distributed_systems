package part_1.commands;

import akka.actor.typed.ActorRef;

public class Begin implements Command {
    public final ActorRef<Command> dispatcher;

    public Begin(ActorRef<Command> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
