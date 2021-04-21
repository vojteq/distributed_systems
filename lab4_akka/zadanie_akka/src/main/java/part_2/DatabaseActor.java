package part_2;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part_2.commands.Command;
import part_2.commands.GetDataRequest;
import part_2.commands.GetDataResponse;
import part_2.commands.IncrementRequest;

import java.sql.*;

public class DatabaseActor extends AbstractBehavior<Command> {
    private static Behavior<Command> databaseActor;
    private static Connection connection;

    public DatabaseActor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        if (databaseActor == null) {
            databaseActor = Behaviors.setup(DatabaseActor::new);
        }
        try {

            String url = "jdbc:sqlite:akka.db";
            String drop = "DROP TABLE IF EXISTS 'satellites'";
            String create = "CREATE TABLE IF NOT EXISTS satellites (id INTEGER PRIMARY KEY, issues int)";
            connection = DriverManager.getConnection(url);
            if (connection != null && !connection.isClosed()) {
                System.out.println("CONNECTED");
                Statement statement = connection.createStatement();
                statement.execute(drop);
                statement.execute(create);
                initDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseActor;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(IncrementRequest.class, this::onSaveRequest)
                .onMessage(GetDataRequest.class, this::onGetDataRequest)
                .build();
    }

    private Behavior<Command> onSaveRequest(IncrementRequest incrementRequest) {
        try {
            String select = "SELECT issues FROM satellites WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(select);
            selectStatement.setInt(1, incrementRequest.satelliteId);
            ResultSet set = selectStatement.executeQuery();
            set.next();
            int issues = set.getInt(1) + 1;
            String update = "UPDATE satellites SET issues = ? WHERE id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(update);
            updateStatement.setInt(1, issues);
            updateStatement.setInt(2, incrementRequest.satelliteId);
            updateStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private Behavior<Command> onGetDataRequest(GetDataRequest getDataRequest) {
        int issues = -1;
        try {
            String select = "SELECT issues FROM satellites WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1,getDataRequest.satelliteId);
            ResultSet set = preparedStatement.executeQuery();
            set.next();
            issues = set.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getDataRequest.actorRef.tell(new GetDataResponse(getDataRequest.satelliteId, issues));
        }
        return this;
    }

    private static void initDatabase() {
        String insert = "INSERT INTO satellites(id, issues) VALUES(?,?)";
        try {
            for (int i = 100; i < 200; i++) {
                PreparedStatement preparedStatement = connection.prepareStatement(insert);
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("init error");
            e.printStackTrace();
        }
    }
}
