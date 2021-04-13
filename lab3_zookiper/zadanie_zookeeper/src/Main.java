import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String log4jConfPath = "C:\\Users\\T\\Documents\\STUDIA\\6semestr\\rozproszone\\lab3_zookiper\\apache-zookeeper-3.6.1-bin\\conf\\log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        String host = "127.0.0.1:2181";
        String zNode = "/z";
        String commandToBeExecuted = "notepad.exe";

        ZooKeeper zooKeeper = new ZooKeeper(host,2000,  null);
        zooKeeper.exists(zNode, new MainNodeWatcher(zooKeeper, zNode, commandToBeExecuted));
        Printer printer = new Printer(zooKeeper);

        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!input.equals("e")) {
            input = scanner.nextLine();
            System.out.println("\nPRINTING CURRENT TREE");
            printer.printTree(zNode);
        }
    }
}
