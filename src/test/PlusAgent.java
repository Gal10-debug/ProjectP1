package test;

public class PlusAgent implements Agent{
    String []m_subs ;
    String []m_pubs;
    double x ;
    double y ;

    public PlusAgent(String[] subs, String[] pubs){
        m_subs = subs;
        m_pubs = pubs;

        if(m_subs.length>=2){
            Topic t1 = TopicManagerSingleton.get().getTopic(m_subs[0]);
            Topic t2 = TopicManagerSingleton.get().getTopic(m_subs[1]);

            t1.subscribe(this);
            t2.subscribe(this);
        }
    }

    @Override
    public String getName(){
        return "PlusAgent";
    }

    @Override
    public void reset(){
        x = 0.0;
        y = 0.0;
    }

    @Override
    public void callback(String topic, Message msg){
        double value = msg.asDouble;
        if(Double.isNaN(value))
            return;

        if(topic.equals(m_subs[0])){
            this.x = value;
        }
        else if(topic.equals(m_subs[1])){
            this.y = value;
        }

        double result = x+y;
        TopicManagerSingleton
                .get()
                .getTopic(m_pubs[0])
                .publish(new Message(result));
    }

    @Override
    public void close(){
    }

}
