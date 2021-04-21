package part_2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import part_2.commands.BeginWithParameters;
import part_2.commands.Command;
import part_2.commands.GetDataRequest;

import java.io.File;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        File configFile = new File("/src/main/dispatcher.conf");
        Config config = ConfigFactory.parseFile(configFile);

        ActorSystem.create(Main.create(), "dispatcherSystem", config);
    }

    public static Behavior<Void> create() {
        Random random = new Random();
        return Behaviors.setup(
                context -> {
                    ActorRef<Command> dispatcher = context.spawn(Dispatcher.create(), "dispatcher");
                    ActorRef<Command> database = context.spawn(DatabaseActor.create(), "db");

                    Thread.sleep(2000);

                    ActorRef<Command> station1 = context.spawn(MonitoringStation.create(), "station1");
                    ActorRef<Command> station2 = context.spawn(MonitoringStation.create(), "station2");
                    ActorRef<Command> station3 = context.spawn(MonitoringStation.create(), "station3");

                    station1.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station1.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station2.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station2.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));

                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));
                    station3.tell(new BeginWithParameters(dispatcher, database, 100 + random.nextInt(50), 50, 300));

                    Thread.sleep(40000);
                    station1.tell(new GetDataRequest(database, -1));

                    return Behaviors.receive(Void.class)
                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
                            .build();
                }
        );
    }
}
