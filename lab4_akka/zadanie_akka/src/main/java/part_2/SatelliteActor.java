package part_2;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_2.commands.*;

import java.util.Date;

public class SatelliteActor extends AbstractBehavior<Command> {

    public SatelliteActor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(SatelliteActor::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestOne.class, this::onRequestOne)
                .build();
    }

    private Behavior<Command> onRequestOne(RequestOne requestOne) {
        long startTime, responseTime;
        startTime = new Date().getTime();
        SatelliteAPI.Status status = SatelliteAPI.getStatus(requestOne.satelliteId);
        responseTime = new Date().getTime() - startTime;
        requestOne.replyTo.tell(new ResponseOne(
                requestOne.satelliteId,
                responseTime,
                status,
                responseTime <= requestOne.timeout));
        return Behaviors.stopped();
    }
}
