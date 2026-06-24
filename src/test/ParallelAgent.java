package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent;
    private BlockingQueue<Task> queue;
    private final Thread worker;
    private volatile boolean running;




    private  static class Task{
        String topic;
        Message message;

        public Task(String topic, Message message){
            this.topic = topic;
            this.message= message;
        }
    }

    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(capacity);
        running = true;

        worker = new Thread(()->{
            while (running){
                try{
                    Task task = queue.take();
                    agent.callback(task.topic, task.message);
                }catch (InterruptedException e){
                    break;
                }
            }
        });

        worker.start();
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
        try{
            queue.put(new Task(topic,msg));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        running = false;
        worker.interrupt();
        agent.close();
    }

//    public void start(){
//    }
}
