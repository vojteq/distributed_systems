package Z1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MathActorMultiply extends AbstractBehavior<MathActor.MathCommandMultiply> {
    private int operationCount;

    // --- use messages from MathActor -> no need to define new ones

    // --- constructor and create
    public MathActorMultiply(ActorContext<MathActor.MathCommandMultiply> context) {
        super(context);
        this.operationCount = 0;
    }

    public static Behavior<MathActor.MathCommandMultiply> create() {
        return Behaviors.setup(MathActorMultiply::new);
    }

    // --- define message handlers
    @Override
    public Receive<MathActor.MathCommandMultiply> createReceive() {
        return newReceiveBuilder()
                .onMessage(MathActor.MathCommandMultiply.class, this::onMathCommandMultiply)
                .build();
    }

    private Behavior<MathActor.MathCommandMultiply> onMathCommandMultiply(MathActor.MathCommandMultiply mathCommandMultiply) {
//        System.out.println("actorMultiply: received command: multiply");
        int result = mathCommandMultiply.firstNumber * mathCommandMultiply.secondNumber;
        System.out.println("actorMultiply: multiply result = " + result + " (count: " + ++operationCount + ")");
//        System.out.println("actorMultiply: sending response");
        mathCommandMultiply.replyTo.tell(new MathActor.MathCommandResult(result));
        return this;
    }
}
