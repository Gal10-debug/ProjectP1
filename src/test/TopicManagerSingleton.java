package test;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

    public static class TopicManager{
        public final static TopicManager instance = new TopicManager();
        ConcurrentHashMap<String,Topic> topics = new ConcurrentHashMap<>();

        private TopicManager(){}

        public Topic getTopic(String topic){
            return topics.computeIfAbsent(topic, Topic::new);
        }

        public Collection<Topic> getTopics(){
            return topics.values();
        }

        public void clear (){
            topics.clear();
        }
    }//End of TopicManager class



    public static TopicManager get(){
        return TopicManager.instance;
    }
}
