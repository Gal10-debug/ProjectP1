package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent;

    public ParallelAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
        agent.callback(topic, msg);
    }

    @Override
    public void close() {
        agent.close();
    }

    public void start(){
    }
}
