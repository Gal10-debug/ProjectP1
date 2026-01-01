package test;

import java.util.ArrayList;

public class Topic implements  Agent {
    public final String name;
    final ArrayList<Agent> subs;
    final ArrayList<Agent> pubs;

    Topic(String name){
        this.name=name;
        this.subs=new ArrayList<>();
        this.pubs=new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reset() {}

    @Override
    public void callback(String topic,Message msg){

    }

    @Override
    public void close(){}



    public void subscribe(Agent a){
        for(Agent agent:subs){
            if(a.getName().equals(agent.getName()))
                return;
        }
        subs.add(a);
    }
    public void unsubscribe(Agent a){
        for(Agent agent:subs){
            if(agent.getName().equals(a.getName())) {
                subs.remove(a);
                return;
            }
        }
    }

    public void publish(Message m){
        for(Agent a:subs){
            a.callback(this.name,m);
        }
    }

    public void addPublisher(Agent a){
        for(Agent agent:pubs){
            if(agent.getName().equals(a.getName()))
                return;
        }
        pubs.add(a);
    }

    public void removePublisher(Agent a){
        for(Agent agent:pubs){
            if(agent.getName().equals(a.getName())) {
                pubs.remove(a);
                return;
            }
        }
    }


}
