package part_1;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import part_1.commands.Begin;
import part_1.commands.Command;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File configFile = new File("/src/main/dispatcher.conf");
        Config config = ConfigFactory.parseFile(configFile);

        ActorSystem.create(Main.create(), "dispatcherSystem", config);
    }

    public static Behavior<Void> create() {
        return Behaviors.setup(
                context -> {
                    ActorRef<Command> dispatcher = context.spawn(Dispatcher.create(), "dispatcher");

                    Thread.sleep(2000);

                    ActorRef<Command> station1 = context.spawn(MonitoringStation.create(), "station1");
                    ActorRef<Command> station2 = context.spawn(MonitoringStation.create(), "station2");
                    ActorRef<Command> station3 = context.spawn(MonitoringStation.create(), "station3");

                    station1.tell(new Begin(dispatcher));
                    station1.tell(new Begin(dispatcher));
                    station2.tell(new Begin(dispatcher));
                    station2.tell(new Begin(dispatcher));
                    station3.tell(new Begin(dispatcher));
                    station3.tell(new Begin(dispatcher));

                    return Behaviors.receive(Void.class)
                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
                            .build();
                }
        );
    }
}
