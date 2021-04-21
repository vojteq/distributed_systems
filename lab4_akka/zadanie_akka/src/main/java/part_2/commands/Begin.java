package part_2.commands;

import akka.actor.typed.ActorRef;

public class Begin implements Command {
    public final ActorRef<Command> dispatcher;
    public final ActorRef<Command> database;

    public Begin(ActorRef<Command> dispatcher, ActorRef<Command> database) {
        this.dispatcher = dispatcher;
        this.database = database;
    }
}
