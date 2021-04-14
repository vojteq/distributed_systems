package Z2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;

import java.util.LinkedList;
import java.util.List;

public class ActorTextService extends AbstractBehavior<ActorTextService.Command> {

    // --- messages
    interface Command {
    }

    // TODO: new message type implementing Command, with Receptionist.Listing field

    public static class ReceptionistMessage implements Command {
        final Receptionist.Listing listing;

        public ReceptionistMessage(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }

    public static class Request implements Command {
        final String text;

        public Request(String text) {
            this.text = text;
        }
    }


    // --- constructor and create
    // TODO: field for message adapter
//    private ActorRef<Receptionist.Listing> adapter;
    private ActorRef<Receptionist.Listing> adapter;
    private ServiceKey<Command> serviceKey = ServiceKey.create(Command.class, "key");
    private List<ActorRef<String>> workers = new LinkedList<>();

    public ActorTextService(ActorContext<ActorTextService.Command> context) {
        super(context);

        // TODO: create message adapter
        this.adapter = context.messageAdapter(Receptionist.Listing.class, ReceptionistMessage::new);
        // TODO: subscribe to receptionist (with message adapter)
//        ServiceKey<ReceptionistMessage> serviceKey = ServiceKey.create(ReceptionistMessage.class, "key");
//        context.getSystem().receptionist().tell(Receptionist.register(ServiceKey.create(ReceptionistMessage.class, "key"), context.getSelf()));
//        context.getSystem().receptionist().tell(Receptionist.register(serviceKey, context.getSelf()));
        Receptionist.subscribe(serviceKey, adapter);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(ActorTextService::new);
    }

    // --- define message handlers
    @Override
    public Receive<Command> createReceive() {

        System.out.println("creating receive for text service");

        return newReceiveBuilder()
                .onMessage(Request.class, this::onRequest)
                // TODO: handle the new type of message
                .onMessage(ReceptionistMessage.class, response -> onReceptionistMessage(response.listing))
//                .onMessage(Receptionist.Listing.class, this::onReceptionistMessage))
                .build();
    }

    private Behavior<Command> onRequest(Request msg) {
        System.out.println("request: " + msg.text);
        for (ActorRef<String> worker : workers) {
            System.out.println("sending to worker: " + worker);
            worker.tell(msg.text);
        }
        return this;
    }

    // TODO: handle the new type of message
//    private Behavior<Command> onReceptionistMessage(ReceptionistMessage msg) {
//        System.out.println("receptionist message: key: " + msg.listing.getKey() + " listing size: " + msg.listing.getAllServiceInstances(serviceKey).size());
////        msg.listing.getServiceInstances(serviceKey).forEach(instance -> getContext().spawnAnonymous());
////        workers = msg.listing.getServiceInstances(serviceKey);
////        for (ActorRef<> ref: msg.listing.getServiceInstances(serviceKey)) {
////
////        }
//        return this;
//    }
    private Behavior<Command> onReceptionistMessage(Receptionist.Listing listing) {
        System.out.println("receptionist message: key: " + listing.getKey() + " listing size: " + listing.getAllServiceInstances(serviceKey).size());
//        workers = listing.getServiceInstances(serviceKey);
        return this;
    }

}
