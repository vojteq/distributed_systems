import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class Printer {
    ZooKeeper zooKeeper;

    public Printer(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void printTree(String currentLocation) {
        try {
            List<String> nodes = zooKeeper.getChildren(currentLocation, false);
            for (String node : nodes) {
                String newNode = currentLocation + "/" + node;
                if (zNodeExists(newNode)) {
                    System.out.println(newNode);
                    printTree(newNode);
                }
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean zNodeExists(String newNodePath) throws KeeperException, InterruptedException {
        return zooKeeper.exists(newNodePath, false) != null;
    }
}
