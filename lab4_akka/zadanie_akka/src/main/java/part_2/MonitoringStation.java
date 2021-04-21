package part_2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_2.commands.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MonitoringStation extends AbstractBehavior<Command> {
    private final Random random = new Random();
    private final int id;
    private final Map<Integer, Long> startTimes;
    private ActorRef<Command> databaseActor = null;


    public MonitoringStation(ActorContext<Command> context) {
        super(context);
        this.id = Math.abs(random.nextInt()) % 10000;
        this.startTimes = new HashMap<>();
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(MonitoringStation::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(BeginWithParameters.class, this::onBeginWithParameters)
                .onMessage(Begin.class, this::onBegin)
                .onMessage(Response.class, this::onResponse)
                .onMessage(GetDataRequest.class, this::onGetDataRequest)
                .onMessage(GetDataResponse.class, this::onGetDataResponse)
                .build();
    }

    private Behavior<Command> onGetDataResponse(GetDataResponse getDataResponse) {
        if (getDataResponse.issues != 0) {
            System.out.println("id: " + getDataResponse.satelliteId + " issues: " + getDataResponse.issues);
        }
        return this;
    }

    private Behavior<Command> onGetDataRequest(GetDataRequest getDataRequest) {
        System.out.println("data request");
        for (int i = 100; i < 200; i++) {
            getDataRequest.actorRef.tell(new GetDataRequest(this.getContext().getSelf(), i));
        }
        return this;
    }

    private Behavior<Command> onBegin(Begin begin) {
        if (this.databaseActor == null) {
            this.databaseActor = begin.database;
        }
        int queryId = id * 100000 + Math.abs(random.nextInt()) % 1000;
        int firstSatellite = 100 + Math.abs(random.nextInt()) % 100;
        int range = Math.abs(random.nextInt()) % 100;
        if (firstSatellite + range >= 200) {
            range = 200 - firstSatellite;
        }
        startTimes.put(queryId, new Date().getTime());
        begin.dispatcher.tell(new Request(this.getContext().getSelf(), queryId, firstSatellite, range, 300));
        return this;
    }

    private Behavior<Command> onBeginWithParameters(BeginWithParameters beginWithParameters) {
        if (this.databaseActor == null) {
            this.databaseActor = beginWithParameters.database;
        }
        int queryId = id * 100000 + Math.abs(random.nextInt()) % 1000;
        Request request = new Request(
                this.getContext().getSelf(),
                queryId,
                beginWithParameters.firstSatelliteId,
                beginWithParameters.range,
                beginWithParameters.timeout);
        startTimes.put(queryId, new Date().getTime());
        beginWithParameters.dispatcher.tell(request);
        return this;
    }

    private Behavior<Command> onResponse(Response response) {
        long responseTime = new Date().getTime() - startTimes.remove(response.queryId);

        StringBuilder builder = new StringBuilder();
        builder.append("------------------------------------------------------\n")
                .append("queryID: ")
                .append(response.queryId)
                .append("\nresponse time: ")
                .append(responseTime)
                .append("\nsuccessful: ")
                .append(response.successful)
                .append("%\nmap size: ")
                .append(response.map.size())
                .append("\nresults:");
        for (Integer key : response.map.keySet()) {
            this.databaseActor.tell(new IncrementRequest(key));
            builder.append("\nsatelliteId: ")
                    .append(key)
                    .append(", status: ")
                    .append(response.map.get(key));
        }
        builder.append("\n------------------------------------------------------");
        System.out.println(builder.toString());
        return this;
    }
}
