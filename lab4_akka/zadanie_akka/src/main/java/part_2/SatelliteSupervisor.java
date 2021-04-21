package part_2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_2.commands.*;

import java.util.HashMap;
import java.util.Map;

public class SatelliteSupervisor extends AbstractBehavior<Command> {

    private ActorRef<Command> replyTo;
    private int queryId;
    private int range;

    private int counter;
    private Map<Integer, SatelliteAPI.Status> map;
    private int successfulCounter;

    public SatelliteSupervisor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(SatelliteSupervisor::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::onRequest)
                .onMessage(ResponseOne.class, this::onResponseOne)
                .build();
    }

    private Behavior<Command> onRequest(Request request) {
        this.replyTo = request.replyTo;
        this.queryId = request.queryId;
        this.range = request.range;

        this.counter = request.range;
        this.successfulCounter = 0;
        this.map = new HashMap<>();

        for (int i = 0; i < request.range; i++) {
            getContext()
                    .spawn(SatelliteActor.create(), i + "actor" + request.queryId)
                    .tell(new RequestOne(getContext().getSelf(), request.firstSatelliteId + i, request.timeout));
        }
        return this;
    }

    private Behavior<Command> onResponseOne(ResponseOne responseOne) {
        counter--;
        if (responseOne.wasSuccessful) {
            this.successfulCounter++;
            if (responseOne.status != SatelliteAPI.Status.OK) {
                this.map.put(responseOne.satelliteId, responseOne.status);
            }
        }
        if (counter == 0) {
            this.replyTo.tell(new Response(this.queryId, this.map, this.successfulCounter * 100 / this.range));
            return Behaviors.stopped();
        }
        return this;
    }
}
