package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ListIterator;

import test.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node>{

    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles())
                return true;
        }
        return false;
    }


    public void createFromTopics() {
        this.clear();

        TopicManager tm = TopicManagerSingleton.get();
        Collection<Topic> topics = tm.getTopics();

        HashMap<String, Node> nodes = new HashMap<>();

        for (Topic t : topics) {
            String tName = "T" + t.getName();
            nodes.put(tName, new Node(tName));
        }

        for (Topic t : topics) {
            Node topicNode = nodes.get("T" + t.getName());

            for (Agent a : t.subs) {
                String aName = "A" + a.getName();
                nodes.putIfAbsent(aName, new Node(aName));
                Node agentNode = nodes.get(aName);
                topicNode.addEdge(agentNode);
            }

            for (Agent a : t.pubs) {
                String aName = "A" + a.getName();
                nodes.putIfAbsent(aName, new Node(aName));
                Node agentNode = nodes.get(aName);
                agentNode.addEdge(topicNode);
            }
        }

        this.addAll(nodes.values());
    }

}
