import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class MainNodeWatcher implements Watcher {

    private final ZooKeeper zooKeeper;
    private final String zNode;
    private final ChildNodeWatcher childNodeWatcher;
    private final ProcessBuilder processBuilder;
    private Process app;

    public MainNodeWatcher(ZooKeeper zooKeeper, String zNode, String commandToBeExecuted) {
        this.zooKeeper = zooKeeper;
        this.zNode = zNode;
        this.childNodeWatcher = new ChildNodeWatcher(zooKeeper, zNode);
        this.processBuilder = new ProcessBuilder(commandToBeExecuted);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            switch (watchedEvent.getType()) {
                case NodeCreated -> {
                    System.out.println("MAIN NODE CREATED");
                    zooKeeper.getChildren(zNode, childNodeWatcher);
                    app = processBuilder.start();
                }
                case NodeDeleted -> {
                    System.out.println("MAIN NODE DELETED");
                    if (app != null) {
                        app.destroy();
                        if (app.isAlive()) {
                            app.destroyForcibly();
                        }
                    }
                }
            }
            zooKeeper.exists(zNode, this);
        } catch (KeeperException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
