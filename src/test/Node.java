package test;

import java.util.*;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
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

    public boolean helpHasCycles(Node node, Set<Node> visited,Set<Node> inPath) {
        if(visited.contains(node))
            return false;
        if(inPath.contains(node))
            return true;

        visited.add(node);
        inPath.add(node);

        for(Node n : node.edges){
            if(!n.helpHasCycles(n,visited,inPath))
                return false;
        }
        inPath.remove(node);
        return true;
    }

    public boolean hasCycles() {
        if (edges == null || edges.isEmpty())
            return false;
        return helpHasCycles(edges.getFirst(), new HashSet<>(),
                new HashSet<>());
    }
}