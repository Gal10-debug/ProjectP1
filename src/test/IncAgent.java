package test;

import test.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {
    String []m_subs ;
    String []m_pubs;
    int value;

    public IncAgent(String []subs, String []pubs) {
        this.m_subs = subs;
        this.m_pubs = pubs;

        Topic t1 = TopicManagerSingleton.get().getTopic(m_subs[0]);
        t1.subscribe(this);
    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        value = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        double val = msg.asDouble;
        if (Double.isNaN(val))
            return;

        double result = val+1;

        TopicManagerSingleton
                .get()
                .getTopic(m_pubs[0])
                .publish(new Message(result));
    }

    @Override
    public void close() {

    }

}
