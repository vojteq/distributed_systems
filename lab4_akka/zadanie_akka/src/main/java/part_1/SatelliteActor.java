package part_1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_1.commands.Command;
import part_1.commands.Request;
import part_1.commands.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                .onMessage(Request.class, this::onRequest)
                .build();
    }

    private Behavior<Command> onRequest(Request request) {
        Map<Integer, SatelliteAPI.Status> map = new HashMap<>();
        SatelliteAPI.Status status;
        int currentSatellite, successful = 0;
        long startTime, responseTime;
        for (int i = 0; i < request.range; i++) {
            currentSatellite = request.firstSatelliteId + i;
            startTime = new Date().getTime();
            status = SatelliteAPI.getStatus(currentSatellite);
            responseTime = new Date().getTime() - startTime;
            if (responseTime <= request.timeout) {
                successful++;
                if (status != SatelliteAPI.Status.OK) {
                    map.put(currentSatellite, status);
                }
            }
        }
        request.replyTo.tell(new Response(request.queryId, map, successful * 100 / request.range));
        return Behaviors.stopped();
    }
}
