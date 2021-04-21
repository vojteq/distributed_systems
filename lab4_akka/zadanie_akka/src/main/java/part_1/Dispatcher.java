package part_1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_1.commands.Command;
import part_1.commands.Request;

import java.util.Random;

public class Dispatcher extends AbstractBehavior<Command> {

    public Dispatcher(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Dispatcher::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::onRequest)
                .build();
    }

    private Behavior<Command> onRequest(Request request) {
        getContext()
                .spawn(SatelliteActor.create(), "satelliteActor" + Math.abs(new Random().nextInt() % 10000))
                .tell(request);
        return this;
    }
}
