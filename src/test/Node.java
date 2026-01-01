package test;

import java.util.*;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void addEdge(Node node) {
        this.edges.add(node);
    }

    private boolean hasCyclesFrom(Node current, Set<Node> visited) {
        if (visited.contains(current))
            return false;

        visited.add(current);

        for (Node next : current.edges) {
            if (next == this)
                return true;

            if (hasCyclesFrom(next, visited))
                return true;
        }
        return false;
    }

    public boolean hasCycles() {
        return hasCyclesFrom(this, new HashSet<>());
    }
}