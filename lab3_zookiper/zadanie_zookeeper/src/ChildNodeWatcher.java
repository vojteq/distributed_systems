import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ChildNodeWatcher implements Watcher {

    ZooKeeper zooKeeper;
    String zNode;

    public ChildNodeWatcher(ZooKeeper zooKeeper, String zNode) {
        this.zooKeeper = zooKeeper;
        this.zNode = zNode;
        try {
            watchChildren(zNode);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)) {
            try {
                System.out.println("Number of child nodes: " + zooKeeper.getAllChildrenNumber(zNode));
                watchChildren(zNode);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void watchChildren(String zNode) throws KeeperException, InterruptedException {
        if (nodeExists(zNode)) {
            List<String> nodes = zooKeeper.getChildren(zNode, this);
            for (String node : nodes) {
                watchChildren(zNode + "/" + node);
            }
        }
    }

    private boolean nodeExists(String newNodePath) throws KeeperException, InterruptedException {
        return zooKeeper.exists(newNodePath, false) != null;
    }
}
